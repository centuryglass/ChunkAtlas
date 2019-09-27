/**
 * @file ColorRangeFactory.java
 * 
 * Creates ColorRangeSets to represent specific collections of values.
 */
package com.centuryglass.mcmap.mapping.images;

import java.awt.Color;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * Creates ColorRangeSet objects for a specific collection of long integer
 * values.
 * 
 * By default, values are divided across color ranges so that each range
 * contains approximately the same number of values, and each range smoothly
 * transitions into the next range's color.
 */
public class ColorRangeFactory
{
    /**
     * Selects the way that color ranges will be distributed across the full
     * value set.
     */
    public enum DivisionType
    {
        /**
         * Ranges are selected so that each range contains approximately the
         * same number of values.
         */
        BY_COUNT,
        /**
         * Ranges are selected so that each range has approximately the same
         * distance between its upper and lower bounds. 
         */
        BY_VALUE;
    }
    
    /**
     * Sorts and saves the value set, and sets default options.
     * 
     * @param values  A Collection of values to be represented with color
     *                ranges.
     * 
     * @param colors  The color values to be used by each color range, ordered
     *                from highest to lowest. The size of this ArrayList 
     *                determines how many color ranges the ColorRangeSet will
     *                have.
     */
    public ColorRangeFactory(Collection<Long> values, ArrayList<Color> colors)
    {
        orderedValues = values.toArray(new Long[values.size()]);
        Arrays.sort(orderedValues, Collections.reverseOrder());
        this.colors = colors;
        rangeAdjuster = value -> value;
        divisionType = DivisionType.BY_COUNT;
        rangeFadeFractions = new double[colors.size()];
        rangeFadeTypes = new ColorRangeSet.FadeType[colors.size()];
        for (int i = 0; i < colors.size(); i++)
        {
            rangeFadeFractions[i] = 0;
            rangeFadeTypes[i] = ColorRangeSet.FadeType.TO_NEXT;
        }
    }
    
    /**
     * Sets the method used to divide the value list into ranges.
     * 
     * @param newType  The DivisionType that will be used.
     */
    public void setDivisionType(DivisionType newType)
    {
        divisionType = newType;
    }
    
    /**
     * Sets a function that will be used to adjust range values. This is
     * primarily intended as a way to round ranges to specific intervals.
     * 
     * @param adjuster  A function that, when applied to a potential upper range
     *                  value, returns the value that should actually be used as
     *                  the upper bound of the range.
     */
    public void setRangeAdjuster(Function<Long, Long> adjuster)
    {
        rangeAdjuster = adjuster;
    }
    
    /**
     * Sets the amount that each color range will fade from its maximum color
     * value to its minimum color value.
     * 
     * @param fraction  The color fraction. At each range's minimum value, its
     *                  color equals (fraction * maxColor) + ((1.0 - fraction)
     *                  * minColor). This value must be between zero and one, 
     *                  inclusive.
     */
    public void setFadeFraction(double fraction)
    {
        for (int i = 0; i < rangeFadeFractions.length; i++)
        {
            setFadeFraction(fraction, i);
        }
    }
    
    /**
     * Sets the amount that a color range will fade from its maximum color
     * value to its minimum color value.
     * 
     * @param fraction      The fraction of the max color value remaining at the
     *                      range's lower bounds.
     * 
     * @param rangeIndex    The index of the range within the ColorRangeSet that
     *                      will use that fraction value.
     */
    public void setFadeFraction(double fraction, int rangeIndex)
    {
        if (rangeIndex < 0 || rangeIndex >= rangeFadeFractions.length)
        {
            throw new InvalidParameterException("Invalid range index "
                    + String.valueOf(rangeIndex));
        }
        if (fraction < 0 || fraction > 1)
        {
            throw new InvalidParameterException("Invalid fade fraction "
                    + String.valueOf(fraction));
        }
        rangeFadeFractions[rangeIndex] = fraction;
    }
    
    /**
     * Sets how colors fade over each color range.
     * 
     * @param fadeType  Whether all ranges fade to black, or to the following
     *                  range color.
     */
    public void setFadeType(ColorRangeSet.FadeType fadeType)
    {
        for (int i = 0; i < rangeFadeTypes.length; i++)
        {
            setFadeType(fadeType, i);
        }
    }
    
    /**
     * Sets how colors fade over a specific color range.
     * 
     * @param fadeType    Whether the range fades to black, or to the following
     *                    range color.  
     * 
     * @param rangeIndex  The index of the range within the ColorRangeSet that
     *                    will use that FadeType.
     */
    public void setFadeType(ColorRangeSet.FadeType fadeType, int rangeIndex)
    {
        if (rangeIndex < 0 || rangeIndex >= rangeFadeTypes.length)
        {
            throw new InvalidParameterException("Invalid range index "
                    + String.valueOf(rangeIndex));
        }
        rangeFadeTypes[rangeIndex] = fadeType;
    }
    
    /**
     * Creates a ColorRangeSet spanning all values in the initial value set.
     * 
     * @return  The new ColorRangeSet, created using all options configured
     *          using other ColorRangeFactory methods.
     */
    public ColorRangeSet createColorRangeSet()
    {
        ColorRangeSet rangeSet = new ColorRangeSet();
        final int rangeCount = colors.size();
        long[] rangeMaxValues = new long[rangeCount];
        rangeMaxValues[0] = rangeAdjuster.apply(orderedValues[0]);
        
        if (divisionType == DivisionType.BY_COUNT)
        {
            for (int i = 1; i < rangeCount; i++)
            {
                final int valueIdx = orderedValues.length / rangeCount * i;
                rangeMaxValues[i]
                        = rangeAdjuster.apply(orderedValues[valueIdx]);
                // Discard adjustments if they would put ranges out of order:
                if (rangeMaxValues[i] >= rangeMaxValues[i - 1])
                {
                    rangeMaxValues[i] = orderedValues[valueIdx];
                }
            }      
        }
        else // divisionType == BY_VALUE
        {
            final long fullRange = orderedValues[0]
                    - orderedValues[orderedValues.length - 1];
            for (int i = 1; i < rangeCount; i++)
            {
                rangeMaxValues[i] = rangeAdjuster.apply(orderedValues[0]
                        - fullRange / rangeCount * i);
                assert rangeMaxValues[i] < rangeMaxValues[i - 1];
            }
        }
        
        for (int i = 0; i < rangeCount; i++)
        {
            rangeSet.addColorRange(rangeMaxValues[i], colors.get(i),
                    rangeFadeTypes[i], rangeFadeFractions[i]);
        }
        return rangeSet;
    }
    
    // All values, ordered from greatest to least:
    private final Long[] orderedValues;
    // Colors to apply to ranges, from highest to lowest range:
    private final ArrayList<Color> colors;
    // Function used to adjust the upper bounds of ranges:
    private Function<Long, Long> rangeAdjuster;
    // Method used for dividing the list of values into ranges:
    private DivisionType divisionType;
    // The fraction of each range's maximum color that should remain at the
    // range's minimum value:
    private final double[] rangeFadeFractions;
    // Method used by each color range to select its minimum color value:
    private final ColorRangeSet.FadeType[] rangeFadeTypes;
}
