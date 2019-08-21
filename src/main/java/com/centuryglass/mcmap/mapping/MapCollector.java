/**
 * @file  MapCollector.h
 *
 * @brief  Provides a single interface for generating all map types.
 */
package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.nio.file.Path;

public class MapCollector
{
    /**
     * @brief  Sets all map image properties on construction.
     *
     * @param imagePath       Generic image path used to create all map image
     *                        paths.
     *
     * @param dirInfoPath     Path where directory information will be loaded.
     *
     * @param widthInChunks   Width of the mapped region in chunks.
     *
     * @param heightInChunks  Height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  Width/height in pixels of each chunk.
     */
    public MapCollector(Path imagePath,
            Path dirInfoPath,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk)
    {
        basic = new BasicMapper(imagePath + "_basic.png", widthInChunks,
                heightInChunks, pixelsPerChunk);
        activity = new ActivityMapper(imagePath + "_activity.png",
                widthInChunks, heightInChunks, pixelsPerChunk);
        biome = new BiomeMapper(imagePath + "_biome.png",
                widthInChunks, heightInChunks, pixelsPerChunk);
        structure = new StructureMapper(imagePath + "_structure.png",
                widthInChunks, heightInChunks, pixelsPerChunk);
        directory = new DirectoryMapper(imagePath + "_directory.png",
                dirInfoPath, widthInChunks, heightInChunks, pixelsPerChunk);
    }


    /**
     * @brief  Writes all map images to their image paths.
     */
    public void saveMapFile()
    {
        basic.saveMapFile();
        activity.saveMapFile();
        biome.saveMapFile();
        structure.saveMapFile();
        directory.saveMapFile();
    }

    /**
     * @brief  Updates all maps with data from a single chunk.
     *
     * @param chunk  The world chunk to add to the maps.
     */
    public void drawChunk(ChunkData chunk)
    {
        basic.drawChunk(chunk);
        activity.drawChunk(chunk);
        biome.drawChunk(chunk);
        structure.drawChunk(chunk);
        directory.drawChunk(chunk);
    }

    // All mapper types:
    private final BasicMapper basic;
    private final ActivityMapper activity;
    private final BiomeMapper biome;
    private final StructureMapper structure;
    private final DirectoryMapper directory;
}
