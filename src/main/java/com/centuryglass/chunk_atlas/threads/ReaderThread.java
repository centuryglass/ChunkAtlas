/**
 * @file  ReaderThread.java
 * 
 *  Reads and processes Minecraft .mca region files within a thread.
 */
package com.centuryglass.chunk_atlas.threads;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.savedata.MCAFile;
import com.centuryglass.chunk_atlas.worldinfo.ChunkData;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.apache.commons.lang.Validate;

public class ReaderThread extends Thread
{
    private static final String CLASSNAME = ReaderThread.class.getName();
    
    /**
     *  Sets the list of paths this thread will process and the objects
     *         where it will send processed data.
     * 
     * @param regionFiles     The list of all region files the thread will
     *                        process.
     * 
     * @param regionMapper    The object responsible for creating maps from
     *                        region file data.
     * 
     * @param threadProgress  The object used to track region file processing
     *                        progress.
     */
    public ReaderThread(ArrayList<File> regionFiles, MapperThread regionMapper,
            ProgressThread threadProgress)
    {
        Validate.notNull(regionFiles, "Region file list cannot be null.");
        Validate.notEmpty(regionFiles, "Region file list cannot be empty.");
        Validate.notNull(regionMapper, "Region mapper cannot be null.");
        Validate.notNull(threadProgress, "Progress thread cannot be null.");
        this.regionFiles = regionFiles;
        this.regionMapper = regionMapper;
        this.threadProgress = threadProgress;
    }
    
    /**
     *  Read and map all assigned region files within the thread.
     */
    @Override
    public void run()
    {
        for (File file : regionFiles)
        {
            MCAFile regionFile;
            try
            {
                regionFile = new MCAFile(file);
            }
            catch (FileNotFoundException e)
            {
                LogConfig.getLogger().warning(e.toString());
                continue;
            }
            ArrayList<ChunkData> regionChunks = regionFile.getLoadedChunks();
            int chunkCount = 0;
            for (ChunkData chunk : regionChunks)
            {
                regionMapper.updateMaps(chunk);
                if (chunk.getErrorType() == ChunkData.ErrorFlag.NONE)
                {
                    chunkCount++;
                }
            }
            threadProgress.addToCounts(1, chunkCount);
        }
    }
    
    // Region file paths to process:
    private final ArrayList<File> regionFiles;
    // Mapper thread that will be passed processed region data:
    private final MapperThread regionMapper;
    // Shared progress tracker:
    private final ProgressThread threadProgress;
}
