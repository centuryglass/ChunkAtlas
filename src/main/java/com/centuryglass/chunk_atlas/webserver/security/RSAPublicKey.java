/**
 * @file RSAPublicKey.java
 * 
 * Manages the public key in an RSA encryption key pair.
 */
package com.centuryglass.chunk_atlas.webserver.security;

import com.centuryglass.chunk_atlas.config.LogConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;

/** 
 * Manages the public key in an RSA encryption key pair.
 */
public class RSAPublicKey extends RSAKey
{
    private static final String CLASSNAME = RSAPublicKey.class.getName();
    
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
        final String FN_NAME = "initKey";
        try
        {
            Path keyPath = keyFile.toPath();
            byte[] keyBytes = Files.readAllBytes(keyPath);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(keySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e)
        {
            // Creating a RSA key should never actually throw these exceptions
            LogConfig.getLogger().logp(Level.SEVERE, CLASSNAME, FN_NAME,
                    e.toString());
            System.exit(1);
        }
        catch (IOException e)
        {
            throw new IOException("Unable to read RSA public key file.");
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
    
    /**
     * Verifies that a message signature was created by the private key paired
     * with this public key.
     * 
     * @param signature             The signature to verify.
     * 
     * @param message               The signed message data.
     * 
     * @return                      Whether the signature was valid.
     * 
     * @throws InvalidKeyException  If this object is not a valid public key.
     * 
     * @throws SignatureException   If the signature parameter was not a valid
     *                              signature.
     */
    public boolean verify(byte[] signature, byte[] message)
            throws InvalidKeyException, SignatureException
    
    {
        final String FN_NAME = "verify";
        Signature sign;
        try
        {
            sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify((PublicKey) getKey());
            sign.update(message);
            return sign.verify(signature);
        }
        catch (NoSuchAlgorithmException e)
        {
            // This should never run, "SHA256withRSA" should always be valid.
            LogConfig.getLogger().logp(Level.SEVERE, CLASSNAME, FN_NAME,
                    e.toString());
            System.exit(1);
        }
        return false;
    }
}
