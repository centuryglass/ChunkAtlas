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
}
