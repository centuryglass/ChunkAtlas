/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centuryglass.chunk_atlas.webserver.security;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author anthony
 */
public class SecuredAESKeyTest
{
    public static final int LOCAL = 0;
    public static final int REMOTE = 1;
    
    private static final KeySet[] KEY_SETS = { null, null };
    
    private static final SecuredAESKey[] SECURED_AES = { null, null };
    
    private static final String TEST_MESSAGE = "Lorem ipsum dolor sit amet, co"
            + "nsectetur adipiscing elit, sed do eiusmod tempor incididunt ut "
            + "labore et dolore magna aliqua. Ut enim ad minim veniam, quis no"
            + "strud exercitation ullamco laboris nisi ut aliquip ex ea commod"
            + "o consequat. Duis aute irure dolor in reprehenderit in voluptat"
            + "e velit esse cillum dolore eu fugiat nulla pariatur. Excepteur "
            + "sint occaecat cupidatat non proident, sunt in culpa qui officia"
            + "deserunt mollit anim id est laborum.";
    private static final byte[] TEST_MESSAGE_BYTES;
    static
    {
        TEST_MESSAGE_BYTES = TEST_MESSAGE.getBytes(Charset.forName("UTF8"));
    }
    
    private static File createTemp() throws IOException
    {
        return File.createTempFile("aesTest", "key");
    }
    
    @BeforeAll
    public static void setUpClass() throws IOException
    {
        try
        {
            File[] tempPublic = { createTemp(), createTemp() };
            File[] tempPrivate = { createTemp(), createTemp() };
            RSAGenerator.generate(tempPublic[LOCAL], tempPrivate[LOCAL]);
            RSAGenerator.generate(tempPublic[REMOTE], tempPrivate[REMOTE]);
            KEY_SETS[LOCAL] = new KeySet(tempPrivate[LOCAL], tempPublic[LOCAL],
                    tempPublic[REMOTE]);
            KEY_SETS[REMOTE] = new KeySet(tempPrivate[REMOTE],
                    tempPublic[REMOTE], tempPublic[LOCAL]);
            SecretKey aesLocal = AESGenerator.generate();
            SecretKey aesRemote = AESGenerator.generate();
            SECURED_AES[LOCAL] = new SecuredAESKey(aesLocal, KEY_SETS[LOCAL]);
            SECURED_AES[REMOTE] = new SecuredAESKey(aesRemote,
                    KEY_SETS[REMOTE]);
            for (File f : tempPublic) { f.delete(); }
            for (File f : tempPrivate) { f.delete(); }
        }
        catch (GeneralSecurityException e)
        {
            System.err.println("Failed to set up SecuredAESKey tests: "
                    + e.getMessage());
        }
    }
    
    @AfterAll
    public static void tearDownClass()
    {
        for (int i = LOCAL; i <= REMOTE; i++)
        {
            KEY_SETS[i] = null;
            SECURED_AES[i] = null;
        }
    }

    /**
     * Test of getSecuredKeyString method, of class SecuredAESKey.
     */
    @Test
    public void testGetSecuredKeyString()
    {
        String secured = SECURED_AES[LOCAL].getSecuredKeyString();
        assertNotNull(secured, "The secured key string should never be null.");
        assertNotEquals("", secured,
                "The secured key string should never be empty.");
        assertEquals(secured, SECURED_AES[LOCAL].getSecuredKeyString(),
                "The exported secure key string should always be the same.");
        byte[] initialEncrypted = SECURED_AES[LOCAL].encryptMessage(
                TEST_MESSAGE.getBytes(Charset.forName("UTF8")));
        try
        {
            SecuredAESKey remoteKey
                    = new SecuredAESKey(secured, KEY_SETS[REMOTE]);
            byte[] remoteEncrypted = remoteKey.encryptMessage(
                    TEST_MESSAGE_BYTES);
            assertArrayEquals(initialEncrypted, remoteEncrypted,
                    "Encrypted data created remotely doesn't match encrypted "
                    + "data created locally.");
            String remoteSecured = remoteKey.getSecuredKeyString();
            assertNotNull(remoteSecured,
                    "The secured key string should never be null.");
            assertNotEquals("", remoteSecured,
                    "The secured key string should never be empty.");
            assertNotEquals(secured, remoteSecured,
                    "The key string signed and encrypted locally shouldn't "
                    + "equal the remotely signed and encrypted remotely.");
            SecuredAESKey duplicateKey 
                    = new SecuredAESKey(remoteSecured, KEY_SETS[LOCAL]);
            byte[] encryptedAgain = duplicateKey.encryptMessage(
                    TEST_MESSAGE_BYTES);
            assertArrayEquals(initialEncrypted, encryptedAgain,
                    "The restored local key should encrypt messages the same "
                    + "way as the original secured key instance.");
        }
        catch (InvalidKeyException | SignatureException e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * Test of encryptMessage and decryptMessage methods, of class
     * SecuredAESKey.
     */
    @Test
    public void testEncryptAndDecryptMessage()
    {
        byte[] encryptedMessage = SECURED_AES[LOCAL].encryptMessage(
                TEST_MESSAGE_BYTES);
        assertNotNull(encryptedMessage);
        assertFalse(encryptedMessage.length == 0,
                "Encrypted message should not be empty.");
        byte[] decrypted = SECURED_AES[LOCAL].decryptMessage(encryptedMessage);
        assertArrayEquals(TEST_MESSAGE_BYTES, decrypted);
    }
}
