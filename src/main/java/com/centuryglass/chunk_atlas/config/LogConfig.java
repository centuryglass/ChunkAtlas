/**
 * @file LogConfig.java
 * 
 * Loads logging options and uses them to generate a shared Logger object.
 */
package com.centuryglass.chunk_atlas.config;

import com.centuryglass.chunk_atlas.serverplugin.Plugin;
import com.centuryglass.chunk_atlas.util.LogLineFormatter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * Initializes the shared ChunkAtlas Logger object from JSON configuration data.
 */
public class LogConfig extends ConfigFile
{
    private static class DuplicateFilter implements Filter
    {
        public DuplicateFilter() {
            lastMessage = "";
            lastTime = 0;
            
        }
        

        @Override
        public boolean isLoggable(LogRecord lr) 
        {
            if (lr.getMessage().equals(lastMessage))
            {
                return (System.currentTimeMillis() - 10000) > lastTime; 
            }
            lastMessage = lr.getMessage();
            lastTime = System.currentTimeMillis();
            return true;
        }
        
        private String lastMessage;
        private long lastTime;
        
    };
    
    private static final String CLASSNAME = ConfigFile.class.getName();
    
    // Path to the resource holding default configuration options:
    private static final String DEFAULT_JSON_RESOURCE
            = "/configDefaults/logging.json";
    private static Logger logger = null;    
    
    // All JSON keys used:
    private class JsonKeys
    {
        protected static final String SERVER_LOG
                = "minecraft server logging enabled";
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
        final String FN_NAME = "LogConfig";
        if (configFile != null)
        {
            getLogger().logp(Level.CONFIG, CLASSNAME, FN_NAME,
                    "Creating configured logger with file '{0}'.",
                    configFile);
        }
        JsonObject consoleLogOptions = getObjectOption(JsonKeys.CONSOLE_LOG,
                JsonObject.EMPTY_JSON_OBJECT);
        boolean useConsoleLogs = consoleLogOptions.getBoolean(JsonKeys.ENABLED,
                false);
        boolean usePluginLogs = getBoolOption(JsonKeys.SERVER_LOG, false);

        if (usePluginLogs && Plugin.isRunning())
        {
            logger = Plugin.getRunningPlugin().getLogger();
            useConsoleLogs = false; // Plugin logging replaces console logging.
        }
        if (logger == null)
        {
            logger = Logger.getLogger("com.centuryglass.chunk_atlas", null);
            logger.setUseParentHandlers(false);
        }
                
        // Initializes a handler with a LogLineFormatter and a log level parsed
        // from a String. If the level is empty, null, or otherwise invalid, the
        // handler's logging level will not be changed.
        BiConsumer<Handler, String> initHandler = (handler, levelStr) ->
        {
            handler.setFormatter(new LogLineFormatter());
            handler.setFilter(new DuplicateFilter());
            if (levelStr != null && ! levelStr.isEmpty())
            {
                try
                {
                    handler.setLevel(Level.parse(levelStr));
                }
                catch (IllegalArgumentException e)
                {
                    Logger.getGlobal().logp(Level.WARNING, CLASSNAME, FN_NAME,
                            "Invalid log level '{0}' defined in config file.",
                            levelStr);
                }
            }
            else
            {
                Logger.getGlobal().logp(Level.WARNING, CLASSNAME, FN_NAME,
                        "No log level set, using default level {0}.",
                        handler.getLevel().getName());
            }
        };
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
                Logger.getGlobal().logp(Level.WARNING, CLASSNAME, FN_NAME,
                        "Failed to create logging config file at '{0}'.",
                        logPath); 
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
        final String FN_NAME = "getLogger";
        if (logger == null)
        {
            Logger.getGlobal().logp(Level.FINEST, CLASSNAME, FN_NAME,
                    "No LogConfig loaded, using default.");
            return Logger.getGlobal();
        }
        return logger;
    }
}
