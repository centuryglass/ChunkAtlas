/**
 * @file ServerThread.java
 * 
 * Generates map tiles within its own thread when running as a server plugin.
 */
package com.centuryglass.mcmap.serverplugin;

import com.centuryglass.mcmap.MapCreator;
import com.centuryglass.mcmap.config.MapGenOptions;
import java.io.File;


public class ServerThread extends Thread
{   
    private static final String DEFAULT_SERVER_CONFIG
            = "plugins/mcmap_mapGen.json";
    
    @Override
    public void run()
    {
        MapGenOptions mapConfig
                = new MapGenOptions(new File(DEFAULT_SERVER_CONFIG));
        MapCreator mapCreator = new MapCreator(mapConfig);
        mapCreator.createMaps(); 
    }
}
