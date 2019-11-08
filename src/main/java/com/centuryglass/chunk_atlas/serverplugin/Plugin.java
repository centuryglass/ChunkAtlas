/**
 * @file Plugin.java
 * 
 * Allows ChunkAtlas to function as a Minecraft Spigot server plugin.
 */

package com.centuryglass.chunk_atlas.serverplugin;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Initializes ChunkAtlas as a Minecraft server plugin.
 */
public class Plugin extends JavaPlugin 
{
    /**
     * Checks if the application is running as a Minecraft server plugin.
     * 
     * @return  Whether this application was started by a Minecraft server using
     *          this Plugin class.
     */
    public static boolean isRunning()
    {
        return getRunningPlugin() != null;
    }
    
    /**
     * If the application is running as a Minecraft server plugin, this method
     * gets the running Plugin object.
     * 
     * @return  The Plugin, or null if the application isn't running as a server
     *          plugin.
     */
    public static Plugin getRunningPlugin()
    {
        try
        {
            JavaPlugin plugin = JavaPlugin.getProvidingPlugin(Plugin.class);
            return (Plugin) plugin;
        }
        catch (NoClassDefFoundError | IllegalArgumentException e)
        {
            return null;
        }
    }
    
    /**
     * Starts asynchronous server mapping when the plugin is enabled.
     */
    @Override
    public void onEnable()
    {
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }
    
    @Override
    public void onDisable() { }
}
