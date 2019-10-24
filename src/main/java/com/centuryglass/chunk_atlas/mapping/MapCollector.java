/**
 * @file  MapCollector.java
 *
 * Provides a single interface for generating all map types.
 */
package com.centuryglass.chunk_atlas.mapping;

import com.centuryglass.chunk_atlas.mapping.maptype.Mapper;
import com.centuryglass.chunk_atlas.mapping.maptype.MapType;
import com.centuryglass.chunk_atlas.mapping.maptype.StructureMapper;
import com.centuryglass.chunk_atlas.mapping.maptype.ErrorMapper;
import com.centuryglass.chunk_atlas.mapping.maptype.BiomeMapper;
import com.centuryglass.chunk_atlas.mapping.maptype.RecentMapper;
import com.centuryglass.chunk_atlas.mapping.maptype.BasicMapper;
import com.centuryglass.chunk_atlas.mapping.maptype.ActivityMapper;
import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import com.centuryglass.chunk_atlas.worldinfo.ChunkData;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.commons.lang.Validate;

/**
 * MapCollector creates and manages a set of Mapper subclasses through a single
 * interface. Since map data is applied identically to each Mapper,
 * MapCollector takes care of the process of repeating each action for each map
 * type.
 */
public class MapCollector
{
    /**
     * Initializes all mappers to create single-image maps with fixed sizes.
     * 
     * @param imageDir        The directory where map images will be saved.
     * 
     * @param regionName      The name of the mapped region.
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
            String regionName,
            int xMin,
            int zMin,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk)
    {
        validateInitParams(imageDir, regionName, pixelsPerChunk);
        mappers = new ArrayList<>();
        initImageMappers(imageDir, regionName, xMin, zMin, widthInChunks,
                heightInChunks, pixelsPerChunk, getFullTypeSet());
    }
    
    /**
     * Initializes a specific set of mappers to create single-image maps with
     * fixed sizes.
     * 
     * @param imageDir        The directory where map images will be saved.
     * 
     * @param regionName      The name of the mapped region.
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
            String regionName,
            int xMin,
            int zMin,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk,
            Set<MapType> mapTypes)
    {
        validateInitParams(imageDir, regionName, pixelsPerChunk);
        mappers = new ArrayList<>();
        initImageMappers(imageDir, regionName, xMin, zMin, widthInChunks,
                heightInChunks, pixelsPerChunk, mapTypes);
    }
    
    /**
     * Initializes all mappers to create tiled image map folders.
     * 
     * @param imageDir       The directory where map images will be saved.
     * 
     * @param regionName      The name of the mapped region.
     * 
     * @param tileSize        The width and height of each tile, measured in
     *                        chunks.
     * 
     * @param altSizes        The list of alternate scaled tile sizes to
     *                        create.
     * 
     * @param pixelsPerChunk  The width and height in pixels of each mapped
     *                        chunk.
     */
    public MapCollector(File imageDir, String regionName, int tileSize,
            int[] altSizes, int pixelsPerChunk)
    {
        validateInitParams(imageDir, regionName, pixelsPerChunk);
        mappers = new ArrayList<>();
        initTileMappers(imageDir, regionName, tileSize, altSizes,
                pixelsPerChunk, getFullTypeSet());
    }
        
    /**
     * Initializes a specific set of mappers to create tiled image map folders.
     * 
     * @param imageDir        The directory where map images will be saved.
     * 
     * @param regionName      The name of the mapped region.
     * 
     * @param tileSize        The width of each tile, measured in chunks.
     * 
     * @param altSizes        The list of alternate scaled tile sizes to
     *                        create.
     * 
     * @param pixelsPerChunk  The width and height in pixels of each mapped
     *                        chunk.
     * 
     * @param mapTypes        The set of Mapper types that should be used.
     */
    public MapCollector(File imageDir, String regionName, int tileSize,
            int[] altSizes, int pixelsPerChunk, Set<MapType> mapTypes)
    {
        validateInitParams(imageDir, regionName, pixelsPerChunk);
        mappers = new ArrayList<>();
        initTileMappers(imageDir, regionName, tileSize, altSizes,
                pixelsPerChunk, mapTypes);
    }
    
    /**
     * Ensures MapCollector construction parameters are valid.
     * 
     * @param imageDir         The image output directory. This cannot be null,
     *                         and cannot be a regular file.
     * 
     * @param regionName      The name of the mapped region. This cannot be
     *                        null or empty.
     * 
     * @param pixelsPerChunk  The width and height in pixels of each mapped
     *                        chunk. This must be a positive value.
     */
    private void validateInitParams(File imageDir, String regionName,
            int pixelsPerChunk)
    {
        ExtendedValidate.couldBeDirectory(imageDir, "Image output directory");
        ExtendedValidate.notNullOrEmpty(regionName, "Region name");
        ExtendedValidate.isPositive(pixelsPerChunk, "Pixels per chunk");
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
        Validate.notNull("Chunk data cannot be null.");
        mappers.forEach((mapper) ->
        {
            mapper.drawChunk(chunk);
        });
    }
    
    /**
     * Gets map keys for all initialized Mappers.
     * 
     * @return  All combined map key items from each Mapper.
     */
    public JsonArray getMapKeys()
    {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        mappers.forEach((mapper) ->
        {
            Set<KeyItem> key = mapper.getMapKey();
            key.forEach((keyItem) ->
            {
                builder.add(keyItem.toJson());
            });
        });
        return builder.build();
    }
    
    /**
     * Gets a JSON object storing all map files created by all Mappers.
     * 
     * @return  A JSON object mapping each Mapper's name to its list of file
     *          paths.
     */
    public JsonObject getMapFiles()
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        mappers.forEach((mapper) ->
        {
            Map<Integer, JsonArrayBuilder> sizeBuilders = new HashMap<>();
            JsonArrayBuilder fileListBuilder = Json.createArrayBuilder();
            ArrayList<File> mapFiles = mapper.getMapFileList();
            mapFiles.forEach((file) ->
            {
                try
                {
                    int tileSize
                            = Integer.parseInt(file.getParentFile().getName());
                    if (! sizeBuilders.containsKey(tileSize))
                    {
                        sizeBuilders.put(tileSize, Json.createArrayBuilder());
                    }
                    sizeBuilders.get(tileSize).add(file.getPath());
                }
                catch (NumberFormatException e)
                {
                    fileListBuilder.add(file.getPath());
                }
            });
            if (sizeBuilders.isEmpty())
            {
                builder.add(mapper.getTypeName(), fileListBuilder.build());
            }
            else
            {
                JsonObjectBuilder typeBuilder = Json.createObjectBuilder();
                for (Map.Entry<Integer, JsonArrayBuilder> entry
                        : sizeBuilders.entrySet())
                {
                    typeBuilder.add(String.valueOf(entry.getKey()),
                            entry.getValue().build());
                }
                builder.add(mapper.getTypeName(), typeBuilder.build());
            }
        });
        return builder.build();
    }
    
    /**
     * Creates image mappers for a set of Mapper types.
     * 
     * @param imageDir        The directory where map images will be saved.
     * 
     * @param regionName      The name of the mapped region.
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
            String regionName,
            int xMin,
            int zMin,
            int widthInChunks,
            int heightInChunks,
            int pixelsPerChunk,
            Set<MapType> mapTypes)
    {
        createMappers(imageDir, regionName, mapTypes);
        mappers.forEach((mapper) ->
        {
            mapper.initImageMap(xMin, zMin, widthInChunks, heightInChunks,
                    pixelsPerChunk);
        });
    }
    
    /**
     * Creates tile mappers for a set of map types.
     * 
     * @param imageDir        The directory where map images will be saved.
     * 
     * @param regionName      The name of the mapped region.
     * 
     * @param tileSize        The width of each tile, measured in chunks.
     * 
     * @param altSizes        The list of alternate scaled tile sizes to
     *                        create.
     * 
     * @param pixelsPerChunk  The width and height in pixels of each mapped
     *                        chunk.
     * 
     * @param mapTypes        The set of Mapper types that will be used.
     */
    private void initTileMappers(File imageDir, String regionName,
            int tileSize, int[] altSizes, int pixelsPerChunk,
            Set<MapType> mapTypes)
    {
        createMappers(imageDir, regionName, mapTypes);
        mappers.forEach((mapper) ->
        {
            mapper.initTileMap(tileSize, altSizes, pixelsPerChunk);
        });
    }
    
    /**
     * Create all selected Mapper types.
     * 
     * @param imageDir    The directory where map images will be saved.
     * 
     * @param regionName  The name of the mapped region.
     * 
     * @param mapTypes    The set of MapType values indicating which mappers
     *                    should be created.
     */
    private void createMappers(File imageDir, String regionName,
            Set<MapType> mapTypes)
    {
        for (MapType type : mapTypes)
        {
            switch (type)
            {
                case TOTAL_ACTIVITY:
                    mappers.add(new ActivityMapper(imageDir, regionName));
                    break;
                case BASIC:
                    mappers.add(new BasicMapper(imageDir, regionName));
                case BIOME:
                    mappers.add(new BiomeMapper(imageDir, regionName));
                    break;
                case STRUCTURE:
                    mappers.add(new StructureMapper(imageDir, regionName));
                    break;
                case ERROR:
                    mappers.add(new ErrorMapper(imageDir, regionName));
                    break;
                case RECENT_ACTIVITY:
                    mappers.add(new RecentMapper(imageDir, regionName));
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
        Set<MapType> types = new TreeSet<>();
        types.addAll(Arrays.asList(MapType.values()));
        return types;
    }

    // All initialized mappers:
    private final ArrayList<Mapper> mappers;
}
