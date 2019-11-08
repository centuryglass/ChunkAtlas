/*
 * @file ColorRangeSet.java
 * 
 * Creates color representations of long values, using custom color ranges.
 */
package com.centuryglass.chunk_atlas.mapping.images;

import com.centuryglass.chunk_atlas.config.LogConfig;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;

/**
 * Assigns color values over a range of long integer values.
 */
public final class ColorRangeSet
{
    private final String CLASSNAME = ColorRangeSet.class.getName();
    
    /**
     * Selects whether values in a range fade to black, or to the color of the
     * next range.
     */
    public enum FadeType
    {
        /**
         * Range Colors are between the Range's color and black.
         */
        TO_BLACK,
        /**
         * Range Colors are between the Range's color and the color of the next
         * highest Range.
         */
        TO_NEXT;
    }
    
    /**
     * Represents a range of long values mapped over a color gradient. Ranges
     * are immutable.
     */
    public class Range
    {
        /**
         * @param maxValue  The largest value this range may contain.
         * 
         * @param minValue  The smallest value this range may contain.
         * 
         * @param maxColor  The color mapped to this range's largest value.
         * 
         * @param minColor  The color mapped to this range's smallest value.
         */
        protected Range(long maxValue, long minValue, Color maxColor,
                Color minColor)
        {
            Validate.isTrue(maxValue >= minValue,
                    "Minimum must not exceed maximum, but maximum = "
                    + maxValue + " and minimum = " + minValue + ".");
            Validate.notNull(maxColor, "Max. color cannot be null.");
            Validate.notNull(minColor, "Min. color cannot be null.");
            this.maxValue = maxValue;
            this.minValue = minValue;
            this.maxColor = maxColor;
            this.minColor = minColor;
        }
        
        /**
         * The largest value contained by this Range.
         */
        public final long maxValue;
        
        /**
         * The smallest value contained by this Range.
         */
        public final long minValue;
        
        /**
         * The color that this Range maps to its maximum value. 
         */
        public final Color maxColor;
        
        /**
         * The color that this Range maps to its minimum value. 
         */
        public final Color minColor;
    }
    
    /**
     * Constructs a ColorRangeSet with no initial values.
     */
    public ColorRangeSet()
    {
        ranges = new ArrayList<>();
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
        Validate.notNull(color, "Range color cannot be null.");
        Validate.notNull(fadeType, "Fade type cannot be null.");
        Validate.isTrue(maxFade >= 0 && maxFade <= 1,
                "Minimum color intensity must be between zero and one"
                + " inclusive, but maxFade = " + maxFade);
        ranges = new ArrayList<>();
        addColorRange(maxValue, color, fadeType, maxFade);
    }
    
    /**
     * Adds a new color range to the range set.
     * 
     * @param maxValue  The maximum value within the new range. If this value
     *                  already exists in the color range set, the resulting
     *                  behavior is undefined.
     * 
     * @param color     The color used to represent values within the new
     *                  range.
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
        ranges.forEach((internalRange) ->
        {
            Validate.isTrue(internalRange.value != maxValue,
                    "The range with max value " + maxValue
                    + " has already been added.");
            
        });
        Validate.notNull(color, "Range color cannot be null.");
        Validate.notNull(fadeType, "Fade type cannot be null.");
        Validate.isTrue(maxFade >= 0 && maxFade <= 1,
                "Minimum color intensity must be between zero and one"
                + " inclusive, but maxFade = " + maxFade);
        ranges.add(new InternalRange(maxValue, color, fadeType, maxFade));
        Collections.sort(ranges);
    }
    
    /**
     * Gets the color this range set uses to represent a value.
     * 
     * @param value  A value within the range set.
     * 
     * @return       The appropriate color in the range set, or the highest
     *               range's color if the value exceeds the highest range
     *               value, or null if the RangeSet contains no ranges.
     */
    public Color getValueColor(long value)
    {
        final String FN_NAME = "getValueColor";
        for (int i = 0; i < ranges.size(); i++)
        {
            final InternalRange range = ranges.get(i);
            if (value > range.value)
            {
                return range.color;
            }
            final InternalRange nextRange;
            if (i < (ranges.size() - 1))
            {
                nextRange = ranges.get(i + 1);
            }
            else
            {
                long minValue = Math.min(0, range.value);
                minValue = Math.min(minValue, value - 1);
                nextRange = new InternalRange(minValue, Color.BLACK,
                        FadeType.TO_BLACK, 0);
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
        LogConfig.getLogger().logp(Level.WARNING, CLASSNAME, FN_NAME,
                "Failed to find color for value {0} within {1} ranges.",
                new Object[]{value, ranges.size()});
        return null;
    }
    
    /**
     * Gets a list of ordered color ranges held by this RangeSet.
     * 
     * @return  A new array holding Range objects representing each color
     *          range, or an empty array if no ranges are set.
     */
    public Range[] getRanges()
    {
        if (ranges.isEmpty())
        {
            return new Range[0];
        }
        final Range[] rangeList = new Range[ranges.size()];
        final int lastIdx = ranges.size() - 1;
        for (int i = 0; i <= lastIdx; i++)
        {
            final InternalRange range = ranges.get(i);
            Color minColor = range.getRangeEndColor();
            if (minColor == null)
            {
                final Color nextColor = ((i == lastIdx) ? Color.BLACK
                        : ranges.get(i + 1).color);
                minColor = range.findRangeEndColor(nextColor);
            }
            long minValue = Math.min(0, range.value - 1);
            if (i != lastIdx)
            {
                minValue = ranges.get(i + 1).value + 1;
            }
            rangeList[i] = new Range(range.value, minValue, range.color,
                    minColor);
        }
        return rangeList;
    }
    
    // Internal representation of color ranges:
    private class InternalRange implements Comparable<InternalRange>
    {
        /**
         * @param value     The maximum value within the range.
         * 
         * @param color     The color mapped to the maximum value.
         * 
         * @param fadeType  The method used to select the minimum Color.
         * 
         * @param maxFade   The fraction of the range's color that should
         *                  remain at the range's lowest value.
         */
        public InternalRange
        (long value, Color color, FadeType fadeType, double maxFade)
        {
            Validate.notNull(color, "Color cannot be null.");
            Validate.notNull(fadeType, "Fade type cannot be null.");
            Validate.isTrue(maxFade >= 0 && maxFade <= 1,
                "Minimum color intensity must be between zero and one"
                + " inclusive, but maxFade = " + maxFade);
            this.value = value;
            this.color = color;
            this.fadeType = fadeType;
            this.maxFade = maxFade;
            this.rangeEndColor = null;
        }
        
        /**
         * Orders ranges from highest to lowest.
         * 
         * @param otherRange  Another range to compare with this one.
         * 
         * @return            A negative value if this range comes first, a
         *                    positive value if the otherRange comes first or
         *                    zero if the ranges are equivalent.
         */
        @Override
        public int compareTo(InternalRange otherRange)
        {
            Validate.notNull(otherRange, "Ranges should never be null.");
            return (int) (otherRange.value - value);
        }
        
        /**
         * Gets the color mapped to the lowest value in the range.
         * 
         * @return  The lowest value's color. If not previously calculated by
         *          calling findRangeEndColor, this value will be null.
         */
        Color getRangeEndColor() { return rangeEndColor; }
        
        /**
         * Calculates and returns the color mapped to the range's lowest value.
         * This will save the calculated value internally, so that future calls
         * to getRangeEndColor will return the same Color.
         * 
         * @param nextColor  The color assigned to the next range's highest
         *                   value. If fadeType is TO_BLACK or nextColor is
         *                   null, this value will be ignored and Color.BLACK
         *                   will be used instead.
         * 
         * @return           The color used by this range's lowest value.
         */
        Color findRangeEndColor(Color nextColor)
        {            
            int minR, minG, minB;
            if (fadeType == FadeType.TO_BLACK || nextColor == null)
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
        // Maximum value contained within the range:
        public final long value;
        // The color mapped to the maximum value:
        public final Color color;
        public final FadeType fadeType;
        public final double maxFade;
        private Color rangeEndColor;
    }
    
    // Holds all internal ranges:
    private final ArrayList<InternalRange> ranges; 
}
