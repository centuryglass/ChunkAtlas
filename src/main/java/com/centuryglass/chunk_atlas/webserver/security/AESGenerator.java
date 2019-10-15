/**
 * @file AESGenerator.java
 * 
 * Generates random 256-bit AES encryption keys.
 */
package com.centuryglass.chunk_atlas.webserver.security;

import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/** 
 * Generates random 256-bit AES encryption keys.
 */
public class AESGenerator
{
    /**
     * Generates and returns a new 256 bit symmetric encryption key using the
     * AES standard.
     * 
     * @return  The new randomly generated key. 
     */
    public static SecretKey generate()
    {
        try
        {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            return generator.generateKey();
        }
        catch (NoSuchAlgorithmException e)
        {
            // This shouldn't ever actually be thrown, "AES" should always be
            // a valid algorithm.
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }   
}
