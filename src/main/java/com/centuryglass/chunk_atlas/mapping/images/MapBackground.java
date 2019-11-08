/**
 * @file MapBackground.java
 * 
 * Helps with loading and drawing a Minecraft map item background used behind
 * map images.
 */
package com.centuryglass.chunk_atlas.mapping.images;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import com.centuryglass.chunk_atlas.util.JarResource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Loads and draws the Minecraft map background image that may be used behind
 * map images.
 */
public class MapBackground 
{
    private static final String CLASSNAME = MapBackground.class.getName();
    
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
        final String FN_NAME = "getBackgroundImage";
        try
        {
            return JarResource.readImageResource(BACKGROUND_PATH);
        }
        catch (IOException e)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Opening background image '{0}' failed.",
                    BACKGROUND_PATH);
        }
        return null;
    }  
}
