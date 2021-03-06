/**
 * @file  MapperThread.java
 * 
 *  Handles all map data updates within a separate thread.
 */
package com.centuryglass.chunk_atlas.threads;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.mapping.MapCollector;
import com.centuryglass.chunk_atlas.worldinfo.ChunkData;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;

public class MapperThread extends Thread 
{
    private static final String CLASSNAME = MapperThread.class.getName();
    
    // Duration the thread will wait for new ChunkData before pausing
    // to check if it should exit:
    private static final long TIMEOUT = 1; // seconds
    
    /**
     *  Stores the map collection object on construction.
     * 
     * @param mapCollector  The container holding all map instance types.
     */
    public MapperThread(MapCollector mapCollector)
    {
        Validate.notNull(mapCollector, "Map collector cannot be null.");
        this.mapCollector = mapCollector;
        chunkQueue = new LinkedBlockingQueue<>();
        shouldExit = new AtomicBoolean();
    }

    /**
     *  Signals to the map thread that it should stop once it processes
     *         all chunks in its queue.
     */
    public void requestStop()
    {
        final String FN_NAME = "requestStop";
        LogConfig.getLogger().logp(Level.FINE, CLASSNAME, FN_NAME,
                "{0} map chunks remaining, exiting once all are finished.",
                chunkQueue.size());
        shouldExit.set(true);
    }

    /**
     *  Adds another chunk for the thread to process.
     * 
     * @param chunk  Minecraft map data to add to all maps.
     */
    public void updateMaps(ChunkData chunk)
    {
        Validate.notNull(chunk, "Chunk data cannot be null.");
        chunkQueue.add(chunk);
    }

    @Override
    public void run()
    {
        final String FN_NAME = "run";
        LogConfig.getLogger().logp(Level.FINE, CLASSNAME, FN_NAME,
                "Starting MapperThread with ID {0}.", getId());
        while (! shouldExit.get() || ! chunkQueue.isEmpty())
        {
            ChunkData chunk = null;
            try
            {
                chunk = chunkQueue.poll(TIMEOUT, TimeUnit.SECONDS);
            }
            catch (InterruptedException e)
            {
                // If interrupted, just continue on to check shouldExit
                // again and go back to waiting.
            }
            if (chunk != null)
            {
                mapCollector.drawChunk(chunk);
            }
        }
        LogConfig.getLogger().logp(Level.FINE, CLASSNAME, FN_NAME,
                "Stopping MapperThread with ID {0}.", getId());
    }
    // Threadsafe data queue that allows waiting for new items:
    private final BlockingQueue<ChunkData> chunkQueue;
    // Atomically track whether the thread should exit:
    private final AtomicBoolean shouldExit;
    // Holds all map data:
    private final MapCollector mapCollector;  
}
