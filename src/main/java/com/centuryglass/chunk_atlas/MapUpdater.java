/**
 * @file MapUpdater.java
 * 
 * Handles the process of generating map updates and sending those updates to
 * the map web server.
 */
package com.centuryglass.chunk_atlas;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.config.MapGenConfig;
import com.centuryglass.chunk_atlas.config.WebServerConfig;
import com.centuryglass.chunk_atlas.util.args.ArgOption;
import com.centuryglass.chunk_atlas.util.args.ArgParser;
import com.centuryglass.chunk_atlas.webserver.Connection;
import com.centuryglass.chunk_atlas.webserver.ServerUpdate;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Handles the process of generating map updates and sending those updates to
 * the map web server.
 */
public class MapUpdater
{
    private static final String CLASSNAME = MapUpdater.class.getName();
    
    /**
     * Attempts to load or generate update data, and optionally send that data
     * to a remote web server.
     * 
     * @param argParser  An optional parsed command line argument object.
     * 
     * @param mapConfig  An optional set of map generation options. If
     *                   argParser selects a different configuration file, it
     *                   will override this parameter.
     * 
     * @param webConfig  An optional set of web server connection options. If
     *                   argParser selects a different configuration file, it
     *                   will override this parameter.
     */
    public static void update(ArgParser<MapArgOptions> argParser,
            MapGenConfig mapConfig, WebServerConfig webConfig)
    {
        final String FN_NAME = "update";
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
                LogConfig.getLogger().logp(Level.CONFIG, CLASSNAME, FN_NAME,
                        "Using MapGenConfig path from command line args.");
                mapConfig = new MapGenConfig(new File(
                        mapConfigOption.getParameter(0)));
            }
            ArgOption<MapArgOptions> webConfigOption
                    = argParser.getOptionParams(
                    MapArgOptions.WEB_SERVER_CONFIG_PATH);
            if (webConfigOption != null)
            {
                LogConfig.getLogger().logp(Level.CONFIG, CLASSNAME, FN_NAME,
                        "Using WebServerConfig path from command line args.");
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
                    catch (IllegalArgumentException e)
                    {
                        LogConfig.getLogger().logp(Level.SEVERE, CLASSNAME,
                                FN_NAME, e.toString());
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
        if (reuseCache && updateJson != null)
        {
            LogConfig.getLogger().logp(Level.FINE, CLASSNAME, FN_NAME,
                    "Reusing existing update data from '{0}'.", updateJson);
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
            LogConfig.getLogger().logp(Level.FINE, CLASSNAME, FN_NAME,
                    "Config files loaded, applying options to MapCreator");
            mapCreator = new MapCreator(mapConfig);
            try
            {
                mapCreator.applyArgOptions(argParser);
            }
            catch (IllegalArgumentException | FileNotFoundException e)
            {
                if (argParser != null)
                {
                    LogConfig.getLogger().logp(Level.SEVERE, CLASSNAME, FN_NAME,
                            "Error applying command line options:", e);
                }
            }
            LogConfig.getLogger().logp(Level.FINE, CLASSNAME, FN_NAME,
                    "Options loaded, creating new server maps.");
            mapCreator.createMaps();
            updateManager = new ServerUpdate(mapCreator);
            if (updateJson != null)
            {
                LogConfig.getLogger().logp(Level.CONFIG, CLASSNAME, FN_NAME,
                        "Saving map update data to '{0}'.", updateJson);
                try {
                    updateManager.exportUpdate(updateJson);
                }
                catch (IOException e)
                {
                    LogConfig.getLogger().logp(Level.SEVERE, CLASSNAME,
                            FN_NAME, e.toString());
                }
            }
        }
        LogConfig.getLogger().logp(Level.FINE, CLASSNAME, FN_NAME,
                "Map generation complete.");
        // Send the update data if server options were provided:
        if (updateManager != null && webConfig != null)
        {
            LogConfig.getLogger().logp(Level.FINE, CLASSNAME, FN_NAME,
                    "Sending update data to ChunkAtlas-Viewer web server.");
            Connection serverConnection = new Connection(webConfig);
            updateManager.sendUpdate(serverConnection);
        }
        else
        {
            LogConfig.getLogger().logp(Level.FINE, CLASSNAME, FN_NAME,
                    "No valid server to connect to, updated maps will not be"
                    + " transmitted.");
            
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
        // Attempt to get config files from argument paths. If paths are not
        // defined, load default config using temporary files.
        MapArgOptions[] configArgs =
        {
            MapArgOptions.MAP_CONFIG_PATH,
            MapArgOptions.WEB_SERVER_CONFIG_PATH
        };
        File[] configFiles = { null, null };
        for (int i = 0; i < configArgs.length; i++)
        {
            if (argParser.optionFound(configArgs[i]))
            {
                ArgOption<MapArgOptions> pathOptions
                        = argParser.getOptionParams(configArgs[i]);
                if (pathOptions.getParamCount() > 0)
                {
                    configFiles[i] = new File(pathOptions.getParameter(0));
                }
            }
        }
        MapGenConfig genConfig = new MapGenConfig(configFiles[0]);
        WebServerConfig serverConfig = new WebServerConfig(configFiles[1]);
        update(argParser, genConfig, serverConfig);
    }
        
    /**
     * Attempts to load or generate update data, and optionally send that data
     * to a remote web server.
     * 
     * @param mapConfig  An optional set of map generation options. If
     *                   argParser selects a different configuration file, it
     *                   will override this parameter.
     * 
     * @param webConfig  An optional set of web server connection options. If
     *                   argParser selects a different configuration file, it
     *                   will override this parameter.
     */
    public static void update
    (MapGenConfig mapConfig, WebServerConfig webConfig)
    {
        update(null, mapConfig, webConfig);
    }
}
