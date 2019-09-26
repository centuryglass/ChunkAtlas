/**
 * @file Texture.java
 * 
 * Assists in drawing a tiled image texture within an area.
 */

package com.centuryglass.mcmap.mapping.images;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Texture 
{
    /**
     * Loads a texture on construction.
     * 
     * @param texturePath  The path to a texture image resource embedded in the
     *                     jar.
     */
    public Texture(String texturePath)
    {
        final URL textureURL = Texture.class.getResource(texturePath);
        if (textureURL == null)
        {
            System.err.println("Texture: invalid path " + texturePath);
            System.exit(1);
        }
        final BufferedImage textureImage;
        try
        {
            textureImage = ImageIO.read(textureURL);
        }
        catch (IOException e)
        {
            System.err.println("Texture: Opening texture image "
                    + texturePath + " failed.");
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
        if (textureData == null) { return null; }
        return textureData[texturePos(x, width, scale)][texturePos(y, height,
                scale)];
    }
    
    private int texturePos(int coordinate, int max, int scale)
    {
        int pos = (coordinate / scale) % max;
        if (pos < 0) { pos += max; }
        return pos;
    }
    
    final int width;
    final int height;
    final Color[][] textureData;
}