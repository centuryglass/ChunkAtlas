/**
 * @file Texture.java
 * 
 * Assists in drawing a tiled image texture within an area.
 */

package com.centuryglass.chunk_atlas.mapping.images;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import com.centuryglass.chunk_atlas.util.JarResource;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;

public class Texture 
{
    private static final String CLASSNAME = Texture.class.getName();
    
    /**
     * Loads a texture on construction.
     * 
     * @param texturePath  The path to a texture image resource embedded in the
     *                     jar.
     */
    public Texture(String texturePath)
    {
        final String FN_NAME = "Texture";
        ExtendedValidate.notNullOrEmpty(texturePath, "Texture path");
        final BufferedImage textureImage;
        try
        {
            textureImage = JarResource.readImageResource(texturePath);
        }
        catch (IOException e)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Opening texture image '{0}' failed.", texturePath);
            textureData = null;
            width = 0;
            height = 0;
            return;
        }
        width = textureImage.getWidth();
        height = textureImage.getHeight();
        textureData = new Color[width][height];
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                textureData[x][y] = new Color(textureImage.getRGB(x, y));
            }
        }
    }
    
    /**
     * Given an arbitrary coordinate in an area where this texture is drawn,
     * get the appropriate texture pixel color for that coordinate.
     * 
     * @param x       An x-coordinate within some image plane.
     * 
     * @param y       A y-coordinate within some image plane.
     * 
     * @param scale   A scale multiplier to apply to the texture size.
     *
     * @return        The appropriate texture color for the given coordinates.
     */
    public Color getPixel(int x, int y, int scale)
    {
        ExtendedValidate.isPositive(scale, "Texture scale");
        if (textureData == null) { return null; }
        return textureData[texturePos(x, width, scale)][texturePos(y, height,
                scale)];
    }
    
    /**
     * Find the texture coordinate value mapped to an image coordinate.
     * 
     * @param coordinate  The x or y coordinate of an image pixel.
     * 
     * @param size        The size in pixels of the texture along the given
     *                    coordinate's axis.
     * 
     * @param scale       A scale multiplier to apply to the texture size.
     * 
     * @return            The appropriate x or y coordinate value of the
     *                    texture pixel to use.
     */
    private int texturePos(int coordinate, int size, int scale)
    {
        ExtendedValidate.isPositive(size, "Texture size");
        ExtendedValidate.isPositive(scale, "Texture scale");
        int pos = (coordinate / scale) % size;
        if (pos < 0) { pos += size; }
        return pos;
    }
    
    // Texture width in pixels:
    final int width;
    // Texture height in pixels:
    final int height;
    // Indexed color of each pixel in the texture:
    final Color[][] textureData;
}
