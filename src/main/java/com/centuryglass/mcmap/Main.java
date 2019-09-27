/**
 * @file Main.java
 * 
 * Main application class, used when launching MCMap as an executable jar.
 */
package com.centuryglass.mcmap;

import com.centuryglass.mcmap.config.MapGenOptions;
import com.centuryglass.mcmap.util.args.ArgParser;
import com.centuryglass.mcmap.util.args.InvalidArgumentException;
import java.io.File;

/**
 * Starts map generation, using command line options, options read from a
 * configuration file, or default values.
 */
public class Main 
{
    // Default values:
    private static final String DEFAULT_CONFIG_PATH = "mapGen.json";
    
    public static void main(String [] args)
    {
        final ArgParser<MapArgOptions> argParser
                = MapArgOptions.createArgParser();
        final Runnable printHelpAndExit = () ->
        {
            System.out.println("Usage: ./MCMap [options]\nOptions:");
            System.out.print(argParser.getHelpText());
            System.exit(0);
        };
        try
        {
            argParser.parseArguments(args);
            if (argParser.optionFound(MapArgOptions.HELP))
            {
                printHelpAndExit.run();
                
            }
        }
        catch (InvalidArgumentException e)
        {
            System.err.println(e.getMessage());
            printHelpAndExit.run();
        }
        
        // Load options from arguments, a configuration file, or default values:
        String configPath = DEFAULT_CONFIG_PATH;
        if (argParser.optionFound(MapArgOptions.CONFIG_PATH))
        {
            configPath = argParser.getOptionParams(MapArgOptions.CONFIG_PATH)
                    .getParameter(0);
        }
        MapGenOptions mapConfig = new MapGenOptions(new File(configPath));
        MapCreator mapCreator = new MapCreator(mapConfig);
        try
        {
            mapCreator.applyArgOptions(argParser);
        }
        catch (InvalidArgumentException e)
        {
            System.err.println(e.getMessage());
            printHelpAndExit.run();
        }
        
        // Apply settings to create maps:
        mapCreator.createMaps();
    }
}
