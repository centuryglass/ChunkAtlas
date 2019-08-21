/**
 * @file  ReaderThread.java
 * 
 *  Reads and processes Minecraft .mca region files within a thread.
 */
package com.centuryglass.mcmap.threads;

import com.centuryglass.mcmap.savedata.MCAFile;
import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.nio.file.Path;
import java.util.ArrayList;

public class ReaderThread extends Thread
{
    /**
     *  Sets the list of paths this thread will process and the objects
     *         where it will send processed data.
     * 
     * @param regionPaths     The list of all region file paths the thread will
     *                        process.
     * 
     * @param regionMapper    The object responsible for creating maps from
     *                        region file data.
     * 
     * @param threadProgress  The object used to track region file processing
     *                        progress.
     */
    public ReaderThread(ArrayList<Path> regionPaths, MapperThread regionMapper,
            ProgressThread threadProgress)
    {
        this.regionPaths = regionPaths;
        this.regionMapper = regionMapper;
        this.threadProgress = threadProgress;
    }
    
    /**
     *  Read and map all assigned region files within the thread.
     */
    @Override
    public void run()
    {
        for (Path entryPath : regionPaths)
        {
            MCAFile entryFile = new MCAFile(entryPath);
            ArrayList<ChunkData> entryChunks = entryFile.getLoadedChunks();
            entryChunks.forEach((chunk) ->
            {
                regionMapper.updateMaps(chunk);
            });
            threadProgress.addToCounts(1, entryChunks.size());
        }
    }
    
    // Region file paths to process:
    private final ArrayList<Path> regionPaths;
    // Mapper thread that will be passed processed region data:
    private final MapperThread regionMapper;
    // Shared progress tracker:
    private final ProgressThread threadProgress;
}
