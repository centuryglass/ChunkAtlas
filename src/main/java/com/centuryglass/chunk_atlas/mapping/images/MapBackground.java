/**
 * @file MapBackground.java
 * 
 * Helps with loading and drawing a Minecraft map item background used behind
 * map images.
 */
package com.centuryglass.chunk_atlas.mapping.images;

import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import com.centuryglass.chunk_atlas.util.JarResource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Loads and draws the Minecraft map background image that may be used behind
 * map images.
 */
public class MapBackground 
{
    // Image width / BORDER_DIVISOR = the width of each map border.
    private static final int BORDER_DIVISOR = 19;
    // Map background image resource.
    private static final String BACKGROUND_PATH = "/emptyMap.png";
    
    /**
     * Get the width of the border area around map content.
     * 
     * @param contentWidth  The width in pixels of all map content being drawn
     *                      within the map image.
     * 
     * @return              The size in pixels that the padding area around all
     *                      map content will have.
     */
    public static int getBorderWidth(int contentWidth)
    {
        ExtendedValidate.isPositive(contentWidth, "Map content width");
        return contentWidth / BORDER_DIVISOR;
    }
    
    /**
     * Gets the image data source used to draw the map background.
     * 
     * @return  The background image data, or null if loading the background
     *          image failed.
     */
    public static BufferedImage getBackgroundImage()
    {
        try
        {
            return JarResource.readImageResource(BACKGROUND_PATH);
        }
        catch (IOException e)
        {
            System.err.println("Opening background image " + BACKGROUND_PATH
                    + " failed.");
        }
        return null;
    }  
}
