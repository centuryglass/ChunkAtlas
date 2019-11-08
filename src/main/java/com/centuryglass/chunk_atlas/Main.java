/**
 * @file Main.java
 * 
 * Main application class, used when launching ChunkAtlas as an executable jar.
 */
package com.centuryglass.chunk_atlas;

import com.centuryglass.chunk_atlas.config.LogConfig;
import com.centuryglass.chunk_atlas.util.args.ArgOption;
import com.centuryglass.chunk_atlas.util.args.ArgParser;
import com.centuryglass.chunk_atlas.webserver.security.RSAGenerator;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Starts map generation, using command line options, options read from a
 * configuration file, or default values.
 */
public class Main 
{
    /**
     * Starts map generation when the program is launched as an executable.
     * 
     * @param args  The list of arguments provided when launching the
     *              application.
     */
    public static void main(String [] args)
    {
        final ArgParser<MapArgOptions> argParser
                = MapArgOptions.createArgParser();
        final Runnable printHelpAndExit = () ->
        {
            System.out.println("Usage: ./ChunkAtlas [options]\nOptions:");
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
            if (argParser.optionFound(MapArgOptions.LOG_CONFIG_PATH))
            {
                final String logPath = argParser.getOptionParams(
                        MapArgOptions.LOG_CONFIG_PATH).getParameter(0);
                LogConfig logConfig = new LogConfig(new File(logPath));
                LogConfig.getLogger().fine("Initialized logging config from "
                        + " command line options.");
                LogConfig.getLogger().log(Level.FINEST,
                        "Custom logger path: {0}", logPath);
            }
            ArgOption<MapArgOptions> keyGen = argParser.getOptionParams(
                    MapArgOptions.GENERATE_RSA_KEYPAIR);
            if (keyGen != null)
            {
                LogConfig.getLogger().info("Skipping map creation and"
                        + " generating web server security keys.");
                File publicKeyFile = new File(keyGen.getParameter(0));
                File privateKeyFile = new File(keyGen.getParameter(1));
                try
                {
                    RSAGenerator.generate(publicKeyFile, privateKeyFile);
                }
                catch (IOException e)
                {
                    LogConfig.getLogger().log(Level.SEVERE,
                            "Failed to write key files: {0}", e.getMessage());
                }
                LogConfig.getLogger().info("Keys generated successfully.");
                return;
            }
        }
        catch (IllegalArgumentException e)
        {
            LogConfig.getLogger().severe(e.toString());
            printHelpAndExit.run();
        }
        MapUpdater.update(argParser);
    }
}
