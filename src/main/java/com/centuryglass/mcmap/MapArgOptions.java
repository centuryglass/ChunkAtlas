/**
 * @file MapArgOptions.java
 * 
 * Defines the set of command line arguments this application will accept.
 */
package com.centuryglass.mcmap;

import com.centuryglass.mcmap.util.args.ArgParser;
import com.centuryglass.mcmap.util.args.ArgParserFactory;

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
     * Sets the outPath to the map generation configuration file.
     */
    CONFIG_PATH,
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
     * Sets whether the Minecraft map texture is drawn behind single-image maps.
     */
    DRAW_BACKGROUND,
    /**
     * Sets the bounds (in Minecraft chunks) of the area drawn in single-image
     * maps.
     */
    BOUNDS,
    
    // Tile map options:
    /**
     * Sets whether tiled map images should be created, the directory where they
     * should be saved, and their base size in Minecraft chunks.
     */
    TILE_MAP,
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
    STRUCTURE_MAPS_ENABLED;
    
    /**
     * Creates an ArgParser that can read these options from a list of command
     * line arguments.
     * 
     * @return  The initialized argument parser. 
     */
    public static ArgParser<MapArgOptions> createArgParser()
    {
        ArgParserFactory<MapArgOptions> parserFactory = new ArgParserFactory();
        final String optionalBool = "[true|1|false|0]";
        parserFactory.setOptionProperties(HELP, "-h", "--help", 0, 0, "",
                "Print this help text.");
        parserFactory.setOptionProperties(CONFIG_PATH, "-c", "--config", 1, 1,
                "jsonConfigPath",
                "Set the map generation configuration file path.");
        parserFactory.setOptionProperties(REGION_DIRS, "-r", "--regionDirs",
                1, Integer.MAX_VALUE, "regionName=RegionPath ...",
                "Set Minecraft region data directory paths.");
        parserFactory.setOptionProperties(CHUNK_PIXELS, "-p", "--pixels", 1, 1, 
                "chunkWidth/Height",
                "Set the width and height in pixels to draw each map chunk.");
        
        parserFactory.setOptionProperties(IMAGE_MAP, "-i", "--image-map", 0, 2,
                optionalBool + " [outputPath]",
                "Read region files to create single-image maps of a bounded "
                + "area.");
        parserFactory.setOptionProperties(DRAW_BACKGROUND, "-d",
                "--draw-background", 0, 1, optionalBool,
                "Draw the Minecraft map texture behind single-image maps.");
        parserFactory.setOptionProperties(BOUNDS, "b", "--bounds", 4, 4,
                "xMin zMin width height",
                "Set the area in chunks that should be mapped.");
        
        parserFactory.setOptionProperties(TILE_MAP, "-t", "--tile-map", 0, 2,
                optionalBool + " [outputPath]",
                "Map each region directory within sets of image tiles.");
        parserFactory.setOptionProperties(TILE_ALT_SIZES, "-s", "--tile-sizes",
                1, Integer.MAX_VALUE, "size1 size2 ...",
                "Sets one or more alternate sizes of tile image to create.");
        
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
        parserFactory.setOptionProperties(STRUCTURE_MAPS_ENABLED, "-S",
                "--structure-map", 0, 1, optionalBool,
                "Enable or disable generation of Minecraft structure maps.");
        return parserFactory.createParser();
    }
}
