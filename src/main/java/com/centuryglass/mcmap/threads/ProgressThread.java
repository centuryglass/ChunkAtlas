/**
 * @file ProgressCount.java
 * 
 *  Provides thread-safe tracking of processed region file and chunk
 *         counts.
 */
package com.centuryglass.mcmap.threads;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class ProgressThread extends Thread
{
    // Duration the thread will wait for new progress updates before pausing
    // to check if it should exit:
    private static final long TIMEOUT = 1; // seconds
    
    // Stores buffered update data.
    private class Update
    {
        public int addedRegions;
        public int addedChunks;
    }
    
    /**
     * Initialize the ProgressCount with zero values, and save the total number
     * of region files for progress updates.
     * 
     * @param numRegions  The total number of region files in the map.
     */
    public ProgressThread(int numRegions)
    {
        updateQueue = new LinkedBlockingQueue();
        shouldExit = new AtomicBoolean();
        numRegionFiles = numRegions;
        if (numRegionFiles > 3000)
        {
            filesBeforeUpdate = 50;
        }
        else if (numRegionFiles > 1000)
        {
            filesBeforeUpdate = 20;
        }
        else if (numRegionFiles > 100)
        {
            filesBeforeUpdate = 5;
        }
        else
        {
            filesBeforeUpdate = 1;
        }
        regionCount = 0;
        chunkCount = 0;
    }
    
    /**
     *  Signals to the progress thread that it should stop once it
     *         processes all updates in its queue.
     */
    public void requestStop()
    {
        shouldExit.set(true);
    }

    /**
     *  Gets the number of processed region files.
     * 
     * @return  The region count. 
     */
    public synchronized int getRegionCount()
    {
        return regionCount;
    }

    /**
     *  Gets the number of processed Minecraft map chunks.
     * 
     * @return  The chunk count. 
     */
    public synchronized int getChunkCount()
    {
        return chunkCount;
    }

    /**
     *  Adds to the processed region and chunk counts.
     * 
     * @param regions  The number of additional processed region files.
     * 
     * @param chunks   The number of additional processed map chunks. 
     */
    public void addToCounts(int regions, int chunks)
    {
        Update update = new Update();
        update.addedRegions = regions;
        update.addedChunks = chunks;
        updateQueue.add(update);
    }
    
    /**
     *  Updates region and chunk counts, printing the region count if its
     *         value changes, and moving the cursor back so that the next print
     *         call overwrites the region count message.
     */
    @Override
    public void run()
    {
        while (! shouldExit.get() || ! updateQueue.isEmpty())
        {
            try
            {
                Update update = updateQueue.poll(TIMEOUT, TimeUnit.SECONDS);
                if (update != null)
                {
                    regionCount += update.addedRegions;
                    chunkCount += update.addedChunks;
                    boolean printUpdate = update.addedRegions > 0
                            && (regionCount % filesBeforeUpdate) == 0;
                    if (printUpdate)
                    {
                        System.out.println("Finished file " + regionCount + "/"
                                + numRegionFiles + ", " + chunkCount
                                + " chunks read.");
                        System.out.flush();
                    }
                }
            }
            catch (InterruptedException e)
            {
                // If interrupted, just continue on to check shouldExit
                // again and go back to waiting.
            }
        }
    }
    // Threadsafe data queue that allows waiting for new items:
    private final BlockingQueue<Update> updateQueue;
    // Atomically track whether the thread should exit:
    private final AtomicBoolean shouldExit;
    private final int numRegionFiles;
    private final int filesBeforeUpdate;
    private int regionCount;
    private int chunkCount;
    
}
