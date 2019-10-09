/**
 * @file MapUpdater.java
 * 
 * Handles the process of generating map updates and sending those updates to
 * the map web server.
 */
package com.centuryglass.mcmap;

import com.centuryglass.mcmap.config.MapGenConfig;
import com.centuryglass.mcmap.config.WebServerConfig;
import com.centuryglass.mcmap.util.args.ArgOption;
import com.centuryglass.mcmap.util.args.ArgParser;
import com.centuryglass.mcmap.util.args.InvalidArgumentException;
import com.centuryglass.mcmap.webserver.Connection;
import com.centuryglass.mcmap.webserver.ServerUpdate;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Handles the process of generating map updates and sending those updates to
 * the map web server.
 */
public class MapUpdater
{
    /**
     * Attempts to load or generate update data, and optionally send that data
     * to a remote web server.
     * 
     * @param argParser  An optional parsed command line argument object.
     * 
     * @param mapConfig  An optional set of map generation options. If argParser
     *                   selects a different configuration file, it will
     *                   override this parameter.
     * 
     * @param webConfig  An optional set of web server connection options. If
     *                   argParser selects a different configuration file, it
     *                   will override this parameter.
     */
    public static void update(ArgParser<MapArgOptions> argParser,
            MapGenConfig mapConfig, WebServerConfig webConfig)
    {
        boolean reuseCache = false;
        File updateJson = null;
        if (argParser != null)
        {
            // Check for config paths in arguments:
            ArgOption<MapArgOptions> mapConfigOption
                    = argParser.getOptionParams(
                    MapArgOptions.MAP_CONFIG_PATH);
            if (mapConfigOption != null)
            {
                mapConfig = new MapGenConfig(new File(
                        mapConfigOption.getParameter(0)));
            }
            ArgOption<MapArgOptions> webConfigOption
                    = argParser.getOptionParams(
                    MapArgOptions.WEB_SERVER_CONFIG_PATH);
            if (webConfigOption != null)
            {
                webConfig = new WebServerConfig(new File(
                        webConfigOption.getParameter(0)));
            }
            // Check if update data should be saved to a file, and if that
            // update data should be reused instead of generating new maps:
            ArgOption<MapArgOptions> exportOption = argParser.getOptionParams(
                    MapArgOptions.UPDATE_CACHE_PATH);
            String exportPath = "";
            if (exportOption != null)
            {
                exportPath = exportOption.getParameter(0);
            }
            else if(webConfig != null)
            {
                exportPath = webConfig.getUpdateCachePath();
            }
            if (! exportPath.isEmpty())
            {
                updateJson = new File(exportPath);
            }
            if (updateJson != null && updateJson.isFile())
            {
                ArgOption<MapArgOptions> reuseUpdates
                        = argParser.getOptionParams(
                        MapArgOptions.USE_CACHED_UPDATE);
                if (reuseUpdates != null)
                {
                    try 
                    {
                        reuseCache = reuseUpdates.boolOptionStatus();
                    }
                    catch (InvalidArgumentException e)
                    {
                        System.err.println(e.getMessage());
                    }
                }
                else if (webConfig != null)
                {
                    reuseCache = webConfig.sendCachedUpdates();
                }
            }
        }
        
        // Generate or load update data:
        final MapCreator mapCreator;
        ServerUpdate updateManager = null;
        if (reuseCache)
        {
            try
            {
                updateManager = new ServerUpdate(updateJson);
            }
            catch (FileNotFoundException e)
            {
                // This should have been caught and handled earlier, reuseCache
                // should never be set to true if the file isn't found.
                assert false : e.getMessage();
            }
        }
        else
        {
            mapCreator = new MapCreator(mapConfig);
            try
            {
                mapCreator.applyArgOptions(argParser);
            }
            catch (InvalidArgumentException | FileNotFoundException e)
            {
                if (argParser != null)
                {
                    System.err.println("Error applying command line options: "
                            + e.getMessage());
                }
            }
            mapCreator.createMaps();
            updateManager = new ServerUpdate(mapCreator);
            if (updateJson != null)
            {
                try {
                    updateManager.exportUpdate(updateJson);
                }
                catch (IOException e)
                {
                    System.err.println(e.getMessage());
                }
            }
        }
        // Send the update data if server options were provided:
        if (updateManager != null && webConfig != null)
        {
            Connection serverConnection = new Connection(webConfig);
            updateManager.sendUpdate(serverConnection);
        }   
    }

    /**
     * Attempts to load or generate update data, and optionally send that data
     * to a remote web server.
     * 
     * @param argParser  An optional parsed command line argument object.
     */
    public static void update(ArgParser<MapArgOptions> argParser)
    {
        update(argParser, null, null);
    }
        
    /**
     * Attempts to load or generate update data, and optionally send that data
     * to a remote web server.
     * 
     * @param mapConfig  An optional set of map generation options. If argParser
     *                   selects a different configuration file, it will
     *                   override this parameter.
     * 
     * @param webConfig  An optional set of web server connection options. If
     *                   argParser selects a different configuration file, it
     *                   will override this parameter.
     */
    public static void update(MapGenConfig mapConfig, WebServerConfig webConfig)
    {
        update(null, mapConfig, webConfig);
    }
}
