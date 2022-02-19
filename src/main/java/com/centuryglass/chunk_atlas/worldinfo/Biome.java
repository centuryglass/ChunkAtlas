/**
 * @file  Biome.java
 *
 *  Defines the code values used to represent all Minecraft world biomes.
 */
package com.centuryglass.chunk_atlas.worldinfo;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.util.JarResource;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

public class Biome implements Comparable
{
    private static final String BIOME_RESOURCE = "/biomes.json";
    
    // Immutable public properties:
    public final int biomeCode;
    public final Color color;
    public final String name;
    public final String displayName;
    public final String imageResource;
    
    /**
     * Adds a set of biomes to the list of all valid biomes, avoiding name conflicts and replacing any biome with a
     * conflicting  id.
     * 
     * @param biomeDefs   A JSON object defining one or more biome types.  Use biomes.json in the ChunkAtlas
     *                                   resource directory to determine expected JSON structure.
     */
    public static void loadBiomes(JsonStructure biomeDefs)
    {
        final String colorKey = "color";
        final String displayNameKey = "formattedName";
        final String idCodeKey = "id";
        final String resourceKey = "texture";
        
        final JsonObject biomeMap = biomeDefs.asJsonObject();
        for(Entry<String, JsonValue> entry: biomeMap.entrySet())
        {
            String name = entry.getKey();
            JsonObject biomeDef = entry.getValue().asJsonObject();
            
            int id;
            if (biomeDef.containsKey(idCodeKey))
            {
                id = Integer.parseInt(biomeDef.getString(idCodeKey));
            }
            else
            {
                // Later versions of minecraft no longer use biome ids, assign an unused negative number to prevent
                // conflicts.
                id = -1;
                while (codeBiomes.containsKey(id)) {
                    id--;
                }
                
            }
            
            if (codeBiomes.containsKey(id)) 
            {
                Biome conflictingBiome = codeBiomes.get(id);
                LogConfig.getLogger().log(Level.WARNING, "Biome '{0}' already exists with ID {1}, this will be replaced by new "
                        + "biome '{2}'.", new Object[]{conflictingBiome.name, String.valueOf(id), name});
                codeBiomes.remove(id);
                closestMatches.remove(conflictingBiome.name);
            }
            
            while (closestMatches.containsKey(name))
            {
                Biome closest = closestMatches.get(name);
                if (closest.name.equals(name))
                {
                    String newName;
                    if (name.matches(".*_\\d+$"))
                    {
                        Pattern nameSections = Pattern.compile("^(.*_)(\\d+)$");
                        Matcher nameMatch = nameSections.matcher(name);
                        assert(nameMatch.find()); // Assertion will always pass unless the regex above are incorrect.
                        newName  = nameMatch.group(0) + String.valueOf(Integer.parseInt(nameMatch.group(1)) + 1);
                    }
                    else
                    {
                        newName = name + "_2";
                    }
                    LogConfig.getLogger().log(Level.WARNING,
                            "A biome named ''{0}'' already exists with id={1}, new biome with id={2} will be renamed to ''{3}''",
                            new Object[]{name, String.valueOf(closest.biomeCode), String.valueOf(id), newName});
                    name = newName;
                }
                else 
                {
                    closestMatches.remove(name);
                }
            }
            
            Biome addedBiome = new Biome(id, biomeDef.getString(colorKey), name, biomeDef.getString(displayNameKey),
                    biomeDef.getString(resourceKey));
            codeBiomes.put(id, addedBiome);
            closestMatches.put(name, addedBiome);          
        }
    }
    
    /**
     * Get the list of all available biomes.
     * 
     * @return  A copy of the list of biomes.
     */
    public static List<Biome> values()
    {
        return codeBiomes.values().stream().collect(Collectors.toList());
    }

    /**
     *  Gets a biome from its NBT code value.
     * 
     * @param biomeCode  A biome NBT code number.
     * 
     * @return           The associated biome, or null if the code isn't valid.
     */
    public static Biome fromCode(int biomeCode)
    {
        return codeBiomes.get(biomeCode);
    }
    
   /**
    *  Gets the biome that most closely matches the given name.
    * 
    * @param biomeName  The name of a Minecraft biome.
    * 
    * @return           The biome with the longest name that is a sub-string of
    *                   biomeName, or null if no match could be found.
    */
    public static Biome getClosestMatch(String biomeName)
    {
        if (closestMatches.containsKey(biomeName)) {
            return closestMatches.get(biomeName);
        }
        Biome closest = null;
        for (Biome biome : codeBiomes.values())
        {
            if (biome.name.equals(biomeName)) {
                closest = biome;
                break;
            }
            else if (biomeName.contains(biome.name) && (closest == null 
                    || biome.name.length() > closest.name.length()))
            {
                closest = biome;
            }
        }
        closestMatches.put(biomeName, closest);
        return closest;
    }
    
    // Store biome name mappings, used when trying to find acceptable matches
    // for biomes that aren't in the list of expected values:
    private static final Map<String, Biome> closestMatches;
    // Save (integer code, Biome) pairs for quick lookup:
    private static final Map<Integer, Biome> codeBiomes;
    
    static
    {
        closestMatches = new HashMap<>();
        codeBiomes = new HashMap<>();
        try {
            JsonStructure biomeResource = JarResource.readJsonResource(BIOME_RESOURCE);
            loadBiomes(biomeResource);
        } 
        catch(IOException e)
        {
            LogConfig.getLogger().log(Level.SEVERE, "Unable to load default biome definitions:{0}", e.getLocalizedMessage());
        }
    }
    
    /**
     * Get the total number of available biomes.
     * 
     * @return  The number of unique biomes.
     */
    public static int biomeCount()
    {
        return codeBiomes.size();
    }
    
    /**
     * Gets a biome display name.
     * 
     * @return  The formatted display name. 
     */
    @Override
    public String toString()
    {
        return displayName;
    }
    
    private Biome(int code, String color, String name, String displayName, String imagePath)
    {
        biomeCode = code;
        if (!color.startsWith("#"))
        {
            color = "#" + color;
        }
        this.color = Color.decode(color.toUpperCase());
        this.name = name;
        this.displayName = displayName;
        this.imageResource = imagePath;
    }

    @Override
    public int compareTo(Object t) {
        return this.biomeCode - ((Biome) t).biomeCode;
    }

}
