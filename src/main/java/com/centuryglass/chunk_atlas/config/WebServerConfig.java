/**
 * @file WebServerConfig.java
 * 
 * Loads and shares a set of options for connecting to a web server that will
 * display generated maps. 
 */
package com.centuryglass.chunk_atlas.config;

import java.io.File;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 * Loads and shares a set of options for connecting to a web server that will
 * display generated maps. These options may be provided in a JSON
 * configuration file, or loaded from default options.
 */
public class WebServerConfig extends ConfigFile
{
    // Path to the resource holding default configuration options:
    private static final String DEFAULT_JSON_RESOURCE
            = "/configDefaults/webServer.json";
    // String to print when invalid option types are found:
    private static final String INVALID_OPTION_MSG = " options are invalid,"
                    + " check the web server connection configuration file.";
    // Maximum valid network port value:
    private static final int MAX_PORT = 65535;
    
    /**
     * Loads or initializes web server connection options on construction.
     * 
     * @param configFile  A JSON configuration file holding options to load.
     *                    If this parameter is null or is not a valid JSON
     *                    file, it will be ignored, and default options will
     *                    be used. If this file does not exist and can be
     *                    created, default options will be copied to this file.
     */
    public WebServerConfig(File configFile)
    {
        super(configFile, DEFAULT_JSON_RESOURCE);
    }
    
    /**
     * Gets the address of the web server.
     * 
     * @return  The web server's address, or the empty string if the address is
     *          missing or not a String.
     */
    public String getServerAddress()
    {
        String address = getStringOption(JsonKeys.ADDRESS, "");
        if (address.isEmpty())
        {
            System.err.println("Web server address" + INVALID_OPTION_MSG);
        }
        return address;
    }
    
    /**
     * Gets the port used by the web server.
     * 
     * @return  The web server's port, or -1 if the port is not defined or not
     *          a valid port number.
     */
    public int getServerPort()
    {
        int port = getIntOption(JsonKeys.PORT, -1);
        if (port <= 0 || port > MAX_PORT)
        {
            System.err.println("Web server port" + INVALID_OPTION_MSG);
            return -1;
        }
        return port;
    }
    
    /**
     * Gets the path where cached update data should be saved.
     * 
     * @return  The path to a JSON cache file, or the empty string if update
     *          data shouldn't be cached.
     */
    public String getUpdateCachePath()
    {
        return getStringOption(JsonKeys.CACHE, "");
    }
    
    /**
     * Checks whether cached update data should be reused if present.
     * 
     * @return  Whether old update data should be sent instead of running a
     *          new update.
     */
    public boolean sendCachedUpdates()
    {
        return getBoolOption(JsonKeys.SEND_CACHE, false);
    }
    
    /**
     * Gets the File holding this application's public RSA key.
     * 
     * @return  The public key's file path. 
     */
    public File getPublicKeyFile()
    {
        JsonObject keys = getObjectOption(JsonKeys.KEY_PATHS,
                JsonValue.EMPTY_JSON_OBJECT);
        String path = keys.getJsonString(JsonKeys.PUBLIC_KEY).getString();
        return new File(path);
    }
            
    /**
     * Gets the File holding this application's private RSA key.
     * 
     * @return  The private key's file path.
     */
    public File getPrivateKeyFile()
    {
        JsonObject keys = getObjectOption(JsonKeys.KEY_PATHS,
                JsonValue.EMPTY_JSON_OBJECT);
        String path = keys.getJsonString(JsonKeys.PRIVATE_KEY).getString();
        return new File(path);
    }
            
    /**
     * Gets the File holding the ChunkAtlas web server's public RSA key.
     * 
     * @return  The server public key's file path.
     */
    public File getWebPublicKeyFile()
    {
        JsonObject keys = getObjectOption(JsonKeys.KEY_PATHS,
                JsonValue.EMPTY_JSON_OBJECT);
        String path = keys.getJsonString(JsonKeys.WEB_SERVER_PUBLIC)
                .getString();
        return new File(path);
    }
    
    // All JSON key strings used by the configuration file.
    private class JsonKeys
    {
        public static final String ADDRESS = "address";
        public static final String PORT = "port";
        public static final String CACHE = "cached update";
        public static final String SEND_CACHE = "send cached";
        public static final String KEY_PATHS = "key paths";
        public static final String PUBLIC_KEY = "public";
        public static final String PRIVATE_KEY = "private";
        public static final String WEB_SERVER_PUBLIC = "web server public";
    }   
}
