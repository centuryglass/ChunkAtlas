/**
 * @file Main.java
 * 
 * Main application class, used when launching ChunkAtlas as an executable jar.
 */
package com.centuryglass.chunk_atlas;

import com.centuryglass.chunk_atlas.util.args.ArgOption;
import com.centuryglass.chunk_atlas.util.args.ArgParser;
import com.centuryglass.chunk_atlas.util.args.InvalidArgumentException;
import com.centuryglass.chunk_atlas.webserver.security.RSAGenerator;
import java.io.File;
import java.io.IOException;

/**
 * Starts map generation, using command line options, options read from a
 * configuration file, or default values.
 */
public class Main 
{
    // Default values:
    private static final String DEFAULT_CONFIG_PATH = "mapGen.json";
    
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
            ArgOption<MapArgOptions> keyGen = argParser.getOptionParams(
                    MapArgOptions.GENERATE_RSA_KEYPAIR);
            if (keyGen != null)
            {
                System.out.println("Skipping map creation and generating web"
                        + " server security keys.");
                File publicKeyFile = new File(keyGen.getParameter(0));
                File privateKeyFile = new File(keyGen.getParameter(1));
                try
                {
                    RSAGenerator.generate(publicKeyFile, privateKeyFile);
                }
                catch (IOException e)
                {
                    System.err.println("Failed to write key files: "
                            + e.getMessage());
                }
                System.out.println("Keys generated successfully.");
            }
            return;
        }
        catch (InvalidArgumentException e)
        {
            System.err.println(e.getMessage());
            printHelpAndExit.run();
        }
        MapUpdater.update(argParser);
    }
}
