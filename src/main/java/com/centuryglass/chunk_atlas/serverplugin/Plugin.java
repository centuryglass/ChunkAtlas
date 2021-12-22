/**
 * @file Plugin.java
 * 
 * Allows ChunkAtlas to function as a Minecraft Spigot server plugin.
 */

package com.centuryglass.chunk_atlas.serverplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

/**
 * Initializes ChunkAtlas as a Minecraft server plugin.
 */
public class Plugin extends JavaPlugin 
{
    private ServerThread serverThread;
    
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
        serverThread = new ServerThread();
        serverThread.start();
        getCommand("startMapping").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            System.out.println("Command only allowed from Console,sender ="
                    + sender.getName());
            return true;
        }
        if (serverThread.isAlive()) {
            System.out.println("Server thread is already running");
            return true;
        }
        System.out.println("Restarting server thread..");
        serverThread = new ServerThread();
        return true;
    }
    
    @Override
    public void onDisable() { }
}
