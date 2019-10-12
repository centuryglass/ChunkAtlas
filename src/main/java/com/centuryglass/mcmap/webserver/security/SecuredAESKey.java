/**
 * @file SecuredAESKey.java
 * 
 * Uses RSA encryption to sign and encrypt an AES encryption key.
 */
package com.centuryglass.mcmap.webserver.security;


import com.centuryglass.mcmap.util.ExtendedValidate;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.Validate;

/** 
 *  Uses RSA encryption to sign and encrypt an AES encryption key. Secured keys 
 * are shared as 512 bytes of data, base-64 encoded into a string.
 * 
 *  Secured AES keys are protected with RSA encryption to ensure that only the
 * expected recipient can decode the key, and to prove to the recipient that
 * the key came from the expected sender.
 * 
 *  The first 256 bytes are the signature field. When decrypted with the
 * expected key source's RSA public key, the result should be the first eight
 * bytes of the encrypted key. If the result is as expected, the message must
 * have been signed with the expected key source's private key. If the signature
 * is not valid, the key will be discarded.
 * 
 *  The last 256 bytes are the encrypted AES key. Decrypting this with the
 * intended recipient's private RSA key will produce a valid AES key value
 * encoded into a byte array. This key should be used by both the sender and
 * the recipient to encrypt further communications.
 */
public class SecuredAESKey
{
    // The number of bytes to copy from the start of the encrypted key when
    // creating the signature:
    private static final int SIGNED_BYTE_COUNT = 8;
    // Charset used when encoding or decoding String data:
    private static final String CHARSET = "UTF-8";
    // Expected size in bytes of secured key data:
    private static final int BYTE_SIZE = 512;
    
    /**
     * Creates a signed and encrypted version of an AES key suitable for sending
     * to a single recipient.
     * 
     * @param aesKey   The AES key to send.
     * 
     * @param rsaKeys  An object managing the sender's public and private keys,
     *                 and the recipient's public key.
     */
    public SecuredAESKey(SecretKey aesKey, KeySet rsaKeys)
    {
        Validate.notNull(aesKey, "AES key cannot be null.");
        Validate.notNull(rsaKeys, "RSA keys cannot be null.");
        this.aesKey = aesKey;
        byte[] keyBytes = aesKey.getEncoded();
        encryptedKey = rsaKeys.createEncryptedMessage(keyBytes);
        byte[] signedBytes = Arrays.copyOfRange(encryptedKey, 0,
                SIGNED_BYTE_COUNT);
        signature = rsaKeys.createSignedData(signedBytes);
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
     *                              for the expected key sender, or if decrypted
     *                              key data was not a valid RSA key.
     */
    public SecuredAESKey(String securedKey, KeySet rsaKeys)
            throws InvalidKeyException
    {
        ExtendedValidate.notNullOrEmpty(securedKey, "Secured key string");
        Validate.notNull(rsaKeys, "RSA keys cannot be null.");
        byte[] decryptedKey;
        byte[] keyData;
        try
        {
            keyData = Base64.decode(securedKey);
        }
        catch (Base64DecodingException e)
        {
            System.err.println("Error reading key bytes: " + e.getMessage());
            aesKey = null;
            signature = null;
            encryptedKey = null;
            return;
        }
        if (keyData.length != BYTE_SIZE)
        {
            throw new InvalidKeyException("Invalid key data of length "
                + String.valueOf(keyData.length) + ", expected "
                + String.valueOf(BYTE_SIZE) + ".");
        }
        signature = Arrays.copyOfRange(keyData, 0, BYTE_SIZE / 2);
        encryptedKey = Arrays.copyOfRange(keyData, BYTE_SIZE / 2,
                BYTE_SIZE);
        byte[] decodedSignature
                = rsaKeys.readRemoteSignedMessage(signature);
        if (decodedSignature == null)
        {
            throw new InvalidKeyException("Failed to validate signature from "
                    + String.valueOf(signature.length)
                    + "-byte signature field");
        }
        if (decodedSignature.length != SIGNED_BYTE_COUNT)
        {
            throw new InvalidKeyException("Expected a signature of length "
                    + String.valueOf(SIGNED_BYTE_COUNT)
                    + ", but found "
                    + String.valueOf(decodedSignature.length) + " bytes.");
        }
        decryptedKey = rsaKeys.decryptMessage(encryptedKey);
        if (decryptedKey == null)
        {
            throw new InvalidKeyException("Unable to decrypt AES key from "
                    + "encrypted field of length "
                    + String.valueOf(encryptedKey.length) + ".");
        }
        if (! Arrays.equals(decodedSignature, Arrays.copyOf(encryptedKey,
                SIGNED_BYTE_COUNT)))
        {
            throw new InvalidKeyException("Invalid signature, this key "
                    + "probably did not come from the expected source.");
        }
        aesKey = new SecretKeySpec(decryptedKey, "AES");
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
     * @param message  A message string to encrypt.
     * 
     * @return         The encrypted UTF-8 message data base-64 encoded into a
     *                 string, or null if any encryption errors occur.
     */
    public String encryptMessage(String message)
    {
        Validate.notNull(message, "Message cannot be null.");
        try
        {
            byte[] messageBytes = message.getBytes(CHARSET);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encryptedBytes = cipher.doFinal(messageBytes);
            return Base64.encode(encryptedBytes);
        } 
        catch (UnsupportedEncodingException | InvalidKeyException
                | NoSuchAlgorithmException | BadPaddingException
                | IllegalBlockSizeException | NoSuchPaddingException e)
        {
            System.err.println("Error encrypting message: " + e.getMessage());
            System.err.println("Error type: " + e.getClass().getName());
        }
        return null;
    }
    
    /**
     * Decrypts a message using the AES key.
     * 
     * @param message  A set of encrypted message bytes that have been base-64
     *                 encoded into a string.
     * 
     * @return         The decrypted message data, as a UTF-8 string. 
     */
    public String decryptMessage(String message)
    {
        try
        {
            byte[] encryptedBytes = Base64.decode(message);      
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] messageBytes = cipher.doFinal(encryptedBytes);
            return new String(messageBytes, CHARSET);   
        }
        catch (Base64DecodingException | UnsupportedEncodingException
                | InvalidKeyException | NoSuchAlgorithmException
                | BadPaddingException | IllegalBlockSizeException
                | NoSuchPaddingException e)
        {
            System.err.println("Error decrypting message: " + e.getMessage());
            System.err.println("Error type: " + e.getClass().getName());
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