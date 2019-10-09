/**
 * @file ServerThread.java
 * 
 * Generates map tiles within its own thread when running as a server plugin.
 */
package com.centuryglass.mcmap.serverplugin;

import com.centuryglass.mcmap.MapUpdater;
import com.centuryglass.mcmap.config.MapGenConfig;
import com.centuryglass.mcmap.config.WebServerConfig;
import java.io.File;

/**
 * Generates map tiles within its own thread when running as a server plugin.
 */
public class ServerThread extends Thread
{   
    private static final String DEFAULT_SERVER_CONFIG
            = "plugins/mcmap_mapGen.json";
    private static final String DEFAULT_CONNECTION_CONFIG
            = "plugins/mcmap_webConnect.json";
    
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
