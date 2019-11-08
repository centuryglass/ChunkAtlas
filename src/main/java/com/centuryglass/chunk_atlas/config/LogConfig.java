/**
 * @file LogConfig.java
 * 
 * Loads logging options and uses them to generate a shared Logger object.
 */
package com.centuryglass.chunk_atlas.config;

import com.centuryglass.chunk_atlas.serverplugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import org.bukkit.plugin.PluginLogger;

public class LogConfig extends ConfigFile
{
    // Path to the resource holding default configuration options:
    private static final String DEFAULT_JSON_RESOURCE
            = "/configDefaults/logging.json";
    private static Logger logger = null;    
    
    // All JSON keys used:
    private class JsonKeys
    {
        protected static final String SERVER_LOG =  "minecraft server logging";
        protected static final String CONSOLE_LOG = "console logging";
        protected static final String FILE_LOGS =   "log files";
        protected static final String ENABLED =     "enabled";
        protected static final String LOG_LEVEL =   "lowest level";
        protected static final String LOG_PATH =    "path";
    }
        
    /**
     * Loads or initializes map generation options on construction.
     * 
     * @param configFile  A JSON configuration file holding options to load.
     *                    If this parameter is null or is not a valid JSON
     *                    file, it will be ignored, and default options will be
     *                    used. If this file does not exist and can be created,
     *                    default options will be copied to this file.
     */
    public LogConfig(File configFile)
    {
        super(configFile, DEFAULT_JSON_RESOURCE);
        if (logger != null)
        {
            logger.warning("Logger was already initialized.");
            return;
        }
        JsonObject pluginLogOptions = getObjectOption(JsonKeys.SERVER_LOG,
                JsonObject.EMPTY_JSON_OBJECT);
        JsonObject consoleLogOptions = getObjectOption(JsonKeys.CONSOLE_LOG,
                JsonObject.EMPTY_JSON_OBJECT);
        boolean useConsoleLogs = consoleLogOptions.getBoolean(JsonKeys.ENABLED,
                false);
        boolean usePluginLogs = pluginLogOptions.getBoolean(JsonKeys.ENABLED,
                false);
        
        // Initializes a handler with a SimpleFormatter and a log level parsed
        // from a String, and adds it to the logger. If the level is empty,
        // null, or otherwise invalid, the handler's logging level will not be
        // changed.
        BiConsumer<Handler, String> initHandler = (handler, levelStr) ->
        {
            handler.setFormatter(new SimpleFormatter());
            if (levelStr != null && ! levelStr.isEmpty())
            {
                try
                {
                    handler.setLevel(Level.parse(levelStr));
                }
                catch (IllegalArgumentException e)
                {
                    System.err.println("LogConfig.initLogger: invalid log level"
                        + "\"" + levelStr + "\" defined in config file.");
                }
            }
            else
            {
                System.err.println("No log level set, using default level "
                        + handler.getLevel().getName());
            }
            logger.addHandler(handler);
        };
        if (usePluginLogs && Plugin.isRunning())
        {
            logger = new PluginLogger(Plugin.getRunningPlugin());
            // Set the default handler's log level:
            String level = pluginLogOptions.getString(JsonKeys.LOG_LEVEL, null);
            assert (logger.getHandlers().length == 1);
            initHandler.accept(logger.getHandlers()[0], level);
            useConsoleLogs = false; // Plugin logging replaces console logging.
        }
        if (logger == null)
        {
            logger = Logger.getLogger("com.centuryglass.chunk_atlas", null);
            logger.setUseParentHandlers(false);
        }
        if (useConsoleLogs)
        {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            String level = consoleLogOptions.getString(JsonKeys.LOG_LEVEL,
                    null);
            initHandler.accept(consoleHandler, level);
            logger.addHandler(consoleHandler);
        }
        JsonArray logFileOptions = getArrayOption(JsonKeys.FILE_LOGS,
                JsonValue.EMPTY_JSON_ARRAY);
        logFileOptions.forEach((logFile) ->
        {
            if (logFile == null || logFile.getValueType()
                    != JsonValue.ValueType.OBJECT)
            {
                return;
            }
            JsonObject fileOption = (JsonObject) logFile;
            String logPath = fileOption.getString(JsonKeys.LOG_PATH, null);
            if (logPath == null || logPath.isEmpty()) { return; }
            try
            {
                FileHandler fileHandler = new FileHandler(logPath);
                String level = fileOption.getString(JsonKeys.LOG_LEVEL, null);
                initHandler.accept(fileHandler, level);
                logger.addHandler(fileHandler);
            }
            catch (IOException e)
            {
                System.err.println("LogConfig.initLogger: Failed to create "
                        + "log file at \"" + logPath + "\".");   
            }
        });
        logger.setLevel(Level.FINEST);
    }
    
    /**
     * Gets the shared ChunkAtlas logger object, initializing it with default
     * options if it has not been explicitly generated by creating a LogConfig
     * object.
     * 
     * @return  The shared application log object.
     */
    public static Logger getLogger()
    {
        if (logger == null)
        {
            System.out.println("LogConfig.getLogger: No LogConfig has been "
                    + "loaded, creating logger from default options.");
            LogConfig defaultOptions = new LogConfig(null);
        }
        return logger;
    }
}
