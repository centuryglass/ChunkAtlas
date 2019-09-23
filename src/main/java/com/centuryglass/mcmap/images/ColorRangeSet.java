/*
 * @file ColorRangeSet.java
 * 
 * Creates color representations of long values, using custom color ranges.
 */
package com.centuryglass.mcmap.images;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiFunction;

/**
 * Assigns color values over a range of long integer values.
 */
public final class ColorRangeSet
{
    /**
     * Selects whether values in a range fade to black, or to the color of the
     * next range.
     */
    public enum FadeType
    {
        TO_BLACK,
        TO_NEXT;
    }
    
    private class Range implements Comparable<Range>
    {
        public Range(long value, Color color, FadeType fadeType, double maxFade)
        {
            if (maxFade > 1 || maxFade < 0)
            {
                throw new IllegalArgumentException("maxFade must be between "
                        + "0 and 1.");   
            }
            this.value = value;
            this.color = color;
            this.fadeType = fadeType;
            this.maxFade = maxFade;
            this.rangeEndColor = null;
        }
        
        @Override
        public int compareTo(Range otherRange)
        {
            return (int) (otherRange.value - value);
        }
        
        Color getRangeEndColor() { return rangeEndColor; }
        
        Color findRangeEndColor(Color nextColor)
        {            
            int minR, minG, minB;
            if (fadeType == FadeType.TO_BLACK)
            {
                minR = 0;
                minG = 0;
                minB = 0;
            }
            else
            {
                minR = nextColor.getRed();
                minG = nextColor.getGreen();
                minB = nextColor.getBlue();
            }
            if (maxFade != 0)
            {
                double fadedStrength = 1.0 - maxFade;
                minR = (int) (color.getRed() * maxFade
                        + minR * fadedStrength);
                minG = (int) (color.getGreen() * maxFade
                        + minG * fadedStrength);
                minB = (int) (color.getBlue() * maxFade
                        + minB * fadedStrength);
            }
            rangeEndColor = new Color(minR, minG, minB);
            return rangeEndColor;
        }
        
        public final long value;
        public final Color color;
        public final FadeType fadeType;
        public final double maxFade;
        private Color rangeEndColor;
    }
    
    /**
     * Adds the first tracked range to a color range set on construction.
     * 
     * @param maxValue  The maximum value within the initial range.
     * 
     * @param color     The color used to represent values within the initial
     *                  range.
     * 
     * @param fadeType  The way that the color should change across the range.
     * 
     * @param maxFade   The minimum intensity of the range color over this
     *                  range. This value must be between zero and one,
     *                  inclusive.
     */
    public ColorRangeSet(long maxValue, Color color, FadeType fadeType,
            double maxFade)
    {
        ranges = new ArrayList();
        addColorRange(maxValue, color, fadeType, maxFade);
    }
    
    /**
     * Adds a new color range to the range set.
     * 
     * @param maxValue  The maximum value within the new range. If this value
     *                  already exists in the color range set, the resulting
     *                  behavior is undefined.
     * 
     * @param color     The color used to represent values within the new range.
     * 
     * @param fadeType  The way that the color should change across the range.
     * 
     * @param maxFade   The minimum intensity of the range color over this
     *                  range. This value must be between zero and one,
     *                  inclusive.
     */
    public void addColorRange(long maxValue, Color color, FadeType fadeType,
            double maxFade)
    {
        ranges.add(new Range(maxValue, color, fadeType, maxFade));
        Collections.sort(ranges);
    }
    
    /**
     * Gets the color this range set uses to represent a value.
     * 
     * @param value  A value within the range set.
     * 
     * @return       The appropriate color in the range set, or the highest
     *               range's color if the value exceeds the highest range value.
     */
    public Color getValueColor(long value)
    {
        for (int i = 0; i < ranges.size(); i++)
        {
            final Range range = ranges.get(i);
            if (value > range.value)
            {
                return range.color;
            }
            final Range nextRange;
            if (i < (ranges.size() - 1))
            {
                nextRange = ranges.get(i + 1);
            }
            else
            {
                long minValue = Math.min(0, range.value);
                minValue = Math.min(minValue, value - 1);
                nextRange = new Range(minValue, Color.BLACK, FadeType.TO_BLACK,
                        0);
            }
            if (value <= nextRange.value)
            {
                continue;
            }
            Color endColor = range.getRangeEndColor();
            if (endColor == null)
            {
                endColor = range.findRangeEndColor(nextRange.color);
            }
            final double colorStrength = (double) (value - nextRange.value)
                    / (double) (range.value - nextRange.value);
            final double endColorStrength = 1.0 - colorStrength;
            BiFunction<Integer, Integer, Integer> getComp
                    = (upper, lower) -> (int) (upper * colorStrength
                    + lower * endColorStrength);
            return new Color(
                    getComp.apply(range.color.getRed(), endColor.getRed()),
                    getComp.apply(range.color.getGreen(), endColor.getGreen()),
                    getComp.apply(range.color.getBlue(), endColor.getBlue()));
        }
        return null;
    }
    
    private ArrayList<Range> ranges; 
}
