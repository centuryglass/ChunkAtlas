/**
 * @file RSAGenerator
 * 
 * Generates a valid RSA key pair and saves each key to a file.
 */
package com.centuryglass.chunk_atlas.webserver.security;

import com.centuryglass.chunk_atlas.config.LogConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

/** 
 * Generates a valid RSA key pair and saves each key to a file.
 */
public class RSAGenerator
{
    private static final String CLASSNAME = RSAGenerator.class.getName();
    
    /**
     * Generates the RSA key pair and saves it to a pair of files.
     * 
     * @param publicKeyFile   The file where the public key will be saved.
     * 
     * @param privateKeyFile  The file where the private key will be saved.
     * 
     * @throws IOException    If unable to properly write key data to either
     *                        file.
     */
    public static void generate(File publicKeyFile, File privateKeyFile)
            throws IOException
    {
        final String FN_NAME = "generate";
        try
        {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.genKeyPair();
            Key publicKey = keyPair.getPublic();
            Key privateKey = keyPair.getPrivate();
            try (FileOutputStream out = new FileOutputStream(publicKeyFile))
            {
                out.write(publicKey.getEncoded());
                LogConfig.getLogger().logp(Level.INFO, CLASSNAME, FN_NAME,
                        "Wrote public key in format {0}.",
                        publicKey.getFormat());
            }
            try (FileOutputStream out = new FileOutputStream(privateKeyFile))
            {
                out.write(privateKey.getEncoded());
                LogConfig.getLogger().logp(Level.INFO, CLASSNAME, FN_NAME,
                        "Wrote private key in format {0}.",
                        privateKey.getFormat());
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            // This shouldn't ever actually be thrown, "RSA" should always be
            // a valid algorithm.
            LogConfig.getLogger().logp(Level.SEVERE, CLASSNAME, FN_NAME,
                    e.toString());
            System.exit(1);
        }
    }
}
