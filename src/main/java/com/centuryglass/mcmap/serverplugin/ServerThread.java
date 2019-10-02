/**
 * @file ServerThread.java
 * 
 * Generates map tiles within its own thread when running as a server plugin.
 */
package com.centuryglass.mcmap.serverplugin;

import com.centuryglass.mcmap.MapCreator;
import com.centuryglass.mcmap.config.MapGenConfig;
import com.centuryglass.mcmap.config.WebServerConfig;
import com.centuryglass.mcmap.webserver.Connection;
import java.io.File;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;

/**
 * Generates map tiles within its own thread when running as a server plugin.
 */
public class ServerThread extends Thread
{   
    private static final String DEFAULT_SERVER_CONFIG
            = "plugins/mcmap_mapGen.json";
    private static final String DEFAULT_CONNECTION_CONFIG
            = "plugins/mcmap_webConnect.json";
    
    // All JSON keys used when sending data to the web server:
    private class UpdateKeys
    {
        public static final String UPDATE_TIME = "updateTime";
        public static final String TILES = "tiles";
        public static final String KEYS = "keys";
    }
    
    @Override
    public void run()
    {
        MapGenConfig mapConfig
                = new MapGenConfig(new File(DEFAULT_SERVER_CONFIG));
        MapCreator mapCreator = new MapCreator(mapConfig);
        mapCreator.createMaps();
        
        // Construct update message data:
        JsonObjectBuilder messageBuilder = Json.createObjectBuilder();
        messageBuilder.add(UpdateKeys.UPDATE_TIME, System.currentTimeMillis());
        messageBuilder.add(UpdateKeys.TILES, mapCreator.getMapTileList());
        messageBuilder.add(UpdateKeys.KEYS, mapCreator.getMapKeys());
        
        // Send results to the web server:
        WebServerConfig connectionConfig
                = new WebServerConfig(new File(DEFAULT_CONNECTION_CONFIG));
        Connection webConnection = new Connection(connectionConfig);
        JsonStructure response
                = webConnection.sendJson(messageBuilder.build());
        if (response == null)
        {
            System.out.println("No response from web server.");
        }
        else
        {
            System.out.println("Server response: " + response.toString());
        }
    }
    
    
}
