/**
 * @file BiomeTextures.java
 * 
 * Loads and shares textures for every Minecraft biome.
 */

package images;

import com.centuryglass.mcmap.worldinfo.Biome;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class BiomeTextures
{
    // Resource directory where all texture images are found:
    private static final String TEXTURE_DIR = "/biomeTile/";
    
    /**
     * Loads all biome textures on construction.
     */
    public BiomeTextures()
    {
        textures = new HashMap();
        for (Biome biome : Biome.values())
        {
            final String texturePath = TEXTURE_DIR + biome.toString() + ".png";
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
        if (biome == null)
        {
            System.err.println("Null biome!");
            return null;
        }
        Texture texture = textures.get(biome);
        if (texture == null)
        {
            System.err.println("BiomeTextures.getPixel: No texture found for "
                    + biome.toString());
            return null;
        }
        return texture.getPixel(x, y, scale);
    }
    
    private final Map<Biome, Texture> textures;
}
