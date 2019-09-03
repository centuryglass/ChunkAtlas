/**
 * @file  MapCollector.java
 *
 * Provides a single interface for generating all map types.
 */
package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.io.File;

/**
 *  MapCollector creates and manages all Mapper subclasses through a single
 * interface. Since map data is applied identically to each Mapper, MapCollector
 * takes care of the process of repeating each action for each map type.
 */
public class MapCollector
{
    /**
     * Initializes all mappers to create single-image maps with fixed sizes.
     * 
     * @param imageDir        The directory where map images will be saved.
     * 
     * @param imageName       The start of the filename that will be used to
     *                        save all maps.
     * 
     * @param xMin            Lowest x-coordinate within the mapped area,
     *                        measured in chunks.
     * 
     * @param zMin            Lowest z-coordinate within the mapped area,
     *                        measured in chunks.
     *
     * @param widthInChunks   Width of the mapped region in chunks.
     *
     * @param heightInChunks  Height of the mapped image in chunks.
     *
     * @param pixelsPerChunk  Width/height in pixels of each chunk.
     * 
     * 
     * @param dirInfoFile     A file containing notable coordinates to mark on
     *                        the server directory map. Directory files should
     *                        list one point per line, formatted as 
     *                        X Z PlaceName.
     */
    public MapCollector(
            File imageDir,
            String imageName,
            int xMin,
            int zMin,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk,
            File dirInfoFile)
    {
        activity = new ActivityMapper(new File(imageDir, "activity_"
                + imageName), xMin, zMin, widthInChunks, heightInChunks,
                pixelsPerChunk);
        biome = new BiomeMapper(new File(imageDir, "biome_" + imageName), xMin,
                zMin, widthInChunks, heightInChunks, pixelsPerChunk);
        structure = new StructureMapper(new File(imageDir, "structure_"
                + imageName), xMin, zMin, widthInChunks, heightInChunks,
                pixelsPerChunk);
        directory = new DirectoryMapper(new File(imageDir, "directory_" 
                + imageName), dirInfoFile, xMin, zMin, widthInChunks,
                heightInChunks, pixelsPerChunk);
        errors = new ErrorMapper(new File(imageDir, "error_" + imageName),
                xMin, zMin, widthInChunks, heightInChunks, pixelsPerChunk);
    }
    
    /**
     * Initializes all mappers to create tiled image map folders.
     * 
     * 
     * @param imageDir         The directory where map images will be saved.
     * 
     * @param imageName        The start of the filename that will be used to
     * 
     * @param tileSize        The width of each tile, measured in chunks.
     * 
     * @param pixelsPerChunk  The width and height in pixels of each chunk.
     *                         save all maps.
     * 
     * @param dirInfoFile      A file containing notable coordinates to mark on
     *                         the server directory map. Directory files should
     *                         list one point per line, formatted as 
     *                         X Z PlaceName.
     */
    public MapCollector(File imageDir, String imageName, int tileSize,
            int pixelsPerChunk, File dirInfoFile)
    {
        activity = new ActivityMapper(new File(imageDir, "activity"), imageName,
                tileSize, pixelsPerChunk);
        biome = new BiomeMapper(new File(imageDir, "biome"), imageName,
                tileSize, pixelsPerChunk);
        structure = new StructureMapper(new File(imageDir, "structure"),
                imageName, tileSize, pixelsPerChunk);
        directory = new DirectoryMapper(new File(imageDir, "directory"),
                imageName, dirInfoFile, tileSize, pixelsPerChunk);
        errors = new ErrorMapper(new File(imageDir, "errors"), imageName,
                tileSize, pixelsPerChunk);
    }


    /**
     * Writes all map images to their image paths.
     */
    public void saveMapFile()
    {
        activity.saveMapFile();
        biome.saveMapFile();
        structure.saveMapFile();
        directory.saveMapFile();
        errors.saveMapFile();
    }

    /**
     * Updates all maps with data from a single chunk.
     *
     * @param chunk  The world chunk to add to the maps.
     */
    public void drawChunk(ChunkData chunk)
    {
        activity.drawChunk(chunk);
        biome.drawChunk(chunk);
        structure.drawChunk(chunk);
        directory.drawChunk(chunk);
        errors.drawChunk(chunk);
    }

    // All mapper types:
    private final ActivityMapper activity;
    private final BiomeMapper biome;
    private final StructureMapper structure;
    private final DirectoryMapper directory;
    private final ErrorMapper errors;
}
