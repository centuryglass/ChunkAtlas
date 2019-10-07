/**
 * @file KeyItem.java
 * 
 * Represents an item within a map key.
 */

package com.centuryglass.mcmap.mapping;

import com.centuryglass.mcmap.mapping.maptype.MapType;
import com.centuryglass.mcmap.util.ExtendedValidate;
import java.awt.Color;
import java.util.function.Function;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.apache.commons.lang.Validate;

/**
 * An entry within a map key for a specific map type and region. Each KeyItem
 * is displayed as a small thumbnail next to a brief description, used to
 * indicate what a specific map color or texture represents.
 */
public class KeyItem
{
    /**
     * Creates a map key item with both an image and a fallback color value.
     * 
     * @param description  The description printed by this key item.
     * 
     * @param type         The map type associated with this key item.
     * 
     * @param region       The map region associated with this key item.
     * 
     * @param imagePath    The path to an image file or embedded image resource
     *                     to display by this key item.
     * 
     * @param color        A color to display if the image cannot be loaded.
     */
    public KeyItem(String description, MapType type, String region,
            String imagePath, Color color)
    {
        checkParams(description, type, region, imagePath, color);
        this.description = description;
        this.type = type;
        this.region = region;
        this.imagePath = imagePath;
        this.color = color;
    }
    
    /**
     * Creates a map key item holding an image and description.
     * 
     * @param description  The description printed by this key item.
     * 
     * @param type         The map type associated with this key item.
     * 
     * @param region       The map region associated with this key item.
     * 
     * @param imagePath    The path to an image file or embedded image resource
     *                     to display by this key item.
     */
    public KeyItem(String description, MapType type, String region,
            String imagePath)
    {
        checkParams(description, type, region, imagePath, null);
        this.description = description;
        this.type = type;
        this.region = region;
        this.imagePath = imagePath;
        color = null;
    }
    
    /**
     * Creates a map key item with a displayed color.
     * 
     * @param description  The description printed by this key item.
     * 
     * @param type         The map type associated with this key item.
     * 
     * @param region       The map region associated with this key item.
     * 
     * @param color        A color to display for this key item.
     */
    public KeyItem(String description, MapType type, String region, Color color)
    {
        checkParams(description, type, region, null, color);
        this.description = description;
        this.type = type;
        this.region = region;
        imagePath = null;
        this.color = color;
    }
    
    /**
     * Ensures all KeyItem construction parameters are valid.
     * 
     * @param description  The item description, which may not be null or
     *                     empty.
     * 
     * @param type         The item's map type, which may not be null.
     * 
     * @param region       The item's region, which may not be null or empty.
     * 
     * @param imagePath    The item's image path, which may only be null if the
     *                     color is not null.
     * 
     * @param color        The item's display color, which may only be null if
     *                     the imagePath is not null or empty.
     */
    private void checkParams(String description, MapType type, String region,
            String imagePath, Color color)
    {
        ExtendedValidate.notNullOrEmpty(description, "Key description");
        Validate.notNull(type, "Map type cannot be null.");
        ExtendedValidate.notNullOrEmpty(region, "Region name");
        if (color == null)
        {
            Validate.notNull(imagePath, "Image path cannot be null unless a "
                    + "color is provided.");
            Validate.notEmpty(imagePath, "Image path cannot be null unless a "
                    + "color is provided.");
        }
        else if (imagePath == null)
        {
            Validate.notNull(color, "Color cannot be null unles an image path"
                    + " is provided.");
        }
    }
    
    // Keys used when exporting KeyItems as JSON data:
    private class JsonKeys
    {
        public static final String NAME = "name";
        public static final String TYPE = "type";
        public static final String REGION = "region";
        public static final String IMAGE = "image";
        public static final String COLOR = "color";
    }
    
    /**
     * Exports this KeyItem within a JSON object.
     * 
     * @return  All key item data, embedded within a JSON object.
     */
    public JsonObject toJson()
    {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add(JsonKeys.NAME, description);
        builder.add(JsonKeys.TYPE, type.toString());
        builder.add(JsonKeys.REGION, region);
        if (imagePath != null)
        {
            builder.add(JsonKeys.IMAGE, imagePath);
        }
        if (color != null)
        {
            Function<Integer, String> hexComponent = (intVal) ->
            {
                String hex = Integer.toHexString(intVal);
                if (hex.length() == 1)
                {
                    return "0" + hex;
                }
                return hex;
            };
            String colorString = hexComponent.apply(color.getRed())
                    + hexComponent.apply(color.getGreen())
                    + hexComponent.apply(color.getBlue());
            builder.add(JsonKeys.COLOR, colorString);
        }
        return builder.build();
    }
    
    private final String description;
    private final MapType type;
    private final String region;
    private final String imagePath;
    private final Color color;
}
