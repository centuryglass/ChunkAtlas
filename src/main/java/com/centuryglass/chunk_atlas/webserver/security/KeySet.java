/**
 * @file KeySet.java
 * 
 * Manages RSA encryption keys used to validate communications with the web
 * server.
 */
package com.centuryglass.chunk_atlas.webserver.security;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SignatureException;

/**
 * Manages RSA encryption keys used to validate communications with the web
 * server.
 */
public class KeySet
{
    /**
     * Loads required RSA keys from files.
     * 
     * @param privateKeyFile    A file storing this application's private key.
     * 
     * @param publicKeyFile     A file storing this application's public key.
     * 
     * @param webPublicKeyFile  A file storing the web server's public key.
     * 
     * @throws IOException      If unable to read from key files. 
     */
    public KeySet(File privateKeyFile, File publicKeyFile,
            File webPublicKeyFile) throws IOException
    {
        privateKey = new RSAPrivateKey(privateKeyFile);
        publicKey = new RSAPublicKey(publicKeyFile);
        webServerPublic = new RSAPublicKey(webPublicKeyFile);   
    }
    
    /**
     *  Uses this application's private key to sign a message data array.
     * 
     * @param message  A message data array to sign.
     * 
     * @return         The message data signature.
     */
    byte[] createMessageSignature(byte[] message) throws InvalidKeyException,
            SignatureException
    {
        return privateKey.sign(message);
    }
    
    /**
     * Creates an encrypted message that can only be decrypted with the web
     * server's private key.
     * 
     * @param message  A message data array to encrypt.
     * 
     * @return         The encrypted message data.
     */
    byte[] createEncryptedMessage(byte[] message)
            throws GeneralSecurityException
    {
        return webServerPublic.encrypt(message);
    }
    
    
    /**
     * Verifies that a message was signed by this application.
     * 
     * @param signature             The signature to verify.
     * 
     * @param message               The signed message data.
     * 
     * @return                      Whether the signature was valid.
     * 
     * @throws InvalidKeyException  If this application's public key is
     *                              invalid.
     * 
     * @throws SignatureException   If the signature parameter was not a valid
     *                              signature.
     */
    boolean verifyLocallySignedMessage(byte[] signature, byte[] message)
            throws InvalidKeyException, SignatureException
    {   
        return publicKey.verify(signature, message);
    }
    
    /**
     * Verifies that a message was signed by the web server.
     * 
     * @param signature             The signature to verify.
     * 
     * @param message               The signed message data.
     * 
     * @return                      Whether the signature was valid.
     * 
     * @throws InvalidKeyException  If the web server's public key is invalid.
     * 
     * @throws SignatureException   If the signature parameter was not a valid
     *                              signature.
     */
    boolean verifyRemoteSignedMessage(byte[] signature, byte[] message)
            throws InvalidKeyException, SignatureException
    {   
        return webServerPublic.verify(signature, message);
    }
        
    /**
     * Decodes an encrypted message from the web server.
     * 
     * @param message  Data from the web server that needs to be decrypted with
     *                 this application's private key.
     * 
     * @return         The decrypted message data, or null if the given
     *                 message couldn't be decrypted with the application's
     *                 private key.
     */
    byte[] decryptMessage(byte[] message) throws GeneralSecurityException
    {   
        return privateKey.decrypt(message);
    } 
    
    // This application's private key:
    private final RSAPrivateKey privateKey;
    // This application's public key:
    private final RSAPublicKey publicKey;
    // The web server's public key:
    private final RSAPublicKey webServerPublic;
    
}
