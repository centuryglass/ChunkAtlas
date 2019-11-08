/**
 * @file LogConfigTest.java
 * 
 * Tests com.centuryglass.chunk_atlas.config.LogConfig.
 */
package com.centuryglass.chunk_atlas.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LogConfigTest {
    private static final String[] LOG_LEVELS =
    {
        "SEVERE",
        "WARNING",
        "INFO",
        "CONFIG",
        "FINE",
        "FINER",
        "FINEST",
    };
    private static File[] logFiles = new File[LOG_LEVELS.length];
    
    @BeforeAll
    public static void setUpClass()
    {
        JsonObjectBuilder configBuilder = Json.createObjectBuilder();
        JsonObjectBuilder serverConfBuilder = Json.createObjectBuilder();
        serverConfBuilder.add("enabled", true);
        serverConfBuilder.add("lowest level", "INFO");
        configBuilder.add("minecraft server logging",
                serverConfBuilder.build());
        JsonObjectBuilder consoleConfBuilder = Json.createObjectBuilder();
        consoleConfBuilder.add("enabled", true);
        consoleConfBuilder.add("lowest level", "INFO");
        configBuilder.add("console logging", consoleConfBuilder.build());
        JsonArrayBuilder fileListBuilder = Json.createArrayBuilder();
        for (int i = 0; i < LOG_LEVELS.length; i++)
        {
            String level = LOG_LEVELS[i];
            try
            {
                logFiles[i] = File.createTempFile(level, ".log");
            }
            catch (IOException e)
            {
                System.err.println("Failed to create temp log file.");
                return;
            }
            JsonObjectBuilder fileLogBuilder = Json.createObjectBuilder();
            fileLogBuilder.add("path", logFiles[i].toString());
            fileLogBuilder.add("lowest level", level);
            fileListBuilder.add(fileLogBuilder.build());
        }
        configBuilder.add("log files", fileListBuilder.build());
        JsonObject logConfig = configBuilder.build();
        try
        {
            File tempConfigFile = File.createTempFile("logConfig", ".json");
            try (JsonWriter writer = Json.createWriter(
                    new FileOutputStream(tempConfigFile)))
            {
                writer.writeObject(logConfig);
            }
            LogConfig initializer = new LogConfig(tempConfigFile);
            tempConfigFile.delete();
        }
        catch (IOException e)
        {
            System.err.println("Failed to create temporary log config file.");
        }
    }
    
    @AfterAll
    public static void tearDownClass()
    {
        System.out.println("Deleting temporary log files.");
        for (File tempLog : logFiles)
        {
            tempLog.delete();
        }
    }

    /**
     * Test of getLogger method, of class LogConfig.
     */
    @Test
    public void testGetLogger()
    {
        assertNotNull(LogConfig.getLogger());
    }
    
    private static String readFileString(File file)
    {
        try
        {
            return new String(Files.readAllBytes(file.toPath()));
        }
        catch (IOException e)
        {
            return "";
        }
    }
    
    /**
     * Test that logging works as expected.
     */
    @Test
    public void testLogging()
    {
        Logger logger = LogConfig.getLogger();
        final String finestLogMessage = "Logging at Level.FINEST";
        logger.finest(finestLogMessage);
        for (File log : logFiles)
        {
            boolean isFinestFile = (log.equals(logFiles[logFiles.length - 1]));
            String logText = readFileString(log);
            if (isFinestFile)
            {
                assertTrue(! logText.isEmpty(), "Level.FINEST log file "
                        + "shouldn't be empty.");
                assertTrue(logText.contains(finestLogMessage),
                        "Message should have been written to Level.FINEST "
                        + "log file");
            }
            else
            {
                assertTrue(logText.isEmpty(), "Log files at levels above "
                        + "Level.FINEST should be empty.");
            }
            /*
            assertTrue(isFinestFile ^ logText.isEmpty(),
                    "Log files at levels above Level.FINEST should be empty.");
            assertTrue((! isFinestFile) ^ logText.contains(finestLogMessage),
                    "Message should have been written to the Level.FINEST test "
                    + "log file only.");
            */
        }
        logger.info("This message must be logged to the console.");
    }
    
}
