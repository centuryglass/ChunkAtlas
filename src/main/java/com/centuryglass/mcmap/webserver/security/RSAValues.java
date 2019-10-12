/**
 * @file RSAValues.java
 * 
 * Generates, loads, and stores values used as an RSA key.
 */

package com.centuryglass.mcmap.webserver.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Generates, loads, and stores values used as an RSA key.
 */
public class RSAValues
{   
    /**
     * Initialize using specific values.
     * 
     * @param mod  The key's modulus.
     * 
     * @param exp  The key's exponent. 
     */
    public RSAValues(BigInteger mod, BigInteger exp)
    {
        this.mod = mod;
        this.exp = exp;
    }

    /**
     * Initialize using values read from a file.
     * 
     * @param keyFile                  A file containing key data. Data
     *                                 should be stored in the following
     *                                 format:
     *                                 (4 bytes):         modLength,
     *                                 (modLength bytes): key modulus
     *                                 (4 bytes):         expLength,
     *                                 (expLength bytes): key exponent
     * 
     * @throws FileNotFoundException   If the key file doesn't exist.
     * 
     * @throws IOException             If unable to fully read the key file. 
     */
    public RSAValues(File keyFile) throws FileNotFoundException, IOException
    {
        try (FileInputStream keyStream = new FileInputStream(keyFile))
        {
            mod = readBigInt(keyStream);
            exp = readBigInt(keyStream);
        }
    }

    /**
     * Gets the key's modulus value.
     * 
     * @return  The key's modulus.
     */
    public BigInteger getModulus()
    {
        return mod;
    }

    /**
     * Gets the key's exponent value.
     * 
     * @return  The key's exponent.
     */
    public BigInteger getExponent()
    {
        return exp;
    }
        
    /**
     * Saves the key's modulus and exponent to a file in an easily readable
     * format.
     * 
     * @param outFile       The file where the key should be saved.
     * 
     * @throws IOException  If unable to write to the key file. 
     */
    public void saveToBinaryFile(File outFile) throws IOException
    {    
        try (FileOutputStream out = new FileOutputStream(outFile))
        {
            writeBigInt(mod, out);
            writeBigInt(exp, out);
        }  
    }

    /**
     * Read file data until a byte array is full.
     * 
     * @param bytes         The array to fill.
     * 
     * @param stream        The file stream used to read data.
     * 
     * @throws IOException  If the stream didn't contain enough data to fill
     *                      the byte array, or any other error occurred
     *                      while reading from the file.
     */
    private void fillByteArray(byte[] bytes, FileInputStream stream)
            throws IOException
    {
        int totalBytesRead = 0;
        while (totalBytesRead < bytes.length)
        {
            int bytesRead = stream.read(bytes, totalBytesRead,
                    bytes.length - totalBytesRead);
            if (bytesRead == -1)
            {
                throw new IOException("Tried reading "
                        + String.valueOf(bytes.length) + " bytes, but only "
                        + String.valueOf(totalBytesRead) + " bytes found.");
            }
            totalBytesRead += bytesRead;
        }
    }

    /**
     * Reads a BigInteger value from a file. This method expects to find
     * a four-byte length value, followed by (length) bytes of BigInteger
     * data.
     * 
     * @param stream        A file stream used to read the value.
     * 
     * @return              The value read from the file.
     * 
     * @throws IOException  If unable to read from the file, or if the
     *                      file was shorter than expected.
     */
    private BigInteger readBigInt(FileInputStream stream) throws IOException
    {
        byte[] lengthBytes = new byte[Integer.BYTES];
        fillByteArray(lengthBytes, stream);
        int length = ByteBuffer.wrap(lengthBytes).getInt();
        byte[] data = new byte[length];
        fillByteArray(data, stream);
        return new BigInteger(data);
    }
           
    /**
     * Writes a big integer value to disk in a format that can be parsed by
     * other languages more easy than a serialized BigInteger object.
     * 
     * The value is stored as a four-byte size value, followed by however many
     * bytes of BigInteger data that the size value specified.
     * 
     * @param value          A non-null BigInteger value.
     * 
     * @param stream         An open file output stream where the value should
     *                       be written.
     * 
     * @throws IOException   If unable to write to the output stream.
     */
    private static void writeBigInt(BigInteger value, FileOutputStream stream)
            throws IOException
    {
        byte[] valueBytes = value.toByteArray();
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(valueBytes.length);
        stream.write(lengthBuffer.array());
        stream.write(valueBytes);
    }


    private final BigInteger mod;
    private final BigInteger exp;   
}
