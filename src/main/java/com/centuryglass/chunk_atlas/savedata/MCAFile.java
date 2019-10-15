/**
 * @file MCAFile.java
 * 
 * Reads data from a Minecraft region file.
 */

package com.centuryglass.chunk_atlas.savedata;

import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import com.centuryglass.chunk_atlas.worldinfo.ChunkData;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;
import org.apache.commons.lang.Validate;

/**
 * Reads data from a Minecraft region file.
 */
public class MCAFile 
{
    // width/height in chunks of a region file:
    private static final int DIM_IN_CHUNKS = 32;
    
    /**
     * Loads data from a .mca file on construction.
     *
     * @param mcaFile                 The Minecraft anvil region file to load.
     * 
     * @throws FileNotFoundException  If the file does not exist.
     */
    public MCAFile(File mcaFile) throws FileNotFoundException
    {
        ExtendedValidate.isFile(mcaFile, "Minecraft region file");
        loadedChunks = new ArrayList<>();
        // read the region file's base coordinates from the file name:
        Point regionPt = getChunkCoords(mcaFile);
        if (regionPt.x == -1 && regionPt.y == -1)
        {
            System.err.println("Can't parse coordinates from file "
                    + mcaFile.toString());
            return;
        }
        
        FileByteBuffer regionBuffer;
        try
        {
            regionBuffer = new FileByteBuffer(mcaFile);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Failed to find file " + mcaFile.toString());
            return;
        }
        catch (IOException e)
        {
            System.err.println("Error reading region file: " + e.getMessage());
            return;
        }
             
        // Read chunk data offsets within the file:
        int numChunks = DIM_IN_CHUNKS * DIM_IN_CHUNKS;
        final int sectorSize = 4096;
        int invalidChunks = 0;
        
        // If chunk loading fails, use this function to get chunk coordinates
        // from the chunk index:
        Function<Integer, Point> getPos = (index) ->
        {
            return new Point(regionPt.x + (index % 32),
                    regionPt.y + (index / 32));    
        };
        
        // Read all chunk offsets and extract chunk data:
        for (int i = 0; i < numChunks; i++)
        {
            long sectorOffset = 0;
            byte sectorCount = 0;
            try
            {
                sectorOffset = regionBuffer.readInt(3);
                sectorCount = regionBuffer.readByte();
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                System.err.println(e.getMessage());
                System.exit(1);
            }
            if (sectorOffset == 0 && sectorCount == 0)
            {
                // That sector isn't loaded, skip it.
                loadedChunks.add(new ChunkData(getPos.apply(i),
                        ChunkData.ErrorFlag.CHUNK_MISSING));
                continue;
            }
            // Seek to offset and read chunk data:
            long byteOffset = sectorOffset * sectorSize;
            regionBuffer.mark();
            try
            {
                regionBuffer.setPos((int) byteOffset);
            }
            catch (IndexOutOfBoundsException e)
            {
                // Invalid, out of bounds sector, skip it.
                loadedChunks.add(new ChunkData(getPos.apply(i),
                        ChunkData.ErrorFlag.BAD_OFFSET));
                invalidChunks++;
                regionBuffer.reset();
                continue;
            }
            final int chunkByteSize = regionBuffer.readInt();
            if (chunkByteSize == 0)
            {
                continue;
            }
            regionBuffer.skipByte(); // compression type isn't needed
            byte[] chunkBytes = regionBuffer.readBytes(chunkByteSize);
            if (chunkBytes.length != chunkByteSize)
            {
                System.err.println("Unexpected EOF: Read only "
                        + chunkBytes.length + " bytes, expected "
                        + chunkByteSize);
                invalidChunks++;
                continue;
            }
            ChunkNBT nbtData = new ChunkNBT(chunkBytes);
            ChunkData extractedData = nbtData.getChunkData();
            if (extractedData.getErrorType() != ChunkData.ErrorFlag.NONE)
            {
                extractedData = new ChunkData(getPos.apply(i),
                        extractedData.getErrorType());
            }
            loadedChunks.add(extractedData);
            regionBuffer.reset();
        }
        if (invalidChunks > 0)
        {
            System.err.println("Warning: " + invalidChunks + " chunks in "
                    + mcaFile.getName() + " could not be loaded.");
        }
    }

    /**
     *  Finds a region file's upper left chunk coordinate from its file
     *         name.
     *
     * @param regionFile  A Minecraft region file.
     *
     * @return            The chunk coordinates, or null if the file name was
     *                    not properly constructed.
     */
    public static Point getChunkCoords(File regionFile)
    {
        Validate.notNull(regionFile, "Region file cannot be null.");
        final String name = regionFile.getName();
        final int xStart = 2;
        final int xEnd = name.indexOf(".", xStart);
        final int zStart = xEnd + 1;
        final int zEnd = name.indexOf(".", zStart);
        if (xEnd < 0 || zEnd < 0)
        {
            return null;
        }       
        return new Point(
                DIM_IN_CHUNKS * Integer.parseInt(name.substring(xStart, xEnd)),
                DIM_IN_CHUNKS * Integer.parseInt(name.substring(zStart,
                zEnd)));
    }

    /**
     *  Gets information about all loaded chunks stored in the file.
     *
     * @return  The list of chunk data objects.
     */
    public ArrayList<ChunkData> getLoadedChunks()
    {
        if (loadedChunks.isEmpty())
        {
            System.err.println(mcaFile.getName() + " had zero chunks!");
            System.exit(1);
        }
        return new ArrayList<>(loadedChunks);
    }
   
    private File mcaFile;
    private final ArrayList<ChunkData> loadedChunks;  
}
