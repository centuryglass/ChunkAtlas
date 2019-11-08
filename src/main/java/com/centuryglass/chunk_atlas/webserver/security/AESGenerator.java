/**
 * @file AESGenerator.java
 * 
 * Generates random 256-bit AES encryption keys.
 */
package com.centuryglass.chunk_atlas.webserver.security;

import com.centuryglass.chunk_atlas.config.LogConfig;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/** 
 * Generates random 256-bit AES encryption keys.
 */
public class AESGenerator
{
    private static final String CLASSNAME = AESGenerator.class.getName();
    
    /**
     * Generates and returns a new 256 bit symmetric encryption key using the
     * AES standard.
     * 
     * @return  The new randomly generated key. 
     */
    public static SecretKey generate()
    {
        final String FN_NAME = "generate";
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
            LogConfig.getLogger().logp(Level.SEVERE, CLASSNAME, FN_NAME,
                    e.toString());
            System.exit(1);
        }
        return null;
    }   
}
