/**
 * @file  ActivityMapper.java
 *
 * Creates the Minecraft player activity map.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;

/**
 * ActivityMapper a map showing the relative amount of time that players have
 * spent in specific Minecraft region chunks.
 */
public class ActivityMapper extends Mapper
{
    /**
     * Sets map image properties on construction.
     *
     * @param imagePath       Path to where the map image will be saved.
     *
     * @param widthInChunks   Width of the mapped region in chunks.
     *
     * @param heightInChunks  Height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  Width/height in pixels of each chunk.
     */
    public ActivityMapper(String imagePath, int widthInChunks,
            int heightInChunks, int pixelsPerChunk)
    {
        super(imagePath, widthInChunks, heightInChunks, pixelsPerChunk);
        // Initialize inhabited time array:
        inhabitedTimes = new long[heightInChunks][widthInChunks];
        zMax = heightInChunks;
        xMax = widthInChunks;
        for (long[] row : inhabitedTimes)
        {
            Arrays.fill(row, -1);
        }
        xOffset = widthInChunks / 2;
        zOffset = heightInChunks / 2;
    }

    /**
     * Saves the inhabitedTime of a chunk so it can be drawn later.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       A null value, as correct colors can't be calculated until
     *               the largest inabitedTime value is found.
     */
    @Override
    public Color getChunkColor(ChunkData chunk)
    {
        Point chunkPt = chunk.getPos();
        long inhabitedTime = chunk.getInhabitedTime();
        maxTime = Math.max(inhabitedTime, maxTime);
        inhabitedTimes[chunkPt.y + zOffset][chunkPt.x + xOffset]
                = inhabitedTime;
        return null;
    }

    /**
     * Draws chunk activity data to the map after all chunks have been analyzed.
     *
     * @param map  The map image where activity data will be drawn.
     */
    @Override
    protected void finalProcessing(MapImage map)
    {
        int totalChunks = 0;
        for (int z = 0; z < xMax; z++)
        {
            for (int x = 0; x < xMax; x++)
            {
                if (inhabitedTimes[z][x] < 0)
                {
                    continue;
                }
                totalChunks++;
                long mapValue = inhabitedTimes[z][x];
                if (mapValue == 0)
                {
                    // Draw in black to distinguish from rows that have small
                    // but nonzero amounts of activity:
                    map.setChunkColor(x - xOffset, z - zOffset, new Color(0));
                    continue;
                }
                int brightness = (int) (mapValue * 255 / maxTime);
                map.setChunkColor(x - xOffset, z - zOffset,
                        new Color(0, brightness, 255 - brightness));
            }
        }
        super.finalProcessing(map);
    }

    // Inhabited times for all map chunks:
    long[][] inhabitedTimes;
    // Upper index ranges for inhabitedTimes:
    int zMax;
    int xMax;
    // Longest inhabited time:
    long maxTime;
    // Offsets added to chunk coordinates in inhabitedTimes to eliminate
    // negative values:
    int xOffset;
    int zOffset;
}
