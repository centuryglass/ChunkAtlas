/**
 * @file MapGenOptions.java
 * 
 * Loads and shares a set of options for generating maps.
 */
package com.centuryglass.mcmap.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiConsumer;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Loads and shares a set of options for generating maps. These options may be
 * provided in a JSON configuration file, or loaded from default options.
 */
public class MapGenOptions
{
    // Path to the resource holding default configuration options:
    private static final String DEFAULT_JSON_RESOURCE
            = "configDefaults/mapGen.json";
    
    /**
     * Loads or initializes map generation options on construction.
     * 
     * @param configFile  A JSON configuration file where options should be
     *                    loaded. If this parameter is null or is not a valid
     *                    JSON file, it will be ignored, and default options
     *                    will be used. If this file does not exist and can be
     *                    created, default options will be copied to this file.
     */
    MapGenOptions(File configFile)
    {
        // Attempt to load JSON options:
        if (configFile != null && configFile.isFile())
        {
            try (JsonReader reader 
                    = Json.createReader(new FileInputStream(configFile)))
            {
                loadedOptions = reader.readObject();
            }
            catch (FileNotFoundException | JsonException |
                    IllegalStateException ex) 
            {
                System.err.println("Failed to load " + configFile.getName()
                        + ", using default configuration options.");
                System.err.println("Error type encountered: "
                        + ex.getMessage());
                loadedOptions = null;
            }
        }
        
        // Attempt to load default options:
        try (InputStream optionStream = MapGenOptions.class.getResourceAsStream(
                        DEFAULT_JSON_RESOURCE))       
        {
            try (JsonReader reader = Json.createReader(optionStream))
            {
                defaultOptions = reader.readObject();
            }
            catch (JsonException | IllegalStateException ex)
            {
                System.err.println("Failed to load default config resource "
                        + DEFAULT_JSON_RESOURCE + ": " + ex.getMessage());
                defaultOptions = null;
            }
            // Copy defaults if appropriate:
            if (configFile != null && ! configFile.exists()
                    && configFile.canWrite() && configFile.createNewFile())
            {
                optionStream.reset();
                FileOutputStream output = new FileOutputStream(configFile);
                byte[] copyBuffer = new byte[1280];
                int bytesRead;
                while ((bytesRead = optionStream.read(copyBuffer)) != -1)
                {
                    output.write(copyBuffer, 0, bytesRead);
                }
                output.close();
            }
        }
        catch (IOException e)
        {
            System.err.println("Error reading/copying default config: "
                    + e.getMessage());
        }
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
        JsonArray regions = null;
        if (loadedOptions != null)
        {
            regions = loadedOptions.getJsonArray(JSONKeys.REGION_LIST);
        }
        if (regions == null)
        {
            regions = defaultOptions.getJsonArray(JSONKeys.REGION_LIST);
        }
        assert (regions != null);
        regions.forEach(regionItem -> 
        {
            if (regionItem == null) { return; }
            if (! (regionItem instanceof JsonObject))
            {
                System.err.println("Invalid region object "
                        + regionItem.toString());
                return; 
            }
            try
            {
                String regionPath = ((JsonObject) regionItem).getString(
                        JSONKeys.REGION_PATH);
                String regionName = ((JsonObject) regionItem).getString(
                        JSONKeys.REGION_NAME);
                File regionDir = new File(regionPath);
                if (! regionDir.isDirectory())
                {
                    System.err.println("Region directory path \""
                            + regionPath + "\" either doesn't exist or is not"
                            + " a directory.");
                    return;
                }
                action.accept(regionDir, regionName);
            }
            catch (NullPointerException | ClassCastException e)
            {
                System.err.println("Invalid region object: " + e.getMessage());
            }
        });
    }
    
    // Defines all JSON keys used in configuration files.
    private class JSONKeys
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
        public static final String GENERATE_MAPS = "true";
        // The path where Mappers will save images they create:
        public static final String OUTPUT_PATH = "path";
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
    }
    
    // Default options, loaded from the default option resource file.
    JsonObject defaultOptions;
    // Custom options provided on construction, or null.
    JsonObject loadedOptions;
    
}
