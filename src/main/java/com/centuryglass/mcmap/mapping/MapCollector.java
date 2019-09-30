/**
 * @file  MapCollector.java
 *
 * Provides a single interface for generating all map types.
 */
package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.mapping.maptype.MapType;
import com.centuryglass.mcmap.mapping.maptype.StructureMapper;
import com.centuryglass.mcmap.mapping.maptype.ErrorMapper;
import com.centuryglass.mcmap.mapping.maptype.BiomeMapper;
import com.centuryglass.mcmap.mapping.maptype.RecentMapper;
import com.centuryglass.mcmap.mapping.maptype.BasicMapper;
import com.centuryglass.mcmap.mapping.maptype.ActivityMapper;
import com.centuryglass.mcmap.worldinfo.ChunkData;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 *  MapCollector creates and manages a set of Mapper subclasses through a single
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
     */
    public MapCollector(
            File imageDir,
            String imageName,
            int xMin,
            int zMin,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk)
    {
        mappers = new ArrayList();
        initImageMappers(imageDir, imageName, xMin, zMin, widthInChunks,
                heightInChunks, pixelsPerChunk, getFullTypeSet());
    }
    
    /**
     * Initializes a specific set of mappers to create single-image maps with
     * fixed sizes.
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
     * @param mapTypes        The set of Mapper types that should be used.
     */
    public MapCollector(
            File imageDir,
            String imageName,
            int xMin,
            int zMin,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk,
            Set<MapType> mapTypes)
    {
        mappers = new ArrayList();
        initImageMappers(imageDir, imageName, xMin, zMin, widthInChunks,
                heightInChunks, pixelsPerChunk, mapTypes);
    }
    
    /**
     * Initializes all mappers to create tiled image map folders.
     * 
     * @param imageDir   The directory where map images will be saved.
     * 
     * @param imageName  The start of the filename that will be used to
     * 
     * @param tileSize   The width of each tile, measured in chunks.
     */
    public MapCollector(File imageDir, String imageName, int tileSize)
    {
        mappers = new ArrayList();
        initTileMappers(imageDir, imageName, tileSize, getFullTypeSet());
    }
        
    /**
     * Initializes a specific set of mappers to create tiled image map folders.
     * 
     * @param imageDir   The directory where map images will be saved.
     * 
     * @param imageName  The start of the filename that will be used to
     * 
     * @param tileSize   The width of each tile, measured in chunks.
     * 
     * @param mapTypes   The set of Mapper types that should be used.
     */
    public MapCollector(File imageDir, String imageName, int tileSize,
            Set<MapType> mapTypes)
    {
        mappers = new ArrayList();
        initTileMappers(imageDir, imageName, tileSize, mapTypes);
    }
    
    /**
     * Writes all map images to their image paths.
     */
    public void saveMapFile()
    {
        mappers.forEach((mapper) -> {
            mapper.saveMapFile();
        });
    }

    /**
     * Updates all maps with data from a single chunk.
     *
     * @param chunk  The world chunk to add to the maps.
     */
    public void drawChunk(ChunkData chunk)
    {
        mappers.forEach((mapper) -> {
            mapper.drawChunk(chunk);
        });
    }
    
    /**
     * Creates image mappers for a set of Mapper types.
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
     * @param mapTypes        The set of Mapper types that should be used.
     */
    private void initImageMappers(
            File imageDir,
            String imageName,
            int xMin,
            int zMin,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk,
            Set<MapType> mapTypes)
    {
        createMappers(mapTypes);
        mappers.forEach((mapper) ->
        {
            mapper.initImageMap(imageDir, imageName, xMin, zMin, widthInChunks,
                    heightInChunks, pixelsPerChunk);
        });
    }
    
    /**
     * Creates tile mappers for a set of map types.
     * 
     * @param imageDir   The directory where map images will be saved.
     * 
     * @param imageName  The filename prefix used when naming tile images.
     * 
     * @param tileSize   The width of each tile, measured in chunks.
     * 
     * @param mapTypes   The set of Mapper types that will be used.
     */
    private void initTileMappers(File imageDir, String imageName, int tileSize,
            Set<MapType> mapTypes)
    {
        createMappers(mapTypes);
        mappers.forEach((mapper) ->
        {
            mapper.initTileMap(imageDir, imageName, tileSize);
        });
    }
    
    /**
     * Create all selected Mapper types.
     * 
     * @param mapTypes  The set of MapType values indicating which mappers
     *                  should be created.
     */
    private void createMappers(Set<MapType> mapTypes)
    {
        for (MapType type : mapTypes)
        {
            switch (type)
            {
                case ACTIVITY:
                    mappers.add(new ActivityMapper());
                    break;
                case BASIC:
                    mappers.add(new BasicMapper());
                case BIOME:
                    mappers.add(new BiomeMapper());
                    break;
                case STRUCTURE:
                    mappers.add(new StructureMapper());
                    break;
                case ERROR:
                    mappers.add(new ErrorMapper());
                    break;
                case RECENT:
                    mappers.add(new RecentMapper());
                    break;
            }   
        }
    }
    
    /**
     * Gets the set of all valid map types, to use when no specific subset of
     * map types has been selected.
     * 
     * @return  A set holding every possible MapType value. 
     */
    private Set<MapType> getFullTypeSet()
    {
        Set<MapType> types = new TreeSet();
        types.addAll(Arrays.asList(MapType.values()));
        return types;
    }

    // All initialized mappers:
    private final ArrayList<Mapper> mappers;
}
