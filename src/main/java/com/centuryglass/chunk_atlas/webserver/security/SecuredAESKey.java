/**
 * @file SecuredAESKey.java
 * 
 * Uses RSA encryption to sign and encrypt an AES encryption key.
 */
package com.centuryglass.chunk_atlas.webserver.security;


import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import com.sun.org.apache.xml.internal.security.exceptions
        .Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.Validate;

/** 
 *  Uses RSA encryption to sign and encrypt an AES encryption key. Secured keys
 * are shared as 512 bytes of data.
 * 
 *  Secured AES keys are protected with RSA encryption to ensure that only the
 * expected recipient can decode the key, and to prove to the recipient that
 * the key came from the expected sender.
 * 
 *  The first 256 bytes are the SHA256 signature field, used to verify that the
 * rest of the message was signed by the appropriate message source.
 * 
 *  The last 256 bytes are the encrypted AES key. Decrypting this with the
 * intended recipient's private RSA key will produce a valid AES key value
 * encoded into a byte array. This key should be used by both the sender and
 * the recipient to encrypt further communications.
 */
public class SecuredAESKey
{
    private static final String CLASSNAME = SecuredAESKey.class.getName();
    
    // Expected size in bytes of secured key data:
    private static final int BYTE_SIZE = 512;
    
    /**
     * Creates a signed and encrypted version of an AES key suitable for
     * sending to a single recipient.
     * 
     * @param aesKey                     The AES key to send.
     * 
     * @param rsaKeys                    An object managing the sender's public
     *                                   and private keys, and the recipient's
     *                                   public key.
     * 
     * @throws GeneralSecurityException  If any of the RSA keys involved were
     *                                   invalid.
     */
    public SecuredAESKey(SecretKey aesKey, KeySet rsaKeys)
            throws GeneralSecurityException
    {
        Validate.notNull(aesKey, "AES key cannot be null.");
        Validate.notNull(rsaKeys, "RSA keys cannot be null.");
        this.aesKey = aesKey;
        byte[] keyBytes = aesKey.getEncoded();
        encryptedKey = rsaKeys.createEncryptedMessage(keyBytes);
        signature = rsaKeys.createMessageSignature(encryptedKey);
    }
    
    /**
     * Reads a signed and encrypted AES key addressed to this application.
     * 
     * @param securedKey            A signed and encoded AES key, encoded in 
     *                              base 64.
     * 
     * @param rsaKeys               An object managing this application's key
     *                              pair, along with the public key from the
     *                              expected key sender.
     * 
     * @throws InvalidKeyException  If the key was not encoded in the expected
     *                              format, if the signature was not correct
     *                              for the expected key sender, or if
     *                              decrypted key data was not a valid RSA key.
     * 
     * @throws SignatureException   If the message's signature field was not a
     *                              valid signature.
     */
    public SecuredAESKey(String securedKey, KeySet rsaKeys)
            throws InvalidKeyException, SignatureException
    {
        final String FN_NAME = "SecuredAESKey";
        ExtendedValidate.notNullOrEmpty(securedKey, "Secured key string");
        Validate.notNull(rsaKeys, "RSA keys cannot be null.");
        byte[] keyData;
        try
        {
            keyData = Base64.decode(securedKey);
        }
        catch (Base64DecodingException e)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Error reading key bytes:", e);
            aesKey = null;
            signature = null;
            encryptedKey = null;
            return;
        }
        if (keyData.length != BYTE_SIZE)
        {
            throw new InvalidKeyException("Invalid key data of length "
                + keyData.length + ", expected " + BYTE_SIZE + ".");
        }
        byte[] remoteSignature = Arrays.copyOfRange(keyData, 0, BYTE_SIZE / 2);
        byte[] remoteEncryptedKey = Arrays.copyOfRange(keyData, BYTE_SIZE / 2,
                BYTE_SIZE);
        
        if (! rsaKeys.verifyRemoteSignedMessage(remoteSignature,
                remoteEncryptedKey))
        {
            throw new InvalidKeyException(
                    "Failed to validate signature from " 
                    + remoteSignature.length + "-byte signature field");
        }
        try
        {
            byte[] decryptedKey = rsaKeys.decryptMessage(remoteEncryptedKey);
            aesKey = new SecretKeySpec(decryptedKey, "AES");
            encryptedKey = rsaKeys.createEncryptedMessage(aesKey.getEncoded());
        }
        catch (GeneralSecurityException e)
        {
            throw new InvalidKeyException("Error decrypting secured AES key: "
                    + e.getMessage());
        }
        signature = rsaKeys.createMessageSignature(encryptedKey);
    }
    
    /**
     * Gets the secured key data, base-64 encoded into a String.
     * 
     * @return  The encoded key, ready to send to the expected recipient. 
     */
    public String getSecuredKeyString()
    {
        byte[] keyData = new byte[signature.length + encryptedKey.length];
        System.arraycopy(signature, 0, keyData, 0, signature.length);
        System.arraycopy(encryptedKey, 0, keyData, signature.length,
                encryptedKey.length);
        return Base64.encode(keyData);
    }
    
    /**
     * Encrypts a message string using the AES key.
     * 
     * @param message  A message data array to encrypt.
     * 
     * @return         The encrypted message data bytes, or null if any
     *                 encryption errors occur.
     */
    public byte[] encryptMessage(byte[] message)
    {
        final String FN_NAME = "encryptMessage";
        Validate.notNull(message, "Message cannot be null.");
        try
        {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return cipher.doFinal(message);
        } 
        catch ( InvalidKeyException | NoSuchAlgorithmException
                | BadPaddingException | IllegalBlockSizeException
                | NoSuchPaddingException e)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Error encrypting message:", e);
        }
        return null;
    }
    
    /**
     * Decrypts a message using the AES key.
     * 
     * @param message  A set of encrypted message bytes.
     * 
     * @return         The decrypted message data array, or null if unable to
     *                 decrypt the message. 
     */
    public byte[] decryptMessage(byte[] message)
    {
        final String FN_NAME = "decryptMessage";
        try
        {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return cipher.doFinal(message);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException
                | BadPaddingException | IllegalBlockSizeException
                | NoSuchPaddingException e)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Error decrypting message:", e);
        }
        return null;
    }
    
    // The securely shared AES encryption key:
    private final SecretKey aesKey;
    // The 256-byte RSA-signed signature:
    private final byte[] signature;
    // The 256-byte RSA-encrypted AES key:
    private final byte[] encryptedKey;
}
