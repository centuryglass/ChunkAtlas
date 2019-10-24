/**
 * @file JarResource.java
 * 
 * Loads resource file data from this application's .jar file.
 */
package com.centuryglass.chunk_atlas.util;


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
     * @throws IOException  If the resource cannot be read. 
     */
    public static JsonStructure readJsonResource(String resourcePath)
            throws IOException
    {
        ExtendedValidate.notNullOrEmpty(resourcePath, "JSON resource path");
        // Attempt to load default options:
        try (InputStream optionStream = getResourceStream(resourcePath))
        {
            if (optionStream == null)
            {
                throw new IOException("No JSON jar file resource found at \""
                        + resourcePath + "\".");
            }
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
        return null;
    }
    
    /**
     * Gets a jar resource as an input stream.
     * 
     * @param resourcePath  The path to a resource stored in this application's
     *                      jar file.
     * 
     * @return              An open InputStream for the requested resource, or
     *                      null if the resource isn't found.
     */
    public static InputStream getResourceStream(String resourcePath)
    {
        // Resource paths should all start with a leading '/' character, add it
        // if it's not already there.
        if (! (resourcePath.charAt(0) == '/'))
        {
            resourcePath = "/" + resourcePath;
        }
        return JarResource.class.getResourceAsStream(resourcePath);
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
        if (! (imagePath.charAt(0) == '/'))
        {
            imagePath = "/" + imagePath;
        }
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
     * @throws IOException  If unable to read from the resource or write to the
     *                      output file.
     */
    public static void copyResourceToFile(String resourcePath, File outFile)
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
                throw new IOException("Unable to create file at \""
                        + outFile.toString() + "\" to copy resource \""
                        + resourcePath + "\".");
            }
            resourceStream = getResourceStream(resourcePath);
            if (resourceStream == null)
            {
                throw new IOException("Unable to copy resource \""
                        + resourcePath
                        + "\", to file: resource not found");
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
    }
}
