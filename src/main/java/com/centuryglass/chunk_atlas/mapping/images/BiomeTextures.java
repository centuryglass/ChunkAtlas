/**
 * @file BiomeTextures.java
 * 
 * Loads and shares textures for every Minecraft biome.
 */

package com.centuryglass.chunk_atlas.mapping.images;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import com.centuryglass.chunk_atlas.worldinfo.Biome;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;

public class BiomeTextures
{
    private static final String CLASSNAME = BiomeTextures.class.getName();
    
    // Texture paths starting with this string will be loaded  from JAR resources:
    private static final String TEXTURE_RESOURCE_DIR = "resources/biomeTile/";
    
    /**
     * Loads all biome textures on construction.
     */
    public BiomeTextures()
    {
        textures = new HashMap<>();
        for (Biome biome : Biome.values())
        {
            String texturePath = biome.imageResource;
            if (texturePath.startsWith(TEXTURE_RESOURCE_DIR))
            {
                texturePath = texturePath.substring(TEXTURE_RESOURCE_DIR.length());
            }
            textures.put(biome, new Texture(texturePath));
        }
    }
    
    /**
     * Given a biome type and an (x, y) coordinate, get the appropriate color
     * from that biome's texture to place at that coordinate.
     * 
     * @param biome   The biome used to select the image texture.
     * 
     * @param x       An x-coordinate within some image plane.
     * 
     * @param y       A y-coordinate within some image plane.
     * 
     * @param scale   A scale multiplier to apply to the texture size.
     * 
     * @return        The appropriate texture color for the given coordinates.
     */
    public Color getPixel(Biome biome, int x, int y, int scale)
    {
        final String FN_NAME = "getPixel";
        Validate.notNull(biome, "Biome cannot be null.");
        ExtendedValidate.isPositive(scale, "Texture scale");
        Texture texture = textures.get(biome);
        if (texture == null)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "No texture found for biome {0}.", biome);
            return null;
        }
        return texture.getPixel(x, y, scale);
    }
    
    private final Map<Biome, Texture> textures;
}
