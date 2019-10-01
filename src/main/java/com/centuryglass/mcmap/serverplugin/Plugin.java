/**
 * @file Plugin.java
 * 
 * Allows MCMaps to function as a Minecraft Spigot server plugin.
 */

package com.centuryglass.mcmap.serverplugin;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Initializes MCMaps as a Minecraft server plugin.
 */
public class Plugin extends JavaPlugin 
{
    /**
     * Starts asynchronous server mapping when the plugin is enabled.
     */
    @Override
    public void onEnable()
    {
        System.out.println("MCMap enabled, starting scan:");
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }
    
    @Override
    public void onDisable() { }
}
