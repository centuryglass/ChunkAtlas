/**
 * @file  StructureMapper.java
 *
 *  Creates the generated Minecraft structure map.
 */
package com.centuryglass.chunk_atlas.mapping.maptype;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.mapping.KeyItem;
import com.centuryglass.chunk_atlas.mapping.WorldMap;
import com.centuryglass.chunk_atlas.serverplugin.StructureScanner;
import com.centuryglass.chunk_atlas.util.MapUnit;
import com.centuryglass.chunk_atlas.worldinfo.ChunkData;
import com.centuryglass.chunk_atlas.worldinfo.Structure;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.StructureType;
import org.bukkit.World;

/**
 *  StructureMapper creates maps showing where different generated structures
 * can be found within the Minecraft world.  Individual structure colors are
 * defined in the worldinfo.Structure Enum, and documented in the project's
 * mapKey.png file.
 */
public class StructureMapper extends Mapper
{
    private static final String CLASSNAME = StructureMapper.class.getName();
    
    // Radius used when using the server plugin interface to scan for
    // structures.
    private static final int SCAN_RADIUS = 1;
    
    /**
     * Sets the mapper's base output directory and mapped region name on
     * construction.
     *
     * @param imageDir    The directory where the map image will be saved.
     * 
     * @param regionName  The name of the region this Mapper is mapping.
     * 
     * @param region      An optional bukkit World object, used to load extra
     *                    map data if non-null.
     */
    public StructureMapper(File imageDir, String regionName, World region)
    {
        super(imageDir, regionName, region);
        structureRefs = new HashMap<>();
        encounteredStructures = new TreeSet<>();
    }
    
    /**
     * Gets the type of map a mapper creates.
     *
     * @return  The Mapper's MapType.
     */
    @Override
    public MapType getMapType()
    {
        return MapType.STRUCTURE;
    }
    
                     
    /**
     * Gets all items in this mapper's map key.
     * 
     * @return  Key items for each structure that appears on this map.
     */
    @Override
    public Set<KeyItem> getMapKey()
    {
        Set<KeyItem> key = new LinkedHashSet<>();
        for (Structure structure : encounteredStructures)
        {
            if (structure.equals(Structure.UNKNOWN))
            {
                continue;
            }
            key.add(new KeyItem(structure.toString(), getMapType(),
                    getRegionName(),
                    structure.getColor()));
        }
        return key;
    }

    /**
     *  Provides a color for any valid chunk based on the structure or
     *  structures it contains.
     *
     * @param chunk  The chunk that may be drawn.
     *
     * @return       The chunk's structure color.
     */
    @Override
    protected Color getChunkColor(ChunkData chunk)
    {
        Validate.notNull(chunk, "Chunk cannot be null.");
        if (chunk.getErrorType() != ChunkData.ErrorFlag.NONE)
        {
            return null;
        }
        final Color emptyChunkColor = new Color(0);
        Color color = emptyChunkColor;
        //if (getRegion() == null) 
        //{ 
            Map<Point, Structure> chunkStructureRefs = chunk.getStructureRefs();
            chunkStructureRefs.entrySet().forEach((entry) ->
            {
                if (structureRefs.containsKey(entry.getKey()))
                {
                    if (structureRefs.get(entry.getKey()).getPriority()
                            >= entry.getValue().getPriority())
                    {
                        return;
                    }
                }
                encounteredStructures.add(entry.getValue());
                structureRefs.put(entry.getKey(), entry.getValue());
            });
        //}
        /*
        // Structure scanning through the bukkit/spigot interface is painfully
        // slow in larger servers, disabling it for now.
        else
        {
            Point chunkPt = chunk.getPos();
            if (! getRegion().isChunkGenerated(chunkPt.x, chunkPt.y))
            {
                return color;
            }
            StructureScanner scanner = StructureScanner.getWorldScanner(
                    getRegion());
            Point blockCoords = MapUnit.convertPoint(chunkPt, MapUnit.CHUNK,
                    MapUnit.BLOCK);
            Location chunkLocation = new Location(getRegion(), blockCoords.x,
                    0, blockCoords.y);
            scanner.scan(chunkLocation, SCAN_RADIUS);
        }
        */
        return color;
    }
    
    /**
     * Adds new structure references to the map before exporting it.
     *
     * @param map  The mapper's MapImage.
     */
    @Override
    protected void finalProcessing(WorldMap map)
    {
        final String FN_NAME = "finalProcessing";
        if (getRegion() != null)
        {
            // Copy scanned structure areas into structure refs:
            StructureScanner scanner = StructureScanner.getWorldScanner(
                    getRegion());
            Map<Point, StructureType> pts = scanner.getStructurePoints();
            LogConfig.getLogger().logp(Level.CONFIG, CLASSNAME, FN_NAME,
                    "Adding {0} structures to the map.", pts.size());
            for (Map.Entry<Point, StructureType> entry : pts.entrySet())
            {
                Structure type = Structure.fromStructureType(entry.getValue());
                Point chunkPt = MapUnit.convertPoint(entry.getKey(),
                        MapUnit.BLOCK, MapUnit.CHUNK);
                structureRefs.put(chunkPt, type);
            }
        }
        final double maxDistance = Math.sqrt(18);
        for (Map.Entry<Point, Structure> entry : structureRefs.entrySet())
        {
            Color structColor = entry.getValue().getColor();
            int x = entry.getKey().x;
            int z = entry.getKey().y;
            // Single structure chunks are hard to spot on a big world map.
            // Expand structure points to 5x5 chunk spaces, fading with
            // distance from the main point.
            for (int i = 0; i < 25; i++)
            {
                int xI = x + ((i % 5) - 2);
                int zI = z + ((i / 5) - 2);
                Color pointColor;
                if (xI == x && zI == z)
                {
                    pointColor = structColor;
                }
                else
                {
                    int dX = x - xI;
                    int dZ = z - zI;
                    double distance = Math.sqrt(dX * dX + dZ * dZ);
                    double mult = 1 - (distance / maxDistance);
                    Color currentColor = map.getChunkColor(xI, zI);
                    if (currentColor == null)
                    {
                        currentColor = new Color(0);
                    }
                    pointColor = new Color(
                            (int) (structColor.getRed() * mult
                                    + currentColor.getRed() * (1 - mult)),
                            (int) (structColor.getGreen() * mult
                                    + currentColor.getGreen() * (1 - mult)),
                            (int) (structColor.getBlue() * mult
                                    + currentColor.getBlue() * (1 -mult)),
                            255);
                }
                map.setChunkColor(xI, zI, pointColor);  
            }
        }
        super.finalProcessing(map);
    }
    
    private final Map<Point, Structure> structureRefs;
    private final Set<Structure> encounteredStructures;
}
