package com.centuryglass.mcmap.webserver.security;

import java.io.File;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class KeySetTest
{
    private static KeySet testedSet;
    private static KeySet matchedSet;
    private static File[] keyFiles;
    private enum Keys
    {
        TEST_PUBLIC,
        TEST_PRIVATE,
        MATCHED_PUBLIC,
        MATCHED_PRIVATE
    }
    private static final byte[] TEST_MESSAGE = { 'L', 'o', 'r', 'e', 'm', ' ',
            'I', 'p', 's', 'u', 'm'};

    @BeforeAll
    public static void setUpClass() throws Exception
    {
        System.out.println("Starting test init.");
        int numKeys = Keys.values().length;
        keyFiles = new File[numKeys];
        for (int i = 0; i < numKeys; i++)
        {
            keyFiles[i] = File.createTempFile(Keys.values()[i].name(), "key");
        }
        RSAGenerator.generate(keyFiles[Keys.TEST_PUBLIC.ordinal()],
                keyFiles[Keys.TEST_PRIVATE.ordinal()]);
        RSAGenerator.generate(keyFiles[Keys.MATCHED_PUBLIC.ordinal()],
                keyFiles[Keys.MATCHED_PRIVATE.ordinal()]);
        testedSet = new KeySet(keyFiles[Keys.TEST_PRIVATE.ordinal()],
                keyFiles[Keys.TEST_PUBLIC.ordinal()],
                keyFiles[Keys.MATCHED_PUBLIC.ordinal()]);
        matchedSet = new KeySet(keyFiles[Keys.MATCHED_PRIVATE.ordinal()],
                keyFiles[Keys.MATCHED_PUBLIC.ordinal()],
                keyFiles[Keys.TEST_PUBLIC.ordinal()]);
        System.out.println("Initialized test files.");
    }

    @AfterAll
    public static void tearDownClass() throws Exception
    {
        testedSet = null;
        matchedSet = null;
        for (File file : keyFiles)
        {
            file.delete();
        }
        keyFiles = null;
    }

    /**
     * Test of createSignedData method, of class KeySet.
     */
    @Test
    public void testCreateSignedMessage()
    {
        byte[] signed = testedSet.createSignedData(TEST_MESSAGE);
        assertNotNull(signed);
        byte[] reversed = matchedSet.readRemoteSignedMessage(signed);
        assertArrayEquals(TEST_MESSAGE, reversed);
    }

    /**
     * Test of createEncryptedMessage method, of class KeySet.
     */
    @Test
    public void testCreateEncryptedMessage()
    {
        byte[] encrypted = testedSet.createEncryptedMessage(TEST_MESSAGE);
        assertNotNull(encrypted);
        byte[] reversed = matchedSet.decryptMessage(encrypted);
        assertArrayEquals(TEST_MESSAGE, reversed);
    }

    /**
     * Test of readLocallySignedMessage method, of class KeySet.
     */
    @Test
    public void testReadLocallySignedMessage()
    {
        byte[] signed = testedSet.createSignedData(TEST_MESSAGE);
        assertNotNull(signed);
        byte[] reversed = testedSet.readLocallySignedMessage(signed);
        assertArrayEquals(TEST_MESSAGE, reversed);
    }

    /**
     * Test of readRemoteSignedMessage method, of class KeySet.
     */
    @Test
    public void testReadRemoteSignedMessage()
    {
        byte[] signed = matchedSet.createSignedData(TEST_MESSAGE);
        assertNotNull(signed);
        byte[] reversed = testedSet.readRemoteSignedMessage(signed);
        assertArrayEquals(TEST_MESSAGE, reversed);
    }

    /**
     * Test of decryptMessage method, of class KeySet.
     */
    @Test
    public void testDecryptMessage()
    {
        byte[] encrypted = matchedSet.createEncryptedMessage(TEST_MESSAGE);
        assertNotNull(encrypted);
        byte[] reversed = testedSet.decryptMessage(encrypted);
        assertArrayEquals(TEST_MESSAGE, reversed);
    }  
}
