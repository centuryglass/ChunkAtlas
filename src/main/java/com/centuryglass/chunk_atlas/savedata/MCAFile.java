/**
 * @file MCAFile.java
 * 
 * Reads data from a Minecraft region file.
 */

package com.centuryglass.chunk_atlas.savedata;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import com.centuryglass.chunk_atlas.util.MapUnit;
import com.centuryglass.chunk_atlas.worldinfo.ChunkData;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;

/**
 * Reads data from a Minecraft region file.
 */
public class MCAFile 
{
    private static final String CLASSNAME = MCAFile.class.getName();
    
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
        final String FN_NAME = "MCAFile";
        ExtendedValidate.isFile(mcaFile, "Minecraft region file");
        loadedChunks = new ArrayList<>();
        // read the region file's base coordinates from the file name:
        Point regionPt = getChunkCoords(mcaFile);
        if (regionPt.x == -1 && regionPt.y == -1)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Can't parse coordinates from file {0}.", mcaFile);
            return;
        }
        
        FileByteBuffer regionBuffer;
        try
        {
            regionBuffer = new FileByteBuffer(mcaFile);
        }
        catch (FileNotFoundException e)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Failed to find file '{0}'.", mcaFile);
            return;
        }
        catch (IOException e)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Error reading region file:", e);
            return;
        }
        
        LogConfig.getLogger().logp(Level.FINER, CLASSNAME, FN_NAME,
                    "Scanning region file {0}:", mcaFile.getAbsolutePath());
             
        // Read chunk data offsets within the file:
        int numChunks = DIM_IN_CHUNKS * DIM_IN_CHUNKS;
        final int sectorSize = 4096;
        List<Point> invalidChunks = new ArrayList<>();
        
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
            catch (IndexOutOfBoundsException e)
            {
                LogConfig.getLogger().logp(Level.SEVERE, CLASSNAME, FN_NAME,
                        e.toString());
                // Invalid, out of bounds sector, skip it.
                loadedChunks.add(new ChunkData(getPos.apply(i),
                        ChunkData.ErrorFlag.BAD_OFFSET));
                invalidChunks.add(getPos.apply(i));
                regionBuffer.reset();
                continue;
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
                invalidChunks.add(getPos.apply(i));
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
                LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                        "Unexpected EOF: Read only {0} bytes, expected {1}.",
                        new Object[] { chunkBytes.length, chunkByteSize });
                invalidChunks.add(getPos.apply(i));
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
        if (! invalidChunks.isEmpty())
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "{0} chunks in region file '{1}' could not be loaded.",
                    new Object[] { invalidChunks.size(), mcaFile.getName() });
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Invalid chunk coordinates: {0}",
                    invalidChunks.stream()
                    .map((Point point) -> {
                        Point blockCoords = new Point(regionPt.x + point.x,
                                regionPt.y + point.y);
                        blockCoords = MapUnit.convertPoint(blockCoords,
                                MapUnit.CHUNK, MapUnit.BLOCK);
                        return "(" + blockCoords.x + ", " + blockCoords.y + ")";
                     })
                    .reduce("", (String list, String point) -> {
                        return list + (list.isEmpty() ? "" : ", ") + point;
                    }));
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
            
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, "getChunkCoords",
                    "chunk coords in region file '{0}' could not be loaded.",
                    new Object[] { regionFile });
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
        final String FN_NAME = "getLoadedChunks";
        if (loadedChunks.isEmpty())
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "'{0}' had zero map chunks.", mcaFile);
        }
        return new ArrayList<>(loadedChunks);
    }
   
    private File mcaFile;
    private final ArrayList<ChunkData> loadedChunks;  
}
