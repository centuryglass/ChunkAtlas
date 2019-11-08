/**
 * @file ConfigFile.java
 * 
 * Reads configurable options from a JSON file or a default resource file.
 */

package com.centuryglass.chunk_atlas.config;

import com.centuryglass.chunk_atlas.util.JarResource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Function;
import java.util.logging.Level;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import org.apache.commons.lang.Validate;

/**
 * Manages a set of configurable options stored in JSON data, loaded from a
 * file or from a default resource.
 */
public class ConfigFile
{
    private static final String CLASSNAME = ConfigFile.class.getName();
    
    /**
     * Loads or initializes options on construction.
     * 
     * @param configFile       A JSON configuration file where options should
     *                         be loaded. If this parameter is null or is not a
     *                         valid JSON file, it will be ignored, and default
     *                         options will be used. If this file does not
     *                         exist and can be created, default options will
     *                         be copied to this file.
     * 
     * @param defaultFilePath  The path to a resource holding the configuration
     *                         file's default options.
     */
    protected ConfigFile(File configFile, String defaultFilePath)
    {
        final String FN_NAME = "ConfigFile";
        Validate.notNull(defaultFilePath, "Default path must not be null.");
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
                // If the file is empty, this exception is expected, and doesn't
                // need to be printed.
                if (configFile.length() > 0)
                {
                    LogConfig.getLogger().logp(Level.WARNING, CLASSNAME,
                            FN_NAME, "Failed to load '{0}' using default "
                                    + "configuration options: {1}",
                            new Object[] { configFile, ex });
                    loadedOptions = null;
                }
            }
        }
        
        // Attempt to load default options:
        try    
        {
            defaultOptions = (JsonObject) JarResource.readJsonResource(
                    defaultFilePath);
        }
        catch (IOException e)
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Error loading default options from '{0}': {1}",
                    new Object[] { defaultFilePath, e });
        }
        
        // Copy defaults if appropriate:
        if (defaultOptions != null && loadedOptions == null
                && configFile != null)
        {
            try
            {
                JarResource.copyResourceToFile(defaultFilePath, configFile);
            }
            catch (IOException e)
            {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Error copying default options from '{0}': {1}",
                    new Object[] { defaultFilePath, e });
            }
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
    protected final JsonValue getSavedOrDefaultOptions(String jsonKey)
    {
        final String FN_NAME = "getSavedOrDefaultOptions";
        Validate.notNull(jsonKey, "JSONKey must not be null.");
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
        Function<JsonValue, JsonValue.ValueType> compType = (value) ->
        {
            if (value == null) { return JsonValue.ValueType.NULL; }
            JsonValue.ValueType type = value.getValueType();
            // For the sake of comparison: convert all boolean value types to
            // TRUE
            if (type.equals(JsonValue.ValueType.FALSE))
            {
                return JsonValue.ValueType.TRUE;
            }
            return type;
        };
        if (! compType.apply(defaultValue).equals(compType.apply(configValue)))
        {
            LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                    "Excepcted config value of type {0} for key '{1}', but"
                    + " found value '{2}' with type {3}",
                    new Object[]{
                        defaultValue.getValueType(),
                        jsonKey, 
                        configValue,
                        configValue.getValueType()
                    });
            return defaultValue; 
        }
        return configValue;
    }
    
    /**
     * Loads a JSON object value from the configuration file or default
     * options.
     * 
     * @param jsonKey       The object value's key string.
     * 
     * @param defaultValue  A default value to return if the requested value is
     *                      missing or is not an object.
     * 
     * @return              The requested object if possible, the defaultValue
     *                      otherwise.
     */
    protected final JsonObject getObjectOption
    (String jsonKey, JsonObject defaultValue)
    {
        Validate.notNull(jsonKey, "JSONKey must not be null.");
        return (JsonObject) getTypedOption(jsonKey, ValueType.OBJECT,
                (JsonValue) defaultValue);
    }
        
    /**
     * Loads a JSON array value from the configuration file or default options.
     * 
     * @param jsonKey       The array value's key string.
     * 
     * @param defaultValue  A default value to return if the requested value is
     *                      missing or is not an array.
     * 
     * @return              The requested array if possible, the defaultValue
     *                      otherwise.
     */
    protected final JsonArray getArrayOption
    (String jsonKey, JsonArray defaultValue)
    {
        Validate.notNull(jsonKey, "JSONKey must not be null.");
        return (JsonArray) getTypedOption(jsonKey, ValueType.ARRAY,
                (JsonValue) defaultValue);
    }
            
    /**
     * Loads a JSON string value from the configuration file or default
     * options.
     * 
     * @param jsonKey       The string value's key string.
     * 
     * @param defaultValue  A default value to return if the requested value is
     *                      missing or is not a string.
     * 
     * @return              The requested string if possible, the defaultValue
     *                      otherwise.
     */
    protected final String getStringOption
    (String jsonKey, String defaultValue)
    {
        Validate.notNull(jsonKey, "JSONKey must not be null.");
        JsonValue option = getTypedOption(jsonKey, ValueType.STRING, null);
        if (option == null)
        {
            return defaultValue;
        }
        return ((JsonString) option).getString();
    }
                
    /**
     * Loads an integer JSON value from the configuration file or default
     * options.
     * 
     * @param jsonKey       The integer value's key string.
     * 
     * @param defaultValue  A default value to return if the requested value is
     *                      missing or is not integral.
     * 
     * @return              The requested integer if possible, the defaultValue
     *                      otherwise.
     */
    protected final int getIntOption
    (String jsonKey, int defaultValue)
    {
        Validate.notNull(jsonKey, "JSONKey must not be null.");
        JsonNumber option
                = (JsonNumber) getTypedOption(jsonKey, ValueType.NUMBER, null);
        if (option == null || ! option.isIntegral())
        {
            return defaultValue;
        }
        try
        {
            return option.intValueExact();
        }
        catch (ArithmeticException e)
        {
            return defaultValue;
        }
    }
             
    /**
     * Loads a long integer JSON value from the configuration file or default
     * options.
     * 
     * @param jsonKey       The long integer value's key string.
     * 
     * @param defaultValue  A default value to return if the requested value is
     *                      missing or is not integral.
     * 
     * @return              The requested long if possible, the defaultValue
     *                      otherwise.
     */
    protected final long getLongOption
    (String jsonKey, long defaultValue)
    {
        Validate.notNull(jsonKey, "JSONKey must not be null.");
        JsonNumber option
                = (JsonNumber) getTypedOption(jsonKey, ValueType.NUMBER, null);
        if (option == null || ! option.isIntegral())
        {
            return defaultValue;
        }
        try
        {
            return option.longValueExact();
        }
        catch(ArithmeticException e)
        {
            return defaultValue;
        }
    }
    
                 
    /**
     * Loads a double JSON value from the configuration file or default
     * options.
     * 
     * @param jsonKey       The double value's key string.
     * 
     * @param defaultValue  A default value to return if the requested value is
     *                      missing or non-numeric.
     * 
     * @return              The requested double if possible, the defaultValue
     *                      otherwise.
     */
    protected final double getDoubleOption
    (String jsonKey, double defaultValue)
    {
        Validate.notNull(jsonKey, "JSONKey must not be null.");
        JsonNumber option
                = (JsonNumber) getTypedOption(jsonKey, ValueType.NUMBER, null);
        if (option == null)
        {
            return defaultValue;
        }
        return option.doubleValue();
    }
                    
    /**
     * Loads a boolean JSON value from the configuration file or default
     * options.
     * 
     * @param jsonKey       The boolean value's key string.
     * 
     * @param defaultValue  A default value to return if the requested value is
     *                      missing or is not boolean.
     * 
     * @return              The requested boolean value if possible, the
     *                      defaultValue otherwise.
     */
    protected final boolean getBoolOption(String jsonKey, boolean defaultValue)
    {
        Validate.notNull(jsonKey, "JSONKey must not be null.");
        JsonValue configValue = getSavedOrDefaultOptions(jsonKey);
        if (configValue == null)
        {
            return defaultValue;
        }
        switch (configValue.getValueType())
        {
            case FALSE:
                return false;
            case TRUE:
                return true;
            default:
                return defaultValue;
        }
    }
    
    /**
     * Returns a value from the configuration file or default options with a
     * specific JSON data type,  or returns an alternate default value if the
     * option is missing or not the expected type.
     * 
     * @param jsonKey       The key of a configurable value to find.
     * 
     * @param expectedType  The JSON data type the value should hold.
     * 
     * @param defaultValue  An alternate value to return if the value is
     *                      missing or not the right type. Behavior is
     *                      undefined if this value is not of the expected
     *                      type or null. 
     * 
     * @return              The requested value if possible, the defaultValue
     *                      otherwise.
     */
    private JsonValue getTypedOption
    (String jsonKey, ValueType expectedType, JsonValue defaultValue)
    {
        Validate.notNull(jsonKey, "JSONKey must not be null.");
        assert (defaultValue == null || defaultValue.getValueType()
                == expectedType);
        JsonValue configValue = getSavedOrDefaultOptions(jsonKey);
        if (configValue == null
                || configValue.getValueType() != expectedType)
        {
            return defaultValue;
        }
        return configValue;
    }
    
    private JsonObject loadedOptions;
    private JsonObject defaultOptions;   
}
