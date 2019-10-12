/**
 * @file RSAPublicKey.java
 * 
 * Manages the public key in an RSA encryption key pair.
 */
package com.centuryglass.mcmap.webserver.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/** 
 * Manages the public key in an RSA encryption key pair.
 */
public class RSAPublicKey extends RSAKey
{
    /**
     * Initializes a public key from saved key values.
     * 
     * @param keyFile       A file storing a public key.
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
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory factory;
        try
        {
            factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(keySpec);
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
     * Initializes a public key from a binary file.
     * 
     * @param keyFile       A file holding a binary public key.
     * 
     * @throws IOException  If unable to properly read the key from the key
     *                      file. 
     */
    public RSAPublicKey(File keyFile) throws IOException
    {
        super(initKey(keyFile));
    }  
}
