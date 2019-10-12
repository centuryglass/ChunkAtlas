/**
 * @file RSAPrivateKey.java
 * 
 * Manages the private key in an RSA encryption key pair.
 */
package com.centuryglass.mcmap.webserver.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * Manages the public key in an RSA encryption key pair.
 */
public class RSAPrivateKey extends RSAKey
{
    /**
     * Initializes a private key from saved key values.
     * 
     * @param keyFile       A file storing a private key.
     * 
     * @return              A key object, created from that file's data.
     * 
     * @throws IOException  If unable to properly read the key from the key
     *                      file. 
     */
    private static Key initKey(File keyFile) throws IOException
    {
        Path keyPath = keyFile.toPath();
        byte[] keyBytes = Files.readAllBytes(keyPath);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory;
        try
        {
            factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(keySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e)
        {
            // Creating a RSA key should never actually throw these exceptions
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
    
    /**
     * Initializes a private key from a binary file.
     * 
     * @param keyFile       A file holding a binary private key.
     * 
     * @throws IOException  If unable to properly read the key from the key
     *                      file. 
     */
    public RSAPrivateKey(File keyFile) throws IOException
    {
        super(initKey(keyFile));
    }
    
    /**
     * Uses this private key to sign a byte array message.
     * 
     * @param message               Message data to sign.
     * 
     * @return                      The signed message.
     * 
     * @throws InvalidKeyException  If this is not a valid private key.
     * 
     * @throws SignatureException   If unable to sign the given message data.
     */
    public byte[] sign(byte[] message) throws InvalidKeyException,
            SignatureException
    {
        Signature sign;
        try
        {
            sign = Signature.getInstance("SHA256withRSA");
            sign.initSign((PrivateKey) getKey());
            sign.update(message);
            return sign.sign();
        }
        catch (NoSuchAlgorithmException e)
        {
            // This should never run, "SHA256withRSA" should always be valid.
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
