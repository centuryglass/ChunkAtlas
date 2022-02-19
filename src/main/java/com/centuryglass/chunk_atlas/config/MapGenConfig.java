/**
 * @file MapGenConfig.java
 * 
 * Loads and shares a set of options for generating maps.
 */
package com.centuryglass.chunk_atlas.config;

import com.centuryglass.chunk_atlas.mapping.maptype.MapType;
import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import com.centuryglass.chunk_atlas.worldinfo.Biome;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import org.apache.commons.lang.Validate;

/**
 * Loads and shares a set of options for generating maps. These options may be
 * provided in a JSON configuration file, or loaded from default options.
 */
public class MapGenConfig extends ConfigFile
{
    private static final String CLASSNAME = MapGenConfig.class.getName();
    
    // Path to the resource holding default configuration options:
    private static final String DEFAULT_JSON_RESOURCE
            = "/configDefaults/mapGen.json";
    // String to print when invalid option types are found:
    private static final String INVALID_OPTION_MSG = " options are invalid,"
                    + " check the map generation configuration file.";
    
    /**
     * Loads or initializes map generation options on construction.
     * 
     * @param configFile  A JSON configuration file holding options to load.
     *                    If this parameter is null or is not a valid JSON
     *                    file, it will be ignored, and default options will be
     *                    used. If this file does not exist and can be created,
     *                    default options will be copied to this file.
     */
    public MapGenConfig(File configFile)
    {
        super(configFile, DEFAULT_JSON_RESOURCE);
        
        // Load any custom biome files:
        JsonArray biomeFiles = (JsonArray) getSavedOrDefaultOptions(JsonKeys.CUSTOM_BIOMES);
        biomeFiles.forEach(biomeFilePath ->               
        {
            JsonStructure addedBiomes = null;
            try {
                File jsonFile = new File(biomeFilePath.toString().replace("\"", ""));
                FileInputStream fileStream = new FileInputStream(jsonFile);
                JsonReader reader = Json.createReader(fileStream);
                addedBiomes = reader.read();
            }
            catch (JsonException |  IllegalStateException | IOException ex) 
            {
                LogConfig.getLogger().log(Level.WARNING, "Skipping invalid biome file {0}, error={1}",
                        new Object[]{biomeFilePath.toString(), ex.toString()});
            }
            if (addedBiomes != null)
            {
                LogConfig.getLogger().log(Level.INFO, "Loading biomes from file {0}", biomeFilePath.toString());
                Biome.loadBiomes(addedBiomes);
            }     
        });
    }
    
    /**
     * Runs an action for each valid Minecraft region data directory defined in
     * configuration.
     * 
     * @param action  An action that will run for each region directory.
     *                Parameters passed in will be the directory File, and the
     *                region's name.
     */
    public void forEachRegionPath(BiConsumer<File, String> action)
    {
        final String FN_NAME = "forEachRegionPath";
        Validate.notNull(action, "Region path action must not be null.");
        JsonArray regions = (JsonArray) getSavedOrDefaultOptions(
                JsonKeys.REGION_LIST);
        assert (regions != null);
        regions.forEach(regionItem -> 
        {
            if (regionItem == null) { return; }
            if (! (regionItem instanceof JsonObject))
            {
                LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                        "Invalid region object: {0}", regionItem);
                return; 
            }
            try
            {
                String regionPath = ((JsonObject) regionItem).getString(
                        JsonKeys.REGION_PATH);
                String regionName = ((JsonObject) regionItem).getString(
                        JsonKeys.REGION_NAME);
                File regionDir = new File(regionPath);
                if (! regionDir.isDirectory())
                {
                    LogConfig.getLogger().logp(Level.WARNING, CLASSNAME,
                            FN_NAME,
                            "Region directory path '{0}' either "
                            + "doesn't exist or is not a directory.",
                            regionPath);
                    return;
                }
                action.accept(regionDir, regionName);
            }
            catch (NullPointerException | ClassCastException e)
            {
                LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                        "Invalid region object:", e);
            }
        });
    }
    
    /**
     * Holds a collection of options for generating single-image maps within an
     * immutable data structure.
     */
    public class SingleImage
    {
        /**
         * Set all options on construction.
         * 
         * @param enabled          Whether single image maps will be generated.
         * 
         * @param drawBackground   Whether a Minecraft map background image
         *                         will be drawn behind the maps.
         * 
         * @param outPath          The path where map images will be saved.
         * 
         * @param xMin             The minimum x-coordinate in chunks drawn
         *                         within the map bounds.
         * 
         * @param zMin             The minimum z-coordinate in chunks drawn
         *                         within the map bounds.
         * 
         * @param width            The width in chunks of the mapped area.
         * 
         * @param height           The height in chunks of the mapped area. 
         */
        protected SingleImage(boolean enabled, boolean drawBackground,
                String outPath, int xMin, int zMin, int width, int height)
        {
            ExtendedValidate.couldBeDirectory(new File(outPath),
                    " Image output path");
            this.enabled = enabled;
            this.drawBackground = drawBackground;
            this.outPath = outPath;
            this.xMin = xMin;
            this.zMin = zMin;
            this.width = width;
            this.height = height;
        }
        
        public final boolean enabled;
        public final boolean drawBackground;
        public final String outPath;
        public final int xMin;
        public final int zMin;
        public final int width;
        public final int height;
    }
    
    /**
     * Gets all options specifically required for generating single-image maps.
     * 
     * @return  An object holding all settings that are only used in
     *          single-image map generation, or null if options were not found.
     */
    public SingleImage getSingleImageOptions()
    {
        final String FN_NAME = "getSingleImageOptions";
        JsonObject imageOptions = (JsonObject) getObjectOption(
                JsonKeys.IMAGE_MAP_OPTIONS, null);
        if (imageOptions == null)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Single-image map generation {0}", INVALID_OPTION_MSG);
            return null;
        }
        final boolean enabled = imageOptions.getBoolean(
                JsonKeys.GENERATE_MAPS);
        final boolean drawBackground = imageOptions.getBoolean(
                JsonKeys.DRAW_BACKGROUND);
        final String path = imageOptions.getString(JsonKeys.OUTPUT_PATH);
        final int xMin = imageOptions.getInt(JsonKeys.X_MIN);
        final int zMin = imageOptions.getInt(JsonKeys.Z_MIN);
        final int width = imageOptions.getInt(JsonKeys.WIDTH);
        final int height = imageOptions.getInt(JsonKeys.HEIGHT);
        return new SingleImage(enabled, drawBackground, path, xMin, zMin,
                width, height);
    }
        
    /**
     * Holds a collection of options for generating map image tiles within an
     * immutable data structure.
     */
    public class MapTiles
    {
        /**
         * Sets all map tile options on construction.
         * 
         * @param enabled          Whether map tiles will be generated.
         * 
         * @param outPath          The path to the directory where map tiles
         *                         will be saved.
         * 
         * @param tileSize         The width and height in pixels of each map
         *                         tile.
         * 
         * @param alternateSizes   An array of alternate tile sizes to create
         *                         by rescaling the main set of tile images.
         */
        protected MapTiles(boolean enabled, String outPath, int tileSize,
                int[] alternateSizes)
        {
            ExtendedValidate.couldBeDirectory(new File(outPath),
                    "Tile output path");
            this.enabled = enabled;
            this.outPath = outPath;
            this.tileSize = tileSize;
            this.alternateSizes = alternateSizes;
        }
        
        /**
         * Gets the list of alternate map tile sizes that should be created.
         * 
         * @return  An array of alternate image resolutions, or null if no
         *          alternate tile sizes should be generated.
         */
        public int[] getAlternateSizes()
        {
            if (alternateSizes == null) { return null; }
            return Arrays.copyOf(alternateSizes, alternateSizes.length);
        }
        
        public final boolean enabled;
        public final String outPath;
        public final int tileSize;
        private final int[] alternateSizes;
    }
    
    /**
     * Gets all options used specifically for generating map image tiles.
     * 
     * @return  The set of all options that only apply to generating map tiles,
     *          or null if tile options could not be loaded.
     */
    public MapTiles getMapTileOptions()
    {
        final String FN_NAME = "getMapTileOptions";
        JsonObject tileOptions = getObjectOption(JsonKeys.TILE_MAP_OPTIONS,
                null);
        if (tileOptions == null)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Tiled map generation {0}", INVALID_OPTION_MSG);
            return null;
        }
        final boolean enabled = tileOptions.getBoolean(JsonKeys.GENERATE_MAPS);
        final String path = tileOptions.getString(JsonKeys.OUTPUT_PATH);
        final int tileSize = tileOptions.getInt(JsonKeys.TILE_SIZE);
        JsonArray altSizeJson = tileOptions.getJsonArray(
                JsonKeys.SCALED_TILES);
        final int[] altSizes = new int[altSizeJson.size()];
        for (int i = 0; i < altSizes.length; i++)
        {
            altSizes[i] = altSizeJson.getInt(i);
        }
        return new MapTiles(enabled, path, tileSize, altSizes);
    }
    
    /**
     * Finds the width and height in image pixels that should be used for each
     * chunk in the map.
     * 
     * @return  The number of pixels per chunk defined in the configuration
     *          file, or the default value.
     */
    public int getPixelsPerChunk()
    {
        final String FN_NAME = "getPixelsPerChunk";
        int px = getIntOption(JsonKeys.CHUNK_PX, -1);
        if (px <= 0)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Invalid pixels per chunk {0}", INVALID_OPTION_MSG);
            return 1;
        }
        return px;
    }
    
    /**
     * Gets the set of MapTypes that should be generated.
     * 
     * @return  The set of all map types to create. 
     */
    public Set<MapType> getEnabledMapTypes()
    {
        final String FN_NAME = "getEnabledMapTypes";
        Set<MapType> typesUsed = new TreeSet<>();
        JsonObject typeSettings = getObjectOption(JsonKeys.MAP_TYPES_USED,
                null);
        if (typeSettings == null)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Invalid MapType selection {0}", INVALID_OPTION_MSG);
            return typesUsed;
        }
        for (MapType type : MapType.values())
        {
            if(typeSettings.getBoolean(type.name(), false))
            {
                typesUsed.add(type);
            }
        }
        return typesUsed;
    }  

    // Defines all JSON keys used in configuration files.
    private class JsonKeys
    {
        // The array of Minecraft region file directories to map:
        public static final String REGION_LIST = "regions";
        // The path to a Minecraft region file directory:
        public static final String REGION_PATH = "regionFilePath";
        // The name to use for a Minecraft region file directory:
        public static final String REGION_NAME = "name";
        // Width and height in pixels of each generated map type:
        public static final String CHUNK_PX = "pixelsPerChunk";
        // The set of options to use for single-image maps:
        public static final String IMAGE_MAP_OPTIONS = "singleImageMaps";
        // The set of options to use when generating map image tiles:
        public static final String TILE_MAP_OPTIONS = "mapTiles";
        // Whether a specific Mapper type (Image or Tile) will be used:
        public static final String GENERATE_MAPS = "generate";
        // The path where Mappers will save images they create:
        public static final String OUTPUT_PATH = "outPath";
        // Whether single-image maps will be drawn over a background image:
        public static final String DRAW_BACKGROUND = "drawBackground";
        // Minimum chunk x-coordinate to draw in single image maps:
        public static final String X_MIN = "xMin";
        // Minimum chunk z-coordinate to draw in single image maps:
        public static final String Z_MIN = "zMin";
        // Width in chunks of single image maps:
        public static final String WIDTH = "width";
        // Height in chunks of single image maps:
        public static final String HEIGHT = "height";
        // Width and height in chunks of each map tile image:
        public static final String TILE_SIZE = "tileSize";
        // Alternate tile resolution sizes that should be generated from the
        // main set of tile images:
        public static final String SCALED_TILES = "createScaled";
        // The set of MapTypes used when generating maps:
        public static final String MAP_TYPES_USED = "mapTypes";
        // JSON file(s) defining additional biomes:
        public static final String CUSTOM_BIOMES = "customBiomeFiles";
    } 
}
