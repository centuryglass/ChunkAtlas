/**
 * @file TickDuration.java
 * 
 * Divides a duration expressed in Minecraft ticks into other time units.
 */
package com.centuryglass.mcmap;

import java.util.function.BiFunction;

public class TickDuration
{
    /**
     * Creates a TickDuration object from a specific number of ticks.
     * 
     * @param ticks  The number of tick units represented by the duration.
     */
    public TickDuration(long ticks)
    {
   
        asTicks = ticks;
        asSeconds = (double) asTicks / 20;
        asMinutes = asSeconds / 60;
        asHours = asMinutes / 60;
        asDays = asHours / 24;
        asWeeks = asDays / 7;
        asYears = asDays / 365;
        
        this.ticks = ticks % 20;
        seconds = ((long) asSeconds) % 60;
        minutes = ((long) asMinutes) % 60;
        hours = ((long) asHours) % 24;
        days = ((long) asDays) % 7;
        weeks = ((long) asWeeks) % 53;
        years = (long) asYears;
    }
    
    /**
     * Different modes to use when rounding tick durations:
     */
    public enum Rounding
    {
        /**
         * Rounds up to the largest time unit integer.
         */
        UP,
        /**
         * Rounds down to the largest time unit integer.
         */
        DOWN,
        /**
         * Rounds to the nearest large time unit integer.
         */
        NEAREST
    }
    
    /**
     * Gets a copy of this TickDuration, rounded using a configurable rounding
     * mode.
     * 
     * @param roundingType  The direction to round the TickDuration's largest
     *                      unit.
     * 
     * @return              The new TickDuration.
     */
    public TickDuration rounded(Rounding roundingType)
    {
        final long[] unitDurations =
        {
            TickDuration.fromYears(1).asTicks,
            TickDuration.fromWeeks(1).asTicks,
            TickDuration.fromDays(1).asTicks,
            TickDuration.fromHours(1).asTicks,
            TickDuration.fromMinutes(1).asTicks,
            TickDuration.fromSeconds(1).asTicks,
            1
        };
        for (long unitTicks : unitDurations)
        {
            if ((asTicks % unitTicks) == 0) 
            {
                return new TickDuration(asTicks);
            }
            else if (asTicks > unitTicks)
            {
                long remainder = asTicks % unitTicks;
                long roundedDown = asTicks - remainder;
                switch (roundingType)
                {
                    case DOWN:
                        return new TickDuration(roundedDown);
                    case NEAREST:
                        if (remainder < (unitTicks / 2))
                        {
                            return new TickDuration(roundedDown);
                        }
                    case UP:
                        return new TickDuration(roundedDown + unitTicks);
                }
            }
        }
        return new TickDuration(0);
    }
    
    /**
     * Returns a TickDuration object representing a specific number of seconds.
     * 
     * @param seconds  The number of seconds to represent.
     * 
     * @return         The TickDuration representing that period. 
     */
    public static TickDuration fromSeconds(long seconds)
    {
        return new TickDuration(seconds * 20);
    }
      
    /**
     * Returns a TickDuration object representing a specific number of minutes.
     * 
     * @param minutes  The number of minutes to represent.
     * 
     * @return         The TickDuration representing that period. 
     */
    public static TickDuration fromMinutes(long minutes)
    {
        return TickDuration.fromSeconds(minutes * 60);
    }
    
    /**
     * Returns a TickDuration object representing a specific number of hours.
     * 
     * @param hours  The number of hours to represent.
     * 
     * @return       The TickDuration representing that period. 
     */
    public static TickDuration fromHours(long hours)
    {
        return TickDuration.fromMinutes(hours * 60);
    }
    
    /**
     * Returns a TickDuration object representing a specific number of days.
     * 
     * @param days  The number of days to represent.
     * 
     * @return      The TickDuration representing that period. 
     */
    public static TickDuration fromDays(long days)
    {
        return TickDuration.fromHours(days * 24);
    }    
    
    /**
     * Returns a TickDuration object representing a specific number of weeks.
     * 
     * @param weeks  The number of weeks to represent.
     * 
     * @return       The TickDuration representing that period. 
     */
    public static TickDuration fromWeeks(long weeks)
    {
        return TickDuration.fromDays(weeks * 7);
    }    
    
    /**
     * Returns a TickDuration object representing a specific number of years.
     * 
     * @param years  The number of years to represent.
     * 
     * @return       The TickDuration representing that period. 
     */
    public static TickDuration fromYears(long years)
    {
        return TickDuration.fromDays(years * 365);
    }

    /**
     * The number of ticks remaining after dividing the duration between all
     * time units
     */
    public final long ticks;

    /**
     * The number of seconds remaining after dividing the duration between all
     * time units.
     */
    public final long seconds;
    
    /**
     * The number of minutes remaining after dividing the duration between all
     * time units.
     */
    public final long minutes;
       
    /**
     * The number of minutes remaining after dividing the duration between all
     * time units.
     */
    public final long hours;
       
    /**
     * The number of days remaining after dividing the duration between all time
     * units.
     */
    public final long days;
       
    /**
     * The number of weeks remaining after dividing the duration between all
     * time units.
     */
    public final long weeks;
       
    /**
     * The number of years remaining after dividing the duration between all time
     * units.
     */
    public final long years;
    
    /**
     * The entire duration, expressed in Minecraft tick units.
     */
    public final long asTicks;
    
    /**
     * The entire duration, expressed in seconds.
     */
    public final double asSeconds;
    
    /**
     * The entire duration, expressed in minutes.
     */
    public final double asMinutes;
    
    /**
     * The entire duration, expressed in hours.
     */
    public final double asHours;
    
    /**
     * The entire duration, expressed in days.
     */
    public final double asDays;
    
    /**
     * The entire duration, expressed in weeks.
     */
    public final double asWeeks;
    
    /**
     * The entire duration, expressed in years.
     */
    public final double asYears;
    
    /**
     * Gets a string representation of the duration, intended for console
     * output.
     * 
     * @return  The duration, divided into days, hours, minutes, seconds, 
     *          and ticks.
     */
    @Override
    public String toString()
    {
        if (asTicks == 0)
        {
            return "0 ticks";
        }
        BiFunction<Long, String, String> unitString = (value, unitName) -> 
        {
            if (value < 1) { return ""; }
            return String.valueOf(value) + " " + ((value == 1) ? unitName 
                    : (unitName + "s"));
        };
        BiFunction<String, String, String> append = (string, toAppend) ->
        {
            if (toAppend.isEmpty())
            {
                return string;
            }
            if (string.isEmpty())
            {
                return toAppend;
            }
            return string + ", " + toAppend;
            
        };
        String strValue = "";
        strValue = append.apply(strValue, unitString.apply(years, "year"));
        strValue = append.apply(strValue, unitString.apply(weeks, "week"));
        strValue = append.apply(strValue, unitString.apply(days, "day"));
        strValue = append.apply(strValue, unitString.apply(hours, "hour"));
        strValue = append.apply(strValue, unitString.apply(minutes, "minute"));
        strValue = append.apply(strValue, unitString.apply(seconds, "second"));
        strValue = append.apply(strValue, unitString.apply(ticks, "tick"));
        return strValue;
    }
}
