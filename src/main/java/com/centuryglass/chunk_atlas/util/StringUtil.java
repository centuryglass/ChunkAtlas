/**
 * @file StringUtil.java
 * 
 * Defines miscellaneous shared string functions.
 */
package com.centuryglass.chunk_atlas.util;

import java.awt.Color;

public class StringUtil
{
    /**
     * Converts a color value to a six-digit hexadecimal color code.
     * 
     * @param color  The color value to convert.
     * 
     * @return       The color's six-digit RGB color code. 
     */
    public static String colorHex(Color color)
    {
        int[] components
                = { color.getRed(), color.getGreen(), color.getBlue() };
        StringBuilder colorBuilder = new StringBuilder();
        for (int comp : components)
        {
            if (comp < 0x10)
            {
                colorBuilder.append('0');
            }
            colorBuilder.append(Integer.toHexString(comp));
        }
        return colorBuilder.toString();
    }
    
    /**
     * Converts an enum value's string representation to an equivalent display
     * string.
     * 
     * @param enumValue  An enum value's name, formatted in all uppercase, words
     *                   separated by underscores.
     * 
     * @return           The same name with only the first letter of each word
     *                   capitalized, words separated by spaces.
     */
    public static String enumToDisplayString(String enumValue)
    {
        StringBuilder formattedName
                = new StringBuilder(enumValue.toLowerCase());
        for (int i = 0; i < formattedName.length(); i++)
        {
            if (formattedName.charAt(i) == '_')
            {
                formattedName.setCharAt(i, ' ');
            }
            else if (i == 0 || formattedName.charAt(i - 1) == ' ')
            {
                formattedName.setCharAt(i, Character.toUpperCase(
                        formattedName.charAt(i)));
            }
        }
        return formattedName.toString();
    }
}
