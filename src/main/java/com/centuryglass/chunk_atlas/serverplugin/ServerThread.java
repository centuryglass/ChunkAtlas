/**
 * @file ServerThread.java
 * 
 * Generates map tiles within its own thread when running as a server plugin.
 */
package com.centuryglass.chunk_atlas.serverplugin;

import com.centuryglass.chunk_atlas.MapUpdater;
import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.config.MapGenConfig;
import com.centuryglass.chunk_atlas.config.WebServerConfig;
import java.io.File;

/**
 * Generates map tiles within its own thread when running as a server plugin.
 */
public class ServerThread extends Thread
{
    // Config file paths to use when running as a server plugin:
    private static final String SERVER_CONFIG_PATH
            = "plugins/ChunkAtlas/mapGen.json";
    private static final String CONNECTION_CONFIG_PATH
            = "plugins/ChunkAtlas/webConnect.json";
    private static final String LOGGING_CONFIG_PATH
            = "plugins/ChunkAtlas/logging.json";
    
    @Override
    public void run()
    {
        MapGenConfig mapConfig
                = new MapGenConfig(new File(SERVER_CONFIG_PATH));
        WebServerConfig connectionConfig
                = new WebServerConfig(new File(CONNECTION_CONFIG_PATH));
        LogConfig logConfig
                = new LogConfig(new File(LOGGING_CONFIG_PATH));
        LogConfig.getLogger().finest("Started plugin thread, and initialized "
                + "config objects.");
        MapUpdater.update(mapConfig, connectionConfig);
    }
}
