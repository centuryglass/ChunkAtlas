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
     * Starts asynchronous server mapping when the plugin is enabled.
     */
    @Override
    public void onEnable()
    {
        System.out.println("ChunkAtlas enabled, starting scan:");
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }
    
    @Override
    public void onDisable() { }
}
