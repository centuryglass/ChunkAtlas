/**
 * @file ReaderFileQueue.java
 * 
 * Holds all map files waiting to be processed, and safely provides them to all
 * ReaderThread objects.
 */
package com.centuryglass.chunk_atlas.threads;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * ReaderFileQueue is a simple synchronized queue of File objects, uses to
 provide Minecraft region files to ReaderThread objects.
 */
public class ReaderFileQueue
{
    /**
     * Initializes the queue with a list of files.
     * 
     * @param filesToMap  The full list of Minecraft region files to map.
     */
    public ReaderFileQueue(ArrayList<File> filesToMap)
    {
        mapFiles = new ArrayDeque<>(filesToMap);
    }
    
    /**
     * Claims the next file waiting in the queue.
     * 
     * @return  A file removed from the queue.
     */
    public synchronized File getNextFile()
    {
        return mapFiles.pollLast();
    }
    
    /**
     * Gets the number of files in the queue.
     * 
     * @return  The number of map files waiting to be processed. 
     */
    public synchronized int size()
    {
        return mapFiles.size();
    }
    
    private final ArrayDeque<File> mapFiles;
}
