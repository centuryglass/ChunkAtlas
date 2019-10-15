/**
 * @file ServerThread.java
 * 
 * Generates map tiles within its own thread when running as a server plugin.
 */
package com.centuryglass.chunk_atlas.serverplugin;

import com.centuryglass.chunk_atlas.MapUpdater;
import com.centuryglass.chunk_atlas.config.MapGenConfig;
import com.centuryglass.chunk_atlas.config.WebServerConfig;
import java.io.File;

/**
 * Generates map tiles within its own thread when running as a server plugin.
 */
public class ServerThread extends Thread
{   
    private static final String DEFAULT_SERVER_CONFIG
            = "plugins/chunk_atlas_mapGen.json";
    private static final String DEFAULT_CONNECTION_CONFIG
            = "plugins/chunk_atlas_webConnect.json";
    
    @Override
    public void run()
    {
        MapGenConfig mapConfig
                = new MapGenConfig(new File(DEFAULT_SERVER_CONFIG));
        WebServerConfig connectionConfig
                = new WebServerConfig(new File(DEFAULT_CONNECTION_CONFIG));
        MapUpdater.update(mapConfig, connectionConfig);
    }
}
