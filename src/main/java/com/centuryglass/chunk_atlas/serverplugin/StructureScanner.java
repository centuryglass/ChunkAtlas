/**
 * @file StructureScanner.java
 * 
 * Scans a Minecraft world for structures.
 */
package com.centuryglass.chunk_atlas.serverplugin;

import com.centuryglass.chunk_atlas.util.Circle;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.StructureType;
import org.bukkit.World;

/**
 * StructureScanner scans org.bukkit.World objects for structure data around
 * specific points, tracking previous scan locations to avoid excessive
 * scanning. Each valid World object may have only one scanner, accessed through
 * StructureScanner.getWorldScanner.
 */
public class StructureScanner
{
    private static final Map<World, StructureScanner> scanners;
    static
    {
        scanners = new HashMap<>();
    }
    
    /**
     * Gets the scanner for a Minecraft World, creating it if necessary.
     * 
     * @param world  A valid, non-null World object.
     * 
     * @return       A scanner associated with the chosen World.
     */
    public static StructureScanner getWorldScanner(World world)
    {
        Validate.notNull(world,
                "StructureScanner.getWorldScanner: cannot scan null World.");
        if (! scanners.containsKey(world))
        {
            scanners.put(world, new StructureScanner(world));
        }
        return scanners.get(world);
    }
    
    /**
     * Creates a scanner for a specific World.
     * 
     * @param world  A valid, non-null World object. 
     */
    private StructureScanner(World world)
    {
        this.world = world;
        scannedStructureAreas = new HashMap<>();
        structures = new HashMap<>();
        StructureType.getStructureTypes().forEach(
                (String typeName, StructureType type) -> 
        {
            scannedStructureAreas.put(type, new ScannedArea());
        });
    }
    
    /**
     * Scans a specific location in the world for all structure types, skipping
     * area/structure combinations where the closest structure has already been
     * located.
     * 
     * @param scanLocation  A coordinate object within this scanner's world.
     * 
     * @param radius        The radius in chunks to search for structures.
     */
    public void scan(Location scanLocation, int radius)
    {
        Point scanPt = new Point(scanLocation.getBlockX(),
                scanLocation.getBlockZ());
        StructureType.getStructureTypes().forEach(
                (String typeName, StructureType type) -> 
        {
            Circle scanArea = new Circle(scanPt, radius);
            if (scannedStructureAreas.get(type).contains(scanArea))
            {
                return;
            }
            Location closest = world.locateNearestStructure(scanLocation, type,
                    radius, false);
            if (closest != null)
            {
                Point closestPt = new Point(closest.getBlockX(),
                        closest.getBlockZ());
                structures.put(closestPt, type);
                Circle scannedArea = new Circle(scanPt,
                        scanPt.distance(closestPt));
                scannedStructureAreas.get(type).addArea(scannedArea);
            }
            else
            {
                scannedStructureAreas.get(type).addArea(scanArea);
            }  
        });
    }
    
    /**
     * Gets all structure coordinates located by the scanner.
     * 
     * @return  Block coordinates for each structure, each mapped to the type
     *          of structure at that location.
     */
    public Map<Point, StructureType> getStructurePoints()
    {
        return structures;
    }
    
    // Track scanned map areas to reduce redundant scanning.
    private static class ScannedArea
    {
        public ScannedArea()
        {
            areas = new ArrayList<>();
        }
        
        /**
         * Check if an area has already been completely scanned.
         * 
         * @param scanArea  A circular area to potentially scan for structures.
         * 
         * @return          Whether the entire potential scan area has already
         *                  been scanned.
         */
        public boolean contains(Circle scanArea)
        {
            Validate.notNull(scanArea, "StructureScanner.ScannedArea.contains:"
                    + " scanArea cannot be null.");
            for (Circle area : areas)
            {
                if (area.contains(scanArea))
                {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Add a circular area to the list of scanned areas.
         * 
         * @param newArea  The new scanned area to add. 
         */
        public void addArea(Circle newArea)
        {
            areas.add(newArea);
        }
        
        private final ArrayList<Circle> areas;
    }
    
    private final World world;
    private final Map<StructureType, ScannedArea> scannedStructureAreas;
    private final Map<Point, StructureType> structures;
}
