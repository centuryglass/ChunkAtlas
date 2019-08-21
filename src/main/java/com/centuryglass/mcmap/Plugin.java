/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.centuryglass.mcmap;

import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author anthony
 */
public class Plugin extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        System.out.println("MCMap enabled.");
    }
    
    @Override
    public void onDisable()
    {
        System.out.println("MCMap disabled.");
    }
    
}
