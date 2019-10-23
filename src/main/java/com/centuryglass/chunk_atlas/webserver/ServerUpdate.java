/**
 * @file ServerUpdate.java
 * 
 * Collects and sends map updates to the web server.
 */
package com.centuryglass.chunk_atlas.webserver;

import com.centuryglass.chunk_atlas.MapCreator;
import com.centuryglass.chunk_atlas.util.ExtendedValidate;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import org.apache.commons.lang.Validate;

/**
 * Collects and sends map updates to the web server.
 */
public class ServerUpdate
{
    /**
     * Constructs an update message from map creation data.
     * 
     * @param mapCreator  A MapCreator that has been used to generate maps. 
     */
    public ServerUpdate(MapCreator mapCreator)
    {
        JsonObjectBuilder messageBuilder = Json.createObjectBuilder();
        messageBuilder.add(UpdateKeys.UPDATE_TIME, System.currentTimeMillis());
        messageBuilder.add(UpdateKeys.TILES, mapCreator.getMapTileList());
        messageBuilder.add(UpdateKeys.KEYS, mapCreator.getMapKeys());
        message = messageBuilder.build();
    }
    
    /**
     * Loads a cached update file.
     * 
     * @param cachedUpdate            A JSON file previously exported using the
     *                                exportUpdate method.
     * 
     * @throws FileNotFoundException  If the update file does not exist.
     */
    public ServerUpdate(File cachedUpdate) throws FileNotFoundException
    {
        ExtendedValidate.isFile(cachedUpdate, "Cached update file");
        FileInputStream jsonStream = new FileInputStream(cachedUpdate);
        try (JsonReader reader = Json.createReader(jsonStream))
        {
            message = reader.readObject();
        }
    }
    
    /**
     * Sends all update data to the web server.
     * 
     * @param webConnection  The connection to the web server used to send 
     *                       update data.
     */
    public void sendUpdate(Connection webConnection)
    {
        JsonArray response = null;
        try 
        {
            response = (JsonArray) webConnection.sendJson(message,
                ServerPaths.UPDATE);
        }
        catch (IOException | GeneralSecurityException e)
        {
            System.err.println("Failed to send update to web server: "
                    + e.getMessage());
            return;
        }
        if (response == null)
        {
            System.out.println("No response from web server.");
            return;
        }
        System.out.println("Sending " + response.size()
                + " requested images to the web server.");
        for (int i = 0; i < response.size(); i++)
        {
            String requestedImage = response.getString(i);
            Map<String, String> imageHeaders = new HashMap<>();
            imageHeaders.put("path", requestedImage);
            try
            {
                webConnection.sendPng(requestedImage, imageHeaders,
                        ServerPaths.IMAGE_UPLOAD);
            }
            catch (IOException e)
            {
                System.err.println("Error sending \"" + requestedImage + "\": "
                        + e.toString());
            }
        }   
    }
    
    /**
     * Saves update data to a local JSON file.
     * 
     * @param cacheFile     The file where update data should be saved. 
     * 
     * @throws IOException  If the file could not be created or updated.
     */
    public void exportUpdate(File cacheFile) throws IOException
    {
        ExtendedValidate.couldBeFile(cacheFile, "JSON cache file");
        if (! cacheFile.isFile())
        {
            Validate.isTrue(cacheFile.createNewFile(),
                    "Failed to create update cache file.");
        }
        FileOutputStream outStream = new FileOutputStream(cacheFile);
        try (JsonWriter writer = Json.createWriter(outStream))
        {
            writer.write(message);
        }
    }
    
    // All JSON keys used when sending data to the web server:
    private class UpdateKeys
    {
        public static final String UPDATE_TIME = "updateTime";
        public static final String TILES = "tiles";
        public static final String KEYS = "keys";
        public static final String REGIONS = "regions";
        public static final String TYPES = "mapTypes";
    }
    
    // Relative server paths used for sending specific update data:
    private class ServerPaths
    {
        // Path used to send initial update data:
        public static final String UPDATE = "update";
        // Path used to send map tiles and map key images:
        public static final String IMAGE_UPLOAD = "imageUpload";
        
    }
    
    // The initial update message sent to the server
    private final JsonObject message;
    
}
