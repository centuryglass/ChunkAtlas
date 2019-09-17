/**
 * @file Plugin.java
 * 
 * Allows MCMaps to function as a Minecraft Spigot server plugin.
 */

package com.centuryglass.mcmap.serverplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin 
{
    @Override
    public void onEnable()
    {
        System.out.println("MCMap enabled, starting scan:");
        ServerThread serverThread = new ServerThread();
        serverThread.start();
    }
    
    @Override
    public void onDisable()
    {
    }
}
