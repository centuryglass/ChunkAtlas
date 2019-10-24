/**
 * @file  MapCreator.java
 * 
 * Stores map creation options, and applies them to generate Minecraft map
 * images.
 */
package com.centuryglass.chunk_atlas;

import com.centuryglass.chunk_atlas.config.MapGenConfig;
import com.centuryglass.chunk_atlas.mapping.MapCollector;
import com.centuryglass.chunk_atlas.mapping.maptype.MapType;
import com.centuryglass.chunk_atlas.mapping.TileMap;
import com.centuryglass.chunk_atlas.mapping.images.ImageStitcher;
import com.centuryglass.chunk_atlas.savedata.MCAFile;
import com.centuryglass.chunk_atlas.threads.MapperThread;
import com.centuryglass.chunk_atlas.threads.ProgressThread;
import com.centuryglass.chunk_atlas.threads.ReaderThread;
import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import com.centuryglass.chunk_atlas.util.MapUnit;
import com.centuryglass.chunk_atlas.util.args.ArgOption;
import com.centuryglass.chunk_atlas.util.args.ArgParser;
import com.centuryglass.chunk_atlas.util.args.InvalidArgumentException;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.commons.lang.Validate;

/**
 * Stores map creation options, and applies them to generate Minecraft map
 * images.
 */
public final class MapCreator 
{   
    // Debug: Set whether to use multiple threads to scan region files:
    private static final boolean MULTI_REGION_THREADS = true;
    
    /**
     * Initialize the MapCreator with all options unset.
     */
    public MapCreator()
    {
        regionsToMap = new ArrayList<>();
        enabledMapTypes = new TreeSet<>();
        keyBuilder = Json.createArrayBuilder();
        tileListBuilder = Json.createObjectBuilder();
    }
    
    /**
     * Create the MapCreator with options loaded from a configuration file.
     * 
     * @param mapConfig  A map generation configuration file object.
     */
    public MapCreator(MapGenConfig mapConfig)
    {
        regionsToMap = new ArrayList<>();
        enabledMapTypes = new TreeSet<>();
        keyBuilder = Json.createArrayBuilder();
        tileListBuilder = Json.createObjectBuilder();
        if (mapConfig != null)
        {
            applyConfigOptions(mapConfig);
        }
    }
    
    /**
     * Applies all map generation options defined in a configuration file.
     * 
     * @param mapConfig  A map generation configuration file object.
     */
    public void applyConfigOptions(MapGenConfig mapConfig)
    {
        Validate.notNull(mapConfig, "Map config object cannot be null.");
        if (mapConfig != null)
        {
            setPixelsPerChunk(mapConfig.getPixelsPerChunk());
            MapGenConfig.SingleImage imageMapOptions
                    = mapConfig.getSingleImageOptions();
            setSingleImageMapsEnabled(imageMapOptions.enabled);
            setImageMapOutputDir(new File(imageMapOptions.outPath));
            setDrawBackgrounds(imageMapOptions.drawBackground);
            setImageMapBounds(imageMapOptions.xMin,
                    imageMapOptions.zMin,
                    imageMapOptions.width,
                    imageMapOptions.height);
            
            MapGenConfig.MapTiles tileOptions
                    = mapConfig.getMapTileOptions();
            setTileMapsEnabled(tileOptions.enabled);
            setTileOutputDir(new File(tileOptions.outPath));
            setTileSize(tileOptions.tileSize);
            setAltTileSizes(tileOptions.getAlternateSizes());
            
            mapConfig.forEachRegionPath((regionDir, name)->
            {
                try 
                {
                    addRegion(name, regionDir);
                }
                catch (FileNotFoundException e)
                {
                    System.err.println("Warning: Cannot find directory \""
                        + regionDir.toString() + "\" for region \""
                        + name + "\", this region will be ignored.");
                }
            });
            enabledMapTypes = mapConfig.getEnabledMapTypes();
        }   
    }
    
    /**
     * Applies all map generation options defined in a set of command line
     * arguments.
     * 
     * @param parsedArgs                 An ArgParser that has already parsed
     *                                   the command line argument array.
     * 
     * @throws InvalidArgumentException  If any option parameters are invalid.
     * 
     * @throws FileNotFoundException     If the region file directory does not
     *                                   exist.
     */
    public void applyArgOptions(ArgParser<MapArgOptions> parsedArgs)
            throws InvalidArgumentException, FileNotFoundException
    {
        Validate.notNull(parsedArgs, "Parsed argument object cannot be null");
        ArgOption<MapArgOptions>[] options = parsedArgs.getAllOptions();
        for (ArgOption<MapArgOptions> option : options)
        {
            switch (option.getType())
            {
                case REGION_DIRS:
                    regionsToMap.clear();
                    for (int i = 0; i < option.getParamCount(); i++)
                    {
                        String regionPath = option.getParameter(i);
                        String regionName = null;
                        File regionDir;
                        if (regionPath.contains("="))
                        {
                            int divide = regionPath.indexOf("=");
                            regionName = regionPath.substring(0, divide);
                            regionPath = regionPath.substring(divide + 1);
                        }
                        regionDir = new File(regionPath);
                        ExtendedValidate.isDirectory(regionDir,
                                "Region file directory ");
                        if (regionName == null)
                        {
                            regionName = regionDir.getName();
                        }
                        addRegion(regionName, regionDir);                
                    }
                    break;
                case CHUNK_PIXELS:
                {
                    setPixelsPerChunk(option.parseIntParam(0, px -> px > 0));
                    break;
                }
                case IMAGE_MAP:
                {
                    String param = option.getParameter(0);
                    if (param.equals("0") || param.equalsIgnoreCase("false"))
                    {
                        setSingleImageMapsEnabled(false);
                    }
                    else
                    {
                        setSingleImageMapsEnabled(true);
                        setImageMapOutputDir(new File(param));
                    }
                    break;
                }
                case DRAW_BACKGROUND:
                    setDrawBackgrounds(option.boolOptionStatus());
                    break;
                case BOUNDS:
                {
                    setImageMapBounds(option.parseIntParam(0, null),
                            option.parseIntParam(1, null),
                            option.parseIntParam(2, w -> w > 0),
                            option.parseIntParam(3, h -> h > 0));
                }
                case TILE_MAP:
                {
                    String param = option.getParameter(0);
                    if (param.equals("0") || param.equalsIgnoreCase("false"))
                    {
                        setTileMapsEnabled(false);
                    }
                    else
                    {
                        setTileMapsEnabled(true);
                        setTileOutputDir(new File(param));
                    }
                    break;
                }
                
                case TILE_ALT_SIZES:
                {
                    int[] altSizes = new int[option.getParamCount()];
                    for (int i = 0; i < option.getParamCount(); i++)
                    {
                        altSizes[i] = option.parseIntParam(i,
                                size -> size > 0);
                    }
                    setAltTileSizes(altSizes);
                    break;
                }
                case ACTIVITY_MAPS_ENABLED:
                    setMapTypeEnabled(MapType.TOTAL_ACTIVITY,
                            option.boolOptionStatus());
                    break;
                case BASIC_MAPS_ENABLED:
                    setMapTypeEnabled(MapType.BASIC,
                            option.boolOptionStatus());
                    break;
                case BIOME_MAPS_ENABLED:
                    setMapTypeEnabled(MapType.BIOME,
                            option.boolOptionStatus());
                    break;
                case ERROR_MAPS_ENABLED:
                    setMapTypeEnabled(MapType.ERROR,
                            option.boolOptionStatus());
                    break;
                case RECENT_MAPS_ENABLED:
                    setMapTypeEnabled(MapType.RECENT_ACTIVITY,
                            option.boolOptionStatus());
                    break;
                case STRUCTURE_MAPS_ENABLED:
                    setMapTypeEnabled(MapType.STRUCTURE,
                            option.boolOptionStatus());
                    break;
            }
        }   
    }
    
    /**
     * Applies all map generation options to create maps in every relevant type
     * and format for every Minecraft region directory added.
     */
    public void createMaps()
    {
        System.out.println("Creating " + enabledMapTypes.size() + " map types "
                + "for " + regionsToMap.size() + " region(s).");
        for (Region region : regionsToMap)
        {
            System.out.println("Mapping region " + region.name + ":");
            // Ensure output directories are region-specific:
            Function<File, File> getRegionOutDir = regionOutDir->
            {
                if (! regionOutDir.getName().equals(region.name))
                {
                    regionOutDir = new File(regionOutDir, region.name);
                }
                if (! regionOutDir.isDirectory())
                {
                    regionOutDir.mkdirs();
                }
                return regionOutDir;
            };
            File regionTileOutDir = getRegionOutDir.apply(tileOutDir);
            File regionImageOutDir = getRegionOutDir.apply(imageOutDir);
            // Remove old map images:
            Deque<File> toDelete = new ArrayDeque<>();
            if (tilesEnabled) 
            { 
                toDelete.add(regionTileOutDir);
            }
            if (imageMapsEnabled)
            {
                toDelete.add(regionImageOutDir);
            }
            while (! toDelete.isEmpty())
            {
                File top = toDelete.pop();
                if (top.isDirectory())
                {
                    toDelete.addAll(Arrays.asList(top.listFiles()));
                }
                else if (top.isFile() && top.getName().endsWith(".png"))
                {
                    top.delete();
                }
            }
            if (tilesEnabled)
            {
                createTileMaps(region, regionTileOutDir);
                // Find and store all tile map directories:
                ArrayList<File> tileDirs = new ArrayList<>();
                Deque<File> toSearch = new ArrayDeque<>();
                toSearch.add(regionTileOutDir);
                while (! toSearch.isEmpty())
                {
                    File searchDir = toSearch.pop();
                    boolean tilesFound = false;
                    assert (searchDir != null);
                    assert (searchDir.isDirectory());
                    for (File child : searchDir.listFiles())
                    {
                        if (child.isDirectory())
                        {
                            String name = child.getName();
                            // Detect and skip resized tile directories:
                            if (name.matches("^\\d+$"))
                            {
                                int nameValue = Integer.parseInt(name);
                                if (nameValue != tileSize)
                                {
                                    continue;
                                }
                            }
                            toSearch.push(child);
                        }
                        else if (! tilesFound)
                        {
                            tilesFound = (child.getPath().endsWith(".png"));
                        }
                    }
                    if (tilesFound)
                    {
                        tileDirs.add(searchDir);
                    }
                }
                // If single-image maps are also enabled, create them by
                // stitching together tile images:
                if (imageMapsEnabled)
                {
                    tileDirs.forEach((tileDir) ->
                    {
                        File outFile = new File(regionImageOutDir,
                                tileDir.getParentFile().getName() + ".png");
                        System.out.println("Creating " + outFile.toString()
                                + " from tiles at " + tileDir.toString()
                                + ".");
                        try
                        {
                            ImageStitcher.stitch(tileDir, outFile, xMin, zMin,
                                    width, height, pixelsPerChunk, tileSize,
                                    drawBackgrounds);
                        }
                        catch (FileNotFoundException e)
                        {
                            System.err.println("Failed to create map: "
                                    + e.getMessage());
                        }
                    });
                }
            }
            else if (imageMapsEnabled)
            {
                createSingleImageMaps(region, regionImageOutDir);
            }  
        }
        System.out.println("Map generation completed.");
    }
     
    /**
     * Set if the mapper should generate map image tiles.
     * 
     * @param enabled  Whether maps will be saved in multiple image tiles with
     *                 the same size.
     */
    public void setTileMapsEnabled(boolean enabled)
    {
        tilesEnabled = enabled;
    }
    
    /**
     * Sets the directory where map image tiles will be saved.
     * 
     * @param outDir  Map tiles will be saved in subdirectories created in this
     *                directory.
     */
    public void setTileOutputDir(File outDir)
    {
        ExtendedValidate.couldBeDirectory(outDir, "Tile output directory");
        tileOutDir = outDir;
    }
    
    /**
     * Sets the size of each generated image tile.
     * 
     * @param size  The width and height in pixels of each new map tile image.
     */
    public void setTileSize(int size)
    {
        ExtendedValidate.isPositive(size, "Tile size");
        tileSize = size;
    }
    
    /**
     * Sets alternate map tile sizes that should be created.
     * 
     * @param altSizes  A set of alternate tile image sizes, measured in
     *                  pixels. These tile sets will be created by rescaling
     *                  the main tile set, so making them larger than the main
     *                  tile set is not advisable.
     */
    public void setAltTileSizes(int[] altSizes)
    {
        Validate.notNull(altSizes, "Alternate tile sizes cannot be null.");
        for (int size : altSizes)
        {
            ExtendedValidate.isPositive(size, "Alt. tile size");
        }
        altTileSizes = altSizes;
    }
    
    /**
     * Sets whether single-image maps will be generated.
     * 
     * @param enabled  Whether each region and map type should be saved as a
     *                 single image file, meant to be viewed alone.
     */
    public void setSingleImageMapsEnabled(boolean enabled)
    {
        imageMapsEnabled = enabled;
    }
    
    /**
     * Sets the directory where single-image maps will be saved.
     * 
     * @param outDir  Map images will be saved within a region-specific
     *                directory within this output directory.
     */
    public void setImageMapOutputDir(File outDir)
    {
        ExtendedValidate.couldBeDirectory(outDir, "Image output directory");
        imageOutDir = outDir;
    }
    
    /**
     * Sets whether backgrounds will be drawn behind single-image maps.
     * 
     * @param shouldDraw  Whether the Minecraft empty map texture should be
     *                    drawn behind map content.
     */
    public void setDrawBackgrounds(boolean shouldDraw)
    {
        drawBackgrounds = shouldDraw;
    }
    
    /**
     * Sets the bounds in Minecraft chunks of the area drawn within
     * single-image maps.
     * 
     * @param xMin    The minimum x-coordinate to include on the map.
     * 
     * @param zMin    The minimum z-coordinate to include on the map.
     * 
     * @param width   The width in chunks of the entire mapped area.
     * 
     * @param height  The height in chunks of the entire mapped area.
     */
    public void setImageMapBounds(int xMin, int zMin, int width, int height)
    {
        ExtendedValidate.isPositive(width, "Map width");
        ExtendedValidate.isPositive(height, "Map height");
        this.xMin = xMin;
        this.zMin = zMin;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Sets the width and height in pixels used when drawing individual
     * Minecraft map chunks.
     * 
     * @param pixelsPerChunk  The chunk size in pixels. 
     */
    public void setPixelsPerChunk(int pixelsPerChunk)
    {
        ExtendedValidate.isPositive(pixelsPerChunk, "Pixels per chunk");
        this.pixelsPerChunk = pixelsPerChunk;
    }
    
    /**
     * Adds a new Minecraft region directory that should be mapped.
     * 
     * @param regionName              A name to apply to all created maps for
     *                                the region.
     * 
     * @param regionDirectory         The path to a directory containing
     *                                Minecraft anvil region files.
     * 
     * @throws FileNotFoundException  If the region directory does not exist.
     */
    public void addRegion(String regionName, File regionDirectory)
            throws FileNotFoundException
    {
        ExtendedValidate.notNullOrEmpty(regionName, "Region name");
        ExtendedValidate.isDirectory(regionDirectory, "Region directory");
        Region region = new Region(regionName, regionDirectory);
        regionsToMap.add(region);
    }
    
    // Immutably store a region directory with its region name:
    private class Region
    {
        protected Region(String name, File directory)
        {
            this.name = name;
            this.directory = directory;
        }  
        protected final String name;
        protected final File directory;
    }
    
    /**
     * Configures the MapCreator to use every MapType when generating maps.
     */
    public void enableAllMapTypes()
    {
        enabledMapTypes.addAll(Arrays.asList(MapType.values()));
    }
    
    /**
     * Sets whether the MapCreator will generate a specific type of map.
     * 
     * @param type       The MapType to enable or disable.
     * 
     * @param isEnabled  Whether that MapType should be generated. 
     */
    public void setMapTypeEnabled(MapType type, boolean isEnabled)
    {
        Validate.notNull(type, "Map type cannot be null.");
        if (isEnabled)
        {
            enabledMapTypes.add(type);
        }
        else
        {
            enabledMapTypes.remove(type);
        }
    }
    
    /**
     * Gets map keys from all generated maps.
     * 
     * @return  A JSON array of map key data, or null if maps have not been
     *          generated yet.
     */
    public JsonArray getMapKeys()
    {
        return keyBuilder.build();
    }
    
    /**
     * Gets the list of all map file paths created by the MapCreator.
     * 
     * @return  A JSON object holding lists of tile paths, indexed by region
     *          and map type.
     */
    public JsonObject getMapTileList()
    {
        return tileListBuilder.build();
    }
    
    /**
     * Apply the current settings to create tile maps for a region directory.
     * 
     * @param mapRegion  A Minecraft region directory path and its associated
     *                   region name.
     * 
     * @param outDir     A region-specific output directory where tiles will be
     *                   saved.
     */
    private void createTileMaps(Region mapRegion, File outDir)
    {
        Validate.notNull(mapRegion, "Mapped region cannot be null.");
        ExtendedValidate.couldBeDirectory(outDir, "Tile output directory");
        ArrayList<File> regionFiles = new ArrayList<>(Arrays.asList(
                mapRegion.directory.listFiles()));
        mappers = new MapCollector(outDir, mapRegion.name, tileSize,
                altTileSizes, pixelsPerChunk, enabledMapTypes);
        // If more than one tile fits in a region, sort regions by tile:
        if (tileSize > 32)
        {
            class RegionSort implements Comparator<File>
            {
                // Get the coordinates of the tile that hold a region file's
                // upper left point.
                private Point getTilePt(File regionFile)
                {
                    Point chunkPt = MCAFile.getChunkCoords(regionFile);
                    return TileMap.getTilePoint(chunkPt.x, chunkPt.y,
                            tileSize);
                }
                
                @Override
                public int compare(File first, File second)
                {
                    Point firstTile = getTilePt(first);
                    Point secondTile = getTilePt(second);
                    if (firstTile.y == secondTile.y)
                    {
                        return firstTile.x - secondTile.x;
                    }
                    return firstTile.y - secondTile.y;
                }
            }
            Collections.sort(regionFiles, new RegionSort());
        }
        final int chunksMapped = mapRegion(mapRegion.name, regionFiles);
        if (chunksMapped > 0)
        {
            int mapKM = MapUnit.convert(chunksMapped, MapUnit.CHUNK,
                    MapUnit.BLOCK) / 1000000;
            System.out.println("Mapped " + chunksMapped
                    + " chunks, an area of " + mapKM + " km^2.");        
        }
    }
            
    /**
     * Applies the current settings to create single-image region maps.
     * 
     * @param mapRegion  A Minecraft region directory path and its associated
     *                   region name.
     * 
     * @param outDir     A region-specific directory where map images will be
     *                   saved.
     */
    private void createSingleImageMaps(Region mapRegion, File outDir)
    {
        Validate.notNull(mapRegion, "Mapped region cannot be null.");
        ExtendedValidate.couldBeDirectory(outDir, "Image output directory");
        ArrayList<File> regionFiles = new ArrayList<>();
        int regionChunks = MapUnit.convert(1, MapUnit.REGION, MapUnit.CHUNK);
        // Valid region files within the given bounds will all be named
        // r.X.Z.mca, where X and Z are valid region coordinates within the
        // given range. Check each of these names to see if a corresponding
        // file exists.
        int regionXMin = (int) Math.floor((double) xMin 
                / (double) regionChunks);
        int regionXMax = (int) Math.ceil((double) xMin / (double) regionChunks)
                + (int) Math.ceil((double) width / (double) regionChunks);
        int regionZMin = (int) Math.floor((double) zMin
                / (double) regionChunks);
        int regionZMax = (int) Math.ceil((double) zMin / (double) regionChunks)
                + (int) Math.ceil((double) height / (double) regionChunks);
        for(int x = regionXMin; x < regionXMax; x++)
        {
            for (int z = regionZMin; z < regionZMax; z++)
            {
                String filename = "r." + String.valueOf(x) + "."
                        + String.valueOf(z) + ".mca";
                File regionFile = new File(mapRegion.directory, filename);
                if (regionFile.exists())
                {
                    regionFiles.add(regionFile);
                }
            }
        }
        int numRegionFiles = regionFiles.size();    
        if (numRegionFiles == 0)
        {
            System.out.println("Region was empty, no maps were created.");
            return;          
        }
        mappers = new MapCollector(outDir, mapRegion.name, xMin, zMin, width,
                height, pixelsPerChunk, enabledMapTypes);
        final int chunksMapped = mapRegion(mapRegion.name, regionFiles);
        if (chunksMapped > 0)
        {
            int numChunks = width * height;
            double explorePercent = (double) chunksMapped * 100 / numChunks;
            System.out.println("Mapped " + chunksMapped
                    + " chunks out of " + numChunks + ", map is "
                    + explorePercent + "% explored.");
        }
        else
        {
            System.out.println("Region was empty, no maps were created.");
        }
    }
      
    /**
     * Finishes the process of mapping a set of Minecraft region files,
     * processing all regions within multiple threads.
     * 
     * @param regionName   The name of the mapped region.
     * 
     * @param regionFiles  The set of Minecraft region files to map.
     * 
     * @return             The total number of region chunks mapped.
     */
    private int mapRegion(String regionName, ArrayList<File> regionFiles)
    {
        ExtendedValidate.notNullOrEmpty(regionName, "Region name");
        Validate.notNull(regionFiles, "Region files cannot be null.");
        Validate.notNull(mappers, "MapCollector cannot be null.");
        int numRegionFiles = regionFiles.size();
        // Provide threadsafe tracking of processed region and chunk counts:
        ProgressThread progressThread = new ProgressThread(numRegionFiles);
        progressThread.start();
        // Handle all map updates within a single thread:
        MapperThread mapperThread = new MapperThread(mappers);
        mapperThread.start();
        // Divide region file updates between multiple threads:
        int numReaderThreads;
        if (MULTI_REGION_THREADS)
        {
            numReaderThreads = Math.max(1,
                    Runtime.getRuntime().availableProcessors());
        }
        else
        {
            numReaderThreads = 1;
        }
        if (numReaderThreads > numRegionFiles)
        {
            numReaderThreads = numRegionFiles;
        }
        System.out.println("Processing " + numRegionFiles
                + " region files with " + numReaderThreads + " threads.");
        int filesPerThread = numRegionFiles / numReaderThreads;
        ArrayList<ReaderThread> threadList = new ArrayList<>();
        for (int i = 0; i < numReaderThreads; i++)
        {
            int regionStart = i * filesPerThread;
            int regionEnd = (i == (numReaderThreads - 1))
                    ? numRegionFiles : (regionStart + filesPerThread);
            threadList.add(new ReaderThread(new ArrayList<>(
                    regionFiles.subList(regionStart, regionEnd)),
                    mapperThread,
                    progressThread));
            threadList.get(i).start();
        }
        threadList.forEach((thread) ->
        {
            while (thread.isAlive())
            {
                try
                {
                    thread.join();
                }
                catch (InterruptedException e)
                {
                    // Just try again.
                }
            }
        });
        mapperThread.requestStop();
        while (mapperThread.isAlive())
        {
            try
            {
                mapperThread.join();
            }
            catch (InterruptedException e) { }
        }
        progressThread.requestStop();
        while (progressThread.isAlive())
        {
            try
            {
                progressThread.join();
            }
            catch (InterruptedException e) { }
        }
        mappers.saveMapFile();
        JsonArray regionKey = mappers.getMapKeys();
        for (int i = 0; i < regionKey.size(); i++)
        {
            keyBuilder.add(regionKey.get(i));
        }
        tileListBuilder.add(regionName, mappers.getMapFiles());
        return progressThread.getChunkCount();
    }
    
    // Image map options:
    private boolean imageMapsEnabled = false;
    private File imageOutDir = null;
    private boolean drawBackgrounds = false;
    private int xMin = 0;
    private int zMin = 0;
    private int width = 0;
    private int height = 0;
    
    // Tile options
    private boolean tilesEnabled = false;
    private File tileOutDir = null;
    private int tileSize = 0;
    private int[] altTileSizes = null;
    
    private MapCollector mappers = null;
    private final JsonArrayBuilder keyBuilder;
    private final JsonObjectBuilder tileListBuilder;
    private final ArrayList<Region> regionsToMap;
    private Set<MapType> enabledMapTypes;
    private int pixelsPerChunk = 0;  
}
