package com.centuryglass.mcmap.savedata;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MCAFile 
{
    // width/height in chunks of a region file:
    private static final int DIM_IN_CHUNKS = 32;
    
    
    /**
     * @brief  Loads data from a .mca file on construction.
     *
     * @param mcaPath  The Minecraft anvil region file path.
     */
    public MCAFile(Path mcaPath)
    {
        loadedChunks = new ArrayList();
        mcaFile = mcaPath.toFile();
        // read the region file's base coordinates from the file name:
        Point regionPt = getChunkCoords(mcaPath);
        if (regionPt.x == -1 && regionPt.y == -1)
        {
            System.err.println("Can't parse coordinates from file "
                    + mcaFile.toString());
            return;
        }
        
        ByteStream regionStream;
        try
        {
            regionStream = new ByteStream(mcaFile);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Failed to find file " + mcaFile.toString());
            return;
        }
             
        // Read chunk data offsets within the file:
        int numChunks = DIM_IN_CHUNKS * DIM_IN_CHUNKS;
        final int sectorSize = 4096;
        try
        {
            regionStream.readToBuffer(sectorSize);
        }
        catch (IOException e)
        {
            System.err.println("Warning: region file " 
                    + mcaPath.getFileName().toString() + " is invalid.");
            return;
        }
        Map<Point, Long> chunkOffsets = new HashMap();
        int invalidChunks = 0;
        for (int i = 0; i < numChunks; i++)
        {
            long sectorOffset;
            byte sectorCount;
            try
            {
                sectorOffset = regionStream.readInt(3);
                sectorCount = regionStream.readByte();
            }
            catch (IOException e)
            {
                System.err.println(e.getMessage());
                return;
            }
            if (sectorOffset == 0 && sectorCount == 0)
            {
                // That sector isn't loaded, skip it.
                continue;
            }
            long byteOffset = sectorOffset * sectorSize;
            if (byteOffset >= mcaFile.length() || byteOffset < sectorSize)
            {
                // Invalid, out of bounds sector, skip it.
                invalidChunks++;
                continue;
            }
            int chunkX = regionPt.x + (i % 32);
            int chunkZ = regionPt.y + (i / 32);
            Point chunkPos = new Point(chunkX, chunkZ);
            chunkOffsets.put(chunkPos, byteOffset);
        }
        // Find chunk data:
        for (Map.Entry<Point, Long> entry : chunkOffsets.entrySet())
        {
            try
            {
                ByteStream chunkStream = new ByteStream(mcaFile);
                long bytesSkipped = chunkStream.skip(entry.getValue());
                if (bytesSkipped == entry.getValue())
                {
                    int chunkByteSize = chunkStream.readInt();
                    byte compressionType = chunkStream.readByte();
                    byte[] chunkBytes = chunkStream.readBytes(chunkByteSize);
                    if (chunkBytes.length != chunkByteSize)
                    {
                        invalidChunks++;
                        continue;
                    }
                    ChunkNBT nbtData = new ChunkNBT(chunkBytes);
                    loadedChunks.add(nbtData.getChunkData(entry.getKey()));
                }
                else
                {
                    invalidChunks++;
                }
            }
            catch (IOException e)
            {
                invalidChunks++;
            }
        }
        if (invalidChunks > 0)
        {
            System.err.println("Warning: " + invalidChunks + " chunks in "
                    + mcaPath.getFileName().toString()
                    + " could not be loaded.");
        }
    }

    /**
     * @brief  Finds a region file's upper left chunk coordinate from its file
     *         name.
     *
     * @param filePath  The path to a region file.
     *
     * @return          The chunk coordinates, or { -1, -1 } if the file name
     *                  was not properly constructed.
     */
    public static Point getChunkCoords(Path filePath)
    {
        final String name = filePath.getFileName().toString();
        final int xStart = 2;
        final int xEnd = name.indexOf(".", xStart);
        final int zStart = xEnd + 1;
        final int zEnd = name.indexOf(".", zStart);
        if (xEnd < 0 || zEnd < 0)
        {
            return new Point(-1, -1);
        }
        
        return new Point(
                DIM_IN_CHUNKS * Integer.parseInt(name.substring(xStart, xEnd)),
                DIM_IN_CHUNKS * Integer.parseInt(name.substring(zStart, zEnd)));
    }

    /**
     * @brief  Gets information about all loaded chunks stored in the file.
     *
     * @return  The list of chunk data objects.
     */
    public ArrayList<ChunkData> getLoadedChunks()
    {
        return new ArrayList(loadedChunks);
    }
   
    private File mcaFile;
    private final ArrayList<ChunkData> loadedChunks;  
}
