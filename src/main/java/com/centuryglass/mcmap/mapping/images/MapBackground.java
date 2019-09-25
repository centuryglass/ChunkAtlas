/**
 * @file MapBackground.java
 * 
 * Helps with loading and drawing a Minecraft map item background used behind
 * map images.
 */
package com.centuryglass.mcmap.images;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class MapBackground 
{
    // Image width / BORDER_DIVISOR = the width of each map border.
    private static final int BORDER_DIVISOR = 19;
    // Map background image resource.
    private static final String BACKGROUND_PATH = "/emptyMap.png";
    
    
    public static int getBorderWidth(int contentWidth)
    {
        return contentWidth / BORDER_DIVISOR;
    }
    
    public static BufferedImage getBackgroundImage()
    {
        URL imageURL = MapBackground.class.getResource(BACKGROUND_PATH);
        BufferedImage backgroundImage;
        try
        {
            backgroundImage = ImageIO.read(imageURL);
        }
        catch (IOException e)
        {
            System.err.println("Opening background image "
                    + BACKGROUND_PATH + " failed.");
            return null;
        }
        return backgroundImage;
    }  
}
