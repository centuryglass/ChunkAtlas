/**
 * @file ConfigFile.java
 * 
 * Reads configurable options from a JSON file or a default resource file.
 */

package com.centuryglass.mcmap.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 * Manages a set of configurable options stored in JSON data, loaded from a
 * file or from a default resource.
 */
public class ConfigFile
{
    /**
     * Loads or initializes options on construction.
     * 
     * @param configFile       A JSON configuration file where options should be
     *                         loaded. If this parameter is null or is not a
     *                         valid JSON file, it will be ignored, and default
     *                         options will be used. If this file does not exist
     *                         and can be created, default options will be
     *                         copied to this file.
     * 
     * @param defaultFilePath  The path to a resource holding the configuration
     *                         file's default options.
     */
    protected ConfigFile(File configFile, String defaultFilePath)
    {
        // Attempt to load JSON options:
        if (configFile != null && configFile.isFile())
        {
            try (JsonReader reader 
                    = Json.createReader(new FileInputStream(configFile)))
            {
                loadedOptions = reader.readObject();
            }
            catch (FileNotFoundException | JsonException |
                    IllegalStateException ex) 
            {
                System.err.println("Failed to load " + configFile.getName()
                        + ", using default configuration options.");
                System.err.println("Error type encountered: "
                        + ex.getMessage());
                loadedOptions = null;
            }
        }
        
        // Attempt to load default options:
        try (InputStream optionStream = ConfigFile.class.getResourceAsStream(
                        defaultFilePath))       
        {
            assert (optionStream != null);
            try (JsonReader reader = Json.createReader(optionStream))
            {
                defaultOptions = reader.readObject();
            }
            catch (JsonException | IllegalStateException ex)
            {
                System.err.println("Failed to load default config resource "
                        + defaultFilePath + ": " + ex.getMessage());
                defaultOptions = null;
            }
            // Copy defaults if appropriate:
            if (configFile != null && ! configFile.exists()
                    && configFile.canWrite() && configFile.createNewFile())
            {
                optionStream.reset();
                FileOutputStream output = new FileOutputStream(configFile);
                byte[] copyBuffer = new byte[1280];
                int bytesRead;
                while ((bytesRead = optionStream.read(copyBuffer)) != -1)
                {
                    output.write(copyBuffer, 0, bytesRead);
                }
                output.close();
            }
        }
        catch (IOException e)
        {
            System.err.println("Error reading/copying default config: "
                    + e.getMessage());
        }
    }
            
    /**
     * Returns a value read from the configuration file, or one read from the
     * default options if the configuration file is missing or lacks the
     * requested value.
     * 
     * @param jsonKey  The key string used to store a configurable value.
     * 
     * @return         The requested value, or null if jsonKey isn't found in
     *                 the configuration file or in default options.
     */
    protected JsonValue getSavedOrDefaultOptions(String jsonKey)
    {
        JsonValue defaultValue = defaultOptions.get(jsonKey);
        if (loadedOptions == null)
        {
            return defaultValue;
        }
        JsonValue configValue = loadedOptions.get(jsonKey);
        if (configValue == null)
        {
            return defaultValue;
        }
        if (defaultValue != null && ! configValue.getValueType().equals(
                defaultValue.getValueType()))
        {
            System.err.println("Warning: expected config value of type "
                    + defaultValue.getValueType().toString() + " for key "
                    + jsonKey + ", but found value of type "
                    + configValue.getValueType().toString() + ".");
            System.err.println("Invalid value: " + configValue.toString());
            return defaultValue; 
        }
        return configValue;
    }
    
    private JsonObject loadedOptions;
    private JsonObject defaultOptions;   
}
