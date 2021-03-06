/**
 * @file MapArgOptions.java
 * 
 * Defines the set of command line arguments this application will accept.
 */
package com.centuryglass.chunk_atlas;

import com.centuryglass.chunk_atlas.util.args.ArgParser;
import com.centuryglass.chunk_atlas.util.args.ArgParserFactory;

/**
 * All valid command line options used to configure map creation.
 */
public enum MapArgOptions
{
    // General options:
    /**
     * Print help text describing all options and exit.
     */
    HELP,
    /**
     * Sets the path to the map generation configuration file.
     */
    MAP_CONFIG_PATH,
    /**
     * Sets the path to the web server connection configuration file.
     */
    WEB_SERVER_CONFIG_PATH,
    /**
     * Sets the path to the application logging configuration file.
     */
    LOG_CONFIG_PATH,
    /**
     * Optionally sets a path where JSON server update messages will be cached.
     */
    UPDATE_CACHE_PATH,
    /**
     * If cached server update data is found, skip map generation and re-send
     * cached data.
     */
    USE_CACHED_UPDATE,
    /**
     * Sets the name and path of each region data directory that should be 
     * mapped.
     */
    REGION_DIRS,
    /**
     * Sets the width and height in pixels of each mapped Minecraft chunk.
     */
    CHUNK_PIXELS,
    
    // Single-Image map options:
    /**
     * Sets whether single-image maps should be created, and optionally sets
     * the directory where they will be saved.
     */
    IMAGE_MAP,
    /**
     * Sets whether the Minecraft map texture is drawn behind single-image
     * maps.
     */
    DRAW_BACKGROUND,
    /**
     * Sets the bounds (in Minecraft chunks) of the area drawn in single-image
     * maps.
     */
    BOUNDS,
    
    // Tile map options:
    /**
     * Sets whether tiled map images should be created, and the directory where
     * they should be saved.
     */
    TILE_MAP,
    /**
     * Sets the width and height in Minecraft chunks of each image tile.
     */
    TILE_SIZE,
    /**
     * Sets alternate tile image sizes that should be created from the main set
     * of map tiles.
     */
    TILE_ALT_SIZES,
    
    // Map type options:
    /**
     * Sets whether total player activity maps should be created.
     */
    ACTIVITY_MAPS_ENABLED,
    /**
     * Sets whether basic maps of all generated chunks should be created.
     */
    BASIC_MAPS_ENABLED,
    /**
     * Set whether Minecraft biome maps should be created.
     */
    BIOME_MAPS_ENABLED,
    /**
     * Sets whether region file error maps should be created.
     */
    ERROR_MAPS_ENABLED,
    /**
     * Sets whether recent chunk update maps should be created.
     */
    RECENT_MAPS_ENABLED,
    /**
     * Sets whether Minecraft structure maps should be created.
     */
    STRUCTURE_MAPS_ENABLED,
    /**
     * Generates a pair of security keys to use when connecting to the web
     * server.
     */
    GENERATE_RSA_KEYPAIR;
    
    /**
     * Creates an ArgParser that can read these options from a list of command
     * line arguments.
     * 
     * @return  The initialized argument parser. 
     */
    @SuppressWarnings("unchecked")
    public static ArgParser<MapArgOptions> createArgParser()
    {
        ArgParserFactory<MapArgOptions> parserFactory
                = new ArgParserFactory<>();
        final String optionalBool = "[<true>|<false>|<0>|<1>]";
        parserFactory.setOptionProperties(HELP, "-h", "--help", 0, 0, "",
                "Print this help text.");
        parserFactory.setOptionProperties(MAP_CONFIG_PATH, "-m",
                "--map-config", 1, 1, "<path/to/mapConfig.json>",
                "Set the map generation configuration file path.");
        parserFactory.setOptionProperties(WEB_SERVER_CONFIG_PATH, "-w",
                "--web-config", 1, 1, "<path/to/webConfig.json>",
                "Set the web server configuration file path.");
        parserFactory.setOptionProperties(LOG_CONFIG_PATH, "-l",
                "--log-config", 1, 1, "<path/to/logConfig.json>",
                "Set the application logging configuration file path.");
        parserFactory.setOptionProperties(UPDATE_CACHE_PATH, "-u",
                "--update-cache-path", 1, 1, "<path/to/updateCache.json>",
                "Set a path where the latest web server update message will be"
                + " saved.");
        parserFactory.setOptionProperties(USE_CACHED_UPDATE, "-c",
                "--use-cached", 0, 1, optionalBool,
                "If available, send cached update data to the web server "
                + "without generating new maps.");
        parserFactory.setOptionProperties(REGION_DIRS, "-r", "--regionDirs",
                1, Integer.MAX_VALUE / 10, "<regionName=RegionPath>...",
                "Set Minecraft region data directory paths.");
        parserFactory.setOptionProperties(CHUNK_PIXELS, "-p", "--pixels", 1, 1,
                "<size>",
                "Set the width and height in pixels to draw each map chunk.");
        
        parserFactory.setOptionProperties(IMAGE_MAP, "-i", "--image-map", 1, 1,
                "(<false>|<outputPath>)",
                "Set if and where to create single-image maps of a bounded "
                + "area.");
        parserFactory.setOptionProperties(DRAW_BACKGROUND, "-d",
                "--draw-background", 0, 1, optionalBool,
                "Draw the Minecraft map texture behind single-image maps.");
        parserFactory.setOptionProperties(BOUNDS, "b", "--bounds", 4, 4,
                "<xMin> <zMin> <width> <height>",
                "Set the area in chunks that should be mapped.");
        
        parserFactory.setOptionProperties(TILE_MAP, "-t", "--tile-map", 1, 1,
                "(<false>|<outputPath>)",
                "Set if and where to save maps as multiple tile images of"
                + "equal size");
        parserFactory.setOptionProperties(TILE_SIZE, "-s", "--tile-size", 1, 1,
                "<size>",
                "Sets the size in Minecraft chunks to use when creating map"
                 + "tiles.");
        parserFactory.setOptionProperties(TILE_ALT_SIZES, "-a",
                "--alt-tile-sizes", 1, Integer.MAX_VALUE / 10, "<size>...",
                "Sets one or more alternate sizes of tile image to create.");
        parserFactory.setOptionProperties(GENERATE_RSA_KEYPAIR, "-g",
                "--generate-rsa", 2, 2,
                "</path/to/publicKeyFile> </path/to/privateKeyFile>",
                "Skips all normal operations, and instead creates and saves a "
                + "pair of RSA key files that can be used to ensure that "
                + "communication with the web server is secure.");
        
        parserFactory.setOptionProperties(ACTIVITY_MAPS_ENABLED, "-A",
                "--activity-map", 0, 1, optionalBool,
                "Enable or disable generation of total player activity maps.");
        parserFactory.setOptionProperties(BASIC_MAPS_ENABLED, "-BA",
                "--basic-map", 0, 1, optionalBool,
                "Enable or disable generation of basic chunk location maps.");
        parserFactory.setOptionProperties(BIOME_MAPS_ENABLED, "-BI",
                "--biome-map", 0, 1, optionalBool,
                "Enable or disable generation of Minecraft biome maps.");
        parserFactory.setOptionProperties(RECENT_MAPS_ENABLED, "-R",
                "--recent-map", 0, 1, optionalBool,
                "Enable or disable generation of recent activity maps.");
        parserFactory.setOptionProperties(ERROR_MAPS_ENABLED, "-E",
                "--error-map", 0, 1, optionalBool,
                "Enable or disable generation of region file error maps.");
        parserFactory.setOptionProperties(STRUCTURE_MAPS_ENABLED, "-S",
                "--structure-map", 0, 1, optionalBool,
                "Enable or disable generation of Minecraft structure maps.");
        return parserFactory.createParser();
    }
}
