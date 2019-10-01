/**
 * @file  MapperThread.java
 * 
 *  Handles all map data updates within a separate thread.
 */
package com.centuryglass.mcmap.threads;

import com.centuryglass.mcmap.mapping.MapCollector;
import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang.Validate;

public class MapperThread extends Thread 
{
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
        chunkQueue = new LinkedBlockingQueue();
        shouldExit = new AtomicBoolean();
    }

    /**
     *  Signals to the map thread that it should stop once it processes
     *         all chunks in its queue.
     */
    public void requestStop()
    {
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
        while (! shouldExit.get() || ! chunkQueue.isEmpty())
        {
            ChunkData chunk = null;
            try
            {
                chunk = chunkQueue.poll(TIMEOUT, TimeUnit.SECONDS);
            }
            catch (InterruptedException e)
            {
                System.out.println("Mapper thread interrupted");
                // If interrupted, just continue on to check shouldExit
                // again and go back to waiting.
            }
            if (chunk != null)
            {
                mapCollector.drawChunk(chunk);
            }
        }
    }
    // Threadsafe data queue that allows waiting for new items:
    private final BlockingQueue<ChunkData> chunkQueue;
    // Atomically track whether the thread should exit:
    private final AtomicBoolean shouldExit;
    // Holds all map data:
    private final MapCollector mapCollector;  
}
