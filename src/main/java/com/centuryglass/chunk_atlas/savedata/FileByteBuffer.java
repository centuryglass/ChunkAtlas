/**
 * @file  FileByteBuffer.java
 * 
 *  Provides a more convenient interface for processing streams of binary 
 * Minecraft region data.
 */
package com.centuryglass.chunk_atlas.savedata;

import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * FileByteBuffer is a binary data stream class, extracting and processing data
 * from either a specific file or a byte array.
 * 
 *  FileByteBuffer objects assume that all data is big-endian, and stored using
 * a set of standard data types of varying sizes. FileByteBuffer is not heavily
 * specialized for handling Minecraft data, and could easily be reused in other
 * applications.
 */
public class FileByteBuffer
{
    /**
     * Creates a FileByteBuffer to read data from a file.
     * 
     * @param toOpen                  The file data source.
     * 
     * @throws FileNotFoundException  If the file does not exist.
     * 
     * @throws IOException            If an error occurs while reading file
     *                                data.
     */
    public FileByteBuffer(File toOpen) throws FileNotFoundException,
           IOException
    {
        final long fileSize = toOpen.length();
        byte [] bufferArray = new byte[(int) fileSize];
        final FileInputStream tempStream = new FileInputStream(toOpen);
        int bytesRead = 0;
        while (bytesRead < fileSize)
        {
            bytesRead += tempStream.read(bufferArray, bytesRead,
                    (int) fileSize - bytesRead);
            
        }
        buffer = ByteBuffer.wrap(bufferArray);
    }
    
    /**
     * Creates a FileByteBuffer to read data from a byte array.
     * 
     * @param byteArray  An array of bytes to access through the buffer. 
     */
    public FileByteBuffer(byte[] byteArray)
    {
        buffer = ByteBuffer.wrap(byteArray);
    }
    
    /**
     * Gets the buffer's current position within binary data.
     * 
     * @return  The index of the next byte in the buffer. 
     */
    public long getPos()
    {
        return buffer.capacity() - buffer.remaining();
    }
    
    /**
     * Sets the buffer's current position within the file or byte array.
     * 
     * @param newPos                      The new buffer index to seek to.
     * 
     * @throws IllegalArgumentException   When the new position is invalid.
     * 
     * @throws IndexOutOfBoundsException  If an attempt is made to seek past
     *                                    the end of the file.
     */
    public void setPos(int newPos) throws IllegalArgumentException,
            IndexOutOfBoundsException
    {
        ExtendedValidate.validIndex(newPos, buffer.limit(), "Buffer index");
        buffer.position(newPos);
    }
    
    /**
     * Sets the buffer's mark at the current position.
     * 
     * @return  This buffer. 
     */
    public final FileByteBuffer mark()
    {
        buffer.mark();
        return this;
    }
    
    /**
     * Resets the buffer's position to the previously-marked position.
     * 
     * @return  This buffer.
     */
    public final FileByteBuffer reset()
    {
        buffer.reset();
        return this;
    }
    
    /**
     * Get the number of bytes remaining in the buffer after the current
     * position.
     * 
     * @return  The number of bytes available. 
     */
    public int remaining()
    {
        return buffer.remaining();
    }
    
    /**
     * Reads a single byte from the buffer at the current position.
     * 
     * @return                            The next byte.
     * 
     * @throws IndexOutOfBoundsException  If the buffer is at its limit.
     */
    public byte readByte() throws BufferOverflowException
    {
        if (buffer.position() == buffer.limit())
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.readByte():"
                    + " No bytes remaining.");
        }
        return buffer.get();
    }; 
    
    /**
     * Skips a single byte from the stream at the current position.
     * 
     * @throws IndexOutOfBoundsException  If the byte could not be skipped.
     */
    public void skipByte() throws IndexOutOfBoundsException
    {
        if (skip(1) != 1)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.skipByte():"
                    + " No bytes remaining.");
        }
    };
    
    /**
     * Reads a big-endian, two byte short value from the stream.
     * 
     * @return                            The value that was read.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have two bytes
     *                                    between the current position and the
     *                                    limit.
     */
    public short readShort() throws IndexOutOfBoundsException
    {
        if ((buffer.limit() - buffer.position()) < 2)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.readShort():"
                    + " Not enough bytes remaining.");
        }
        return buffer.getShort();
    }
    
    /**
     * Skips a single short from the stream at the current position.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have two bytes
     *                                    between the current position and the
     *                                    limit.
     */
    public void skipShort() throws IndexOutOfBoundsException
    {
        if (skip(2) != 2)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.skipShort():"
                    + " Not enough bytes remaining.");
        }
    };
    
    /**
     * Reads a big-endian, four byte int value from the stream.
     * 
     * @return  The value that was read.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have four
     *                                    bytes between the current position
     *                                    and the limit.
     */
    public int readInt() throws IndexOutOfBoundsException
    {
        if ((buffer.limit() - buffer.position()) < 4)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.readInt():"
                    + " Not enough bytes remaining.");
        }
        return buffer.getInt();
    }
    
    /**
     * Skips a single int from the stream at the current position.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have four
     *                                    bytes between the current position
     *                                    and the limit.
     */
    public void skipInt() throws IndexOutOfBoundsException
    {
        if (skip(4) != 4)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.skipInt():"
                    + " Not enough bytes remaining.");
        }
    };
    
    /**
     * Reads a big-endian, four byte float value from the stream.
     * 
     * @return  The value that was read.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have four
     *                                    bytes between the current position
     *                                    and the limit.
     */
    public float readFloat() throws IndexOutOfBoundsException
    {
        if ((buffer.limit() - buffer.position()) < 4)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.readFloat():"
                    + " Not enough bytes remaining.");
        }
        return buffer.getFloat();
    }   
    
    /**
     * Skips a single float from the stream at the current position.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have four
     *                                    bytes between the current position
     *                                    and the limit.
     */
    public void skipFloat() throws IndexOutOfBoundsException
    {
        if (skip(4) != 4)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.skipFloat():"
                    + " Not enough bytes remaining.");
        }
    }; 
    
    /**
     * Reads a big-endian, eight byte double value from the stream.
     * 
     * @return                            The value that was read.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have eight
     *                                    bytes between the current position
     *                                    and the limit.
     */
    public double readDouble() throws IndexOutOfBoundsException
    {
        if ((buffer.limit() - buffer.position()) < 8)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.readDouble():"
                    + " Not enough bytes remaining.");
        }
        return buffer.getDouble();
    }     
    
    /**
     * Skips a single double from the stream at the current position.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have eight
     *                                    bytes between the current position
     *                                    and the limit.
     */
    public void skipDouble() throws IndexOutOfBoundsException
    {
        if (skip(8) != 8)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.skipDouble():"
                    + " Not enough bytes remaining.");
        }
    };     
    
    /**
     * Reads a big-endian, eight byte long value from the stream.
     * 
     * @return  The value that was read.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have eight
     *                                    bytes between the current position
     *                                    and the limit.
     */
    public long readLong() throws IndexOutOfBoundsException
    {
        if ((buffer.limit() - buffer.position()) < 8)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.readDouble():"
                    + " Not enough bytes remaining.");
        }
        return buffer.getLong();
    }
        
    /**
     * Skips a single long from the stream at the current position.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have eight
     *                                    bytes between the current position
     *                                    and the limit.
     */
    public void skipLong() throws IndexOutOfBoundsException
    {
        if (skip(8) != 8)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.skipLong():"
                    + " Not enough bytes remaining.");
        }
    }; 
    
    /**
     * Reads a big-endian int value with a variable byte size from the stream.
     * 
     * @param byteSize                    The number of bytes to read into the
     *                                    integer. This value must be between
     *                                    zero and four, inclusive.
     * 
     * @return                            The value that was read.
     * 
     * @throws IllegalArgumentException   If byteSize is not between zero and
     *                                    four, inclusive.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have byteSize
     *                                    bytes between the current position
     *                                    and the limit.
     */
    public int readInt(int byteSize) throws IllegalArgumentException,
            IndexOutOfBoundsException
    {
        if (byteSize > 4 || byteSize < 0)
        {
            throw new IllegalArgumentException("FileByteBuffer.readInt(int): "
                    + "Invalid byteSize " + byteSize);
        }
        if (byteSize == 4)
        {
            return readInt();
        }
        if (byteSize == 0)
        {
            return 0;
        }
        if ((buffer.limit() - buffer.position()) < 8)
        {
            throw new IndexOutOfBoundsException("FileByteBuffer.readInt(int):"
                    + " Not enough bytes remaining.");
        }
        byte[] tempBytes = new byte[4];
        Arrays.fill(tempBytes, (byte) 0);
        buffer.get(tempBytes, 4 - byteSize, byteSize);
        return ByteBuffer.wrap(tempBytes).getInt();
    }
    
    /**
     * Reads a big-endian long value with a variable byte size from the stream.
     * 
     * @param byteSize                    The number of bytes to read into the
     *                                    integer. This value should not be
     *                                    greater than eight or less than zero.
     * 
     * @return                            The value that was read.
     * 
     * @throws IllegalArgumentException   If byteSize is not between zero and
     *                                    eight, inclusive.
     * 
     * @throws IndexOutOfBoundsException  If the buffer does not have byteSize
     *                                    bytes between the current position
     *                                    and the limit.
     */
    public long readLong(int byteSize) throws IllegalArgumentException, 
            IndexOutOfBoundsException
    {
        if (byteSize > 8 || byteSize < 0)
        {
            throw new IllegalArgumentException("FileByteBuffer.readLong(int): "
                    + "Invalid byteSize " + byteSize);
        }
        if (byteSize == 8)
        {
            return readLong();
        }
        if (byteSize == 0)
        {
            return 0;
        }
        byte[] tempBytes = new byte[byteSize];
        buffer.get(tempBytes);
        return ByteBuffer.wrap(tempBytes).getLong();
    }
    
    /**
     * Reads an array of bytes from the stream.
     * 
     * @param size                        Maximum number of bytes to read.
     * 
     * @return                            A new array holding all returned
     *                                    bytes, with a length exactly equal to
     *                                    the number of bytes read.
     * 
     * @throws IllegalArgumentException   If byteSize is less than one.
     */
    public byte[] readBytes(int size)
    {
        if (size <= 0)
        {
            throw new IllegalArgumentException("FileByteBuffer.readBytes(int):"
                    + " Requested invalid read of size " + size + ".");
        }
        int returnedSize = Math.min(buffer.remaining(), size);
        byte[] streamBytes = new byte[returnedSize];
        buffer.get(streamBytes);
        return streamBytes;
    }
    
    /**
     * Skip forward in the stream by a specific byte count.
     * 
     * @param toSkip                      Maximum number of bytes to skip.
     * 
     * @return                            Actual number of bytes skipped.
     * 
     * @throws IllegalArgumentException   If toSkip is less than zero.
     */
    public long skip(long toSkip) throws IllegalArgumentException
    {
        if (toSkip < 0)
        {
            throw new IllegalArgumentException("FileByteBuffer.skip(long): "
                    + "Requested invalid skip of size " + toSkip + ".");
        }
        long bufferBytesSkipped = Math.min(buffer.limit() - buffer.position(),
                toSkip);
        if (bufferBytesSkipped > 0)
        {
            buffer.position(buffer.position() + (int) bufferBytesSkipped);
        }
        return bufferBytesSkipped;       
    }
    
    // Internal data buffer:
    private final ByteBuffer buffer;
}
