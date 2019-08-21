/**
 * @file  ByteStream.java
 * 
 * @brief  A file byte stream class with an ideal interface for reading
 *         Minecraft world data.
 */
package com.centuryglass.mcmap.savedata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ByteStream
{
    public ByteStream(File toOpen) throws FileNotFoundException
    {
        stream = new FileInputStream(toOpen);
        fileIndex = 0;
        // Make the default buffer size large enough to hold any primitive
        // data type:
        buffer = ByteBuffer.allocate(8);
        buffer.position(0);
        buffer.limit(0);
    }
    
    public ByteStream(byte[] byteArray)
    {
        stream = null;
        buffer = ByteBuffer.wrap(byteArray);
        buffer.position(0);
        buffer.limit(byteArray.length);
        fileIndex = -1;
    }
    
    /**
     * @brief  Gets the stream's current position within the file or byte array.
     * 
     * @return  The index of the next byte in the stream. 
     */
    public int getStreamPos()
    {
        if (fileIndex > 0)
        {
            return fileIndex - buffer.remaining();
        }
        return buffer.capacity() - buffer.remaining();
    }
    
    /**
     * @brief  Get an estimate of the number of remaining bytes that can be read
     *         from the stream without blocking.
     * 
     * @return  The number of bytes available. 
     */
    public int available()
    {
        if (stream == null)
        {
            return buffer.remaining();
        }
        int availableBytes;
        try
        {
            availableBytes = stream.available();
        }
        catch (IOException e)
        {
            availableBytes = 0;
        }
        return availableBytes + buffer.remaining();
    }
    
    /**
     * @brief  Reads a single byte from the stream at the current position.
     * 
     * @return  The next byte.
     * 
     * @throws IOException  If the byte could not be read.
     */
    public byte readByte() throws IOException
    {
        if (! buffer.hasRemaining())
        {
            readToBuffer(1);
        }
        return buffer.get();
    };
    
    /**
     * @brief  Reads a big-endian, two byte short value from the stream.
     * 
     * @return  The value that was read.
     * 
     * @throws IOException  If the short could not be read. 
     */
    public short readShort() throws IOException
    {
        if (buffer.remaining() < 2)
        {
            readToBuffer(2 - buffer.remaining());
        }
        return buffer.getShort();
    }
    
    /**
     * @brief  Reads a big-endian, four byte int value from the stream.
     * 
     * @return  The value that was read.
     * 
     * @throws IOException  If the int could not be read. 
     */
    public int readInt() throws IOException
    {
        if (buffer.remaining() < 4)
        {
            readToBuffer(4 - buffer.remaining());
        }
        return buffer.getInt();
    }
    
    /**
     * @brief  Reads a big-endian, four byte float value from the stream.
     * 
     * @return  The value that was read.
     * 
     * @throws IOException  If the float could not be read. 
     */
    public float readFloat() throws IOException
    {
        if (buffer.remaining() < 4)
        {
            readToBuffer(4 - buffer.remaining());
        }
        return buffer.getFloat();
    }    
    
    /**
     * @brief  Reads a big-endian, eight byte double value from the stream.
     * 
     * @return  The value that was read.
     * 
     * @throws IOException  If the double could not be read. 
     */
    public double readDouble() throws IOException
    {
        if (buffer.remaining() < 8)
        {
            readToBuffer(8 - buffer.remaining());
        }
        return buffer.getDouble();
    }  
    
    /**
     * @brief  Reads a big-endian int value with a variable byte size from the
     *         stream.
     * 
     * @param byteSize      The number of bytes to read into the integer. This
     *                      value should not be greater than four or less than
     *                      zero.
     * 
     * @return              The value that was read.
     * 
     * @throws IOException  If the int could not be read, either because of a
     *                      file IO issue or because byteSize was invalid.
     */
    public int readInt(int byteSize) throws IOException
    {
        if (byteSize > 4 || byteSize < 0)
        {
            throw new IOException("ByteStream.readInt(int): Invalid byteSize "
                    + byteSize);
        }
        if (byteSize == 4)
        {
            return readInt();
        }
        if (byteSize == 0)
        {
            return 0;
        }
        if (buffer.remaining() < byteSize)
        {
            int bytesNeeded = byteSize - buffer.remaining();
            int bytesAdded = readToBuffer(bytesNeeded);
            if (bytesAdded < bytesNeeded)
            {
                throw new IOException("ByteStream.readInt(int): "
                        + "Unable to add " + bytesNeeded + " bytes to buffer.");
            }
        }
        
        byte[] tempBytes = new byte[4];
        Arrays.fill(tempBytes, (byte) 0);
        buffer.get(tempBytes, 4 - byteSize, byteSize);
        return ByteBuffer.wrap(tempBytes).getInt();
    }
    
    /**
     * @brief  Reads a big-endian, eight byte long value from the stream.
     * 
     * @return  The value that was read.
     * 
     * @throws IOException  If the long could not be read. 
     */
    public long readLong() throws IOException
    {
        if (buffer.remaining() < 8)
        {
            readToBuffer(8 - buffer.remaining());
        }
        return buffer.getLong();
    }
    
    /**
     * @brief  Reads a big-endian long value with a variable byte size from the
     *         stream.
     * 
     * @param byteSize      The number of bytes to read into the integer. This
     *                      value should not be greater than eight or less than
     *                      zero.
     * 
     * @return              The value that was read.
     * 
     * @throws IOException  If the int could not be read, either because of a
     *                      file IO issue or because byteSize was invalid.
     */
    public long readLong(int byteSize) throws IOException
    {
        if (byteSize > 8 || byteSize < 0)
        {
            throw new IOException("ByteStream.readLong(int): Invalid byteSize "
                    + byteSize);
        }
        if (byteSize == 8)
        {
            return readLong();
        }
        if (byteSize == 0)
        {
            return 0;
        }
        if (buffer.remaining() < byteSize)
        {
            readToBuffer(byteSize - buffer.remaining());
        }
        byte[] tempBytes = new byte[byteSize];
        buffer.get(tempBytes);
        return ByteBuffer.wrap(tempBytes).getLong();
    }
    
    /**
     * @brief  Reads an array of bytes from the stream.
     * 
     * @param size          Maximum number of bytes to read.
     * 
     * @return              A new array holding all returned bytes, with a
     *                      length exactly equal to the number of bytes read.
     * 
     * @throws IOException  If no bytes could be read, or an error occurred
     *                      when reading from the file.
     */
    public byte[] readBytes(int size) throws IOException
    {
        if (size <= 0)
        {
            throw new IOException("ByteStream.readBytes(int): Requested invalid"
                    + " read of size " + size + ".");
        }
        if (buffer.remaining() < size)
        {
            readToBuffer(size - buffer.remaining());
        }
        int returnedSize = Math.min(buffer.remaining(), size);
        if(returnedSize <= 0)
        {
            throw new IOException("ByteStream.readBytes(int): Unable to read"
                    + " any data from stream.");
            
        }
        byte[] streamBytes = new byte[returnedSize];
        buffer.get(streamBytes);
        return streamBytes;
    }
    
    /**
     * @brief  Skip forward in the stream by a specific byte count.
     * 
     * @param toSkip        Maximum number of bytes to skip.
     * 
     * @return              Actual number of bytes skipped. 
     * 
     * @throws IOException  For any of the reasons FileInputStream.skip
     *                      would throw an IOException.
     */
    public long skip(long toSkip) throws IOException
    {
        long bufferBytesSkipped = Math.min(buffer.limit() - buffer.position(),
                toSkip);
        if (bufferBytesSkipped > 0)
        {
            buffer.position(buffer.position() + (int) bufferBytesSkipped);
        }
        if (stream == null || bufferBytesSkipped >= toSkip)
        {
            return bufferBytesSkipped;
        }
        long numLeftToSkip = toSkip - bufferBytesSkipped;
        long skippedInStream = stream.skip(numLeftToSkip);
        fileIndex += skippedInStream;
        return bufferBytesSkipped + skippedInStream;       
    }
    
    /**
     * @brief  Buffer up to size stream bytes.
     * 
     * @param size  The number of bytes to buffer.
     * 
     * @return      The number of bytes that were actually buffered.
     */
    public int readToBuffer(int size) throws IOException
    {
        if (stream == null || size == 0)
        {
            return 0;
        }
        int startPos = buffer.position();
        byte[] fileBytes = new byte[size];
        int bytesRead = stream.read(fileBytes);
        fileIndex += bytesRead;
        try
        {
            buffer.put(fileBytes);
        }
        catch (BufferOverflowException e)
        {
            ByteBuffer newBuffer = ByteBuffer.allocate(buffer.remaining()
                    + size + 8);
            try
            {
                if (buffer.hasRemaining())
                {
                    newBuffer.put(buffer);
                }
                newBuffer.put(fileBytes);
                buffer = newBuffer;
                startPos = 0;
            }
            catch (BufferOverflowException e2)
            {
                // This shouldn't ever happen, but we might as well explain the
                // problem if somehow it happens anyway.
                System.err.println("ByteStream.readToBuffer(int): "
                        + ": Somehow failed to fit " + newBuffer.capacity()
                        + " bytes in a buffer of capacity "
                        + newBuffer.capacity());
                System.exit(1);
            }
        }
        buffer.limit(buffer.position());
        buffer.position(startPos);
        return bytesRead;
    }
    
    // Internal file stream object:
    private final FileInputStream stream;
    // Buffered file data:
    private ByteBuffer buffer;
    // Stream index within the file:
    private int fileIndex;
}
