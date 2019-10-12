/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centuryglass.mcmap.webserver.security;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
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
    
    private static final String TEST_MESSAGE = "Lorem ipsum dolor sit amet, con"
            + "sectetur adipiscing elit, sed do eiusmod tempor incididunt ut la"
            + "bore et dolore magna aliqua. Ut enim ad minim veniam, quis nostr"
            + "ud exercitation ullamco laboris nisi ut aliquip ex ea commodo co"
            + "nsequat. Duis aute irure dolor in reprehenderit in voluptate vel"
            + "it esse cillum dolore eu fugiat nulla pariatur. Excepteur sint o"
            + "ccaecat cupidatat non proident, sunt in culpa qui officia deseru"
            + "nt mollit anim id est laborum.";
    
    private static File createTemp() throws IOException
    {
        return File.createTempFile("aesTest", "key");
    }
    
    @BeforeAll
    public static void setUpClass() throws IOException
    {
        File[] tempPublic = { createTemp(), createTemp() };
        File[] tempPrivate = { createTemp(), createTemp() };
        RSAGenerator.generate(tempPublic[LOCAL], tempPrivate[LOCAL]);
        RSAGenerator.generate(tempPublic[REMOTE], tempPrivate[REMOTE]);
        KEY_SETS[LOCAL] = new KeySet(tempPrivate[LOCAL], tempPublic[LOCAL],
                tempPublic[REMOTE]);
        KEY_SETS[REMOTE] = new KeySet(tempPrivate[REMOTE], tempPublic[REMOTE],
                tempPublic[LOCAL]);
        SecretKey aesLocal = AESGenerator.generate();
        SecretKey aesRemote = AESGenerator.generate();
        SECURED_AES[LOCAL] = new SecuredAESKey(aesLocal, KEY_SETS[LOCAL]);
        SECURED_AES[REMOTE] = new SecuredAESKey(aesRemote,
                KEY_SETS[REMOTE]);
        for (File f : tempPublic) { f.delete(); }
        for (File f : tempPrivate) { f.delete(); }
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
        assertNotNull(secured);
        assertNotEquals("", secured);
    }

    /**
     * Test of encryptMessage and decryptMessage methods, of class
     * SecuredAESKey.
     */
    @Test
    public void testEncryptAndDecryptMessage()
    {
        String encryptedMessage = SECURED_AES[LOCAL].encryptMessage(
                TEST_MESSAGE);
        assertNotNull(encryptedMessage);
        assertNotEquals("", encryptedMessage);
        assertNotEquals(TEST_MESSAGE, encryptedMessage);
        String decrypted = SECURED_AES[LOCAL].decryptMessage(encryptedMessage);
        assertEquals(TEST_MESSAGE, decrypted);
    }
     
    /**
     * Test loading a remotely generated secured key string.
     */
    @Test
    public void testRemoteKeyLoading()
    {
        String remoteData = SECURED_AES[REMOTE].getSecuredKeyString();
        try
        {
            SecuredAESKey loadedKey
                    = new SecuredAESKey(remoteData, KEY_SETS[LOCAL]);
        }
        catch (InvalidKeyException e)
        {
            fail("Unable to load remotely generated key from string: "
                    + e.getMessage());
        }
    }
}
