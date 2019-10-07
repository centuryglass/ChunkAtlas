/**
 * @file JarResource.java
 * 
 * Loads resource file data from this application's .jar file.
 */
package com.centuryglass.mcmap.util;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonReader;
import javax.json.JsonStructure;

/**
 * Loads resource file data from this application's .jar file.
 */
public class JarResource
{
    // Buffer size when copying resource files:
    private static final int BUF_SIZE = 50000;
    
    /**
     * Reads JSON structure data from a .jar resource.
     * 
     * @param resourcePath  The path to a resource stored in the application's
     *                      .jar file.
     * 
     * @return              Extracted resource JSON data.
     * 
     * @throws IOException  If the resource exists but cannot be read. 
     */
    public static JsonStructure readJsonResource(String resourcePath)
            throws IOException
    {
        ExtendedValidate.notNullOrEmpty(resourcePath, "JSON resource path");
        // Attempt to load default options:
        InputStream optionStream = null;
        try 
        {
            optionStream = JarResource.class.getResourceAsStream(
                    resourcePath); 
            assert (optionStream != null);
            try (JsonReader reader = Json.createReader(optionStream))
            {
                return reader.read();
            }
            catch (JsonException | IllegalStateException ex)
            {
                System.err.println("Failed to load default JSON resource "
                        + resourcePath + ": " + ex.getMessage());
            }
        }
        finally
        {
            if (optionStream != null)
            {
                optionStream.close();
            }
        }
        return null;
    }
    
    /**
     * Reads a buffered image from a .jar resource.
     * 
     * @param imagePath     The path to an image stored in the application's
     *                      .jar file.
     * 
     * @return              All resource image data.
     * 
     * @throws IOException  If the resource cannot be loaded as an image file. 
     */
    public static BufferedImage readImageResource(String imagePath)
            throws IOException
    {
        ExtendedValidate.notNullOrEmpty(imagePath, "Resource image path");
        URL imageURL = JarResource.class.getResource(imagePath);
        if (imageURL == null)
        {
            return null;
        }
        return ImageIO.read(imageURL);
    }
    
    /**
     * Copies a resource embedded in the application's .jar file to an external
     * file.
     * 
     * @param resourcePath  The path to an embedded application resource.
     * 
     * @param outFile       The file where the resource should be copied.
     * 
     * @return              Whether the resource was copied successfully.
     * 
     * @throws IOException  If unable to read from the resource or write to the
     *                      output file.
     */
    public static boolean copyResourceToFile(String resourcePath, File outFile)
            throws IOException
    {
        ExtendedValidate.notNullOrEmpty(resourcePath, "Resource path");
        ExtendedValidate.couldBeFile(outFile, "Resource output file");
        InputStream resourceStream = null;
        FileOutputStream fileStream = null;
        try
        {
            if (! outFile.exists() && ! outFile.createNewFile())
            {
                System.err.println("Unable to create file at \""
                        + outFile.toString() + "\" to copy resource \""
                        + resourcePath + "\".");
                return false;
            }
            resourceStream
                    = JarResource.class.getResourceAsStream(resourcePath);
            if (resourceStream == null)
            {
                System.err.println("Unable to copy resource \"" + resourcePath
                        + "\", to file: resource not found");
                return false;
            }
            fileStream = new FileOutputStream(outFile);
            byte[] buffer = new byte[BUF_SIZE];
            int bytesRead;
            while ((bytesRead = resourceStream.read(buffer)) != -1)
            {
                fileStream.write(buffer, 0, bytesRead);   
            }
        }
        finally
        {
            if (resourceStream != null)
            {
                resourceStream.close();
            }
            if (fileStream != null)
            {
                fileStream.close();
            }
        }
        return true;
    }
}
