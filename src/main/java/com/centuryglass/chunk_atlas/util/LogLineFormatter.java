/**
 * @file LogLineFormatter.java
 * 
 * Formats log messages similarly to SimpleFormatter, but more tersely.
 */
package com.centuryglass.chunk_atlas.util;

import com.centuryglass.chunk_atlas.Main;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Formats log messages within a single line, adding log level, message time,
 * and the source class and method.
 * 
 * At Level.INFO, source class/method are omitted, as they're only relevant
 * when debugging program execution or locating errors.
 */
public class LogLineFormatter extends Formatter
{
    // Date/Time format used, generated using the pattern rules described in
    // the DateTimeFormatter documentation.
    private static final DateTimeFormatter TIMESTAMP_FORMAT
            = DateTimeFormatter.ofPattern("dd'/'MM'/'uu HH':'mm':'ss':' ");
    // Main application package name, to be omitted from logged class names:
    private static final String APP_PKG_PREFIX
            = Main.class.getPackage().getName() + ".";
    
    public LogLineFormatter()
    {
    }
    
    @Override
    public String format(LogRecord record)
    {
        final StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append(record.getLevel().getLocalizedName());
        lineBuilder.append(": ");
        Instant recordInstant = Instant.ofEpochMilli(record.getMillis());
        LocalDateTime recordDateTime = recordInstant.atZone(
                ZoneId.systemDefault()).toLocalDateTime();
        lineBuilder.append(TIMESTAMP_FORMAT.format(recordDateTime));
        // Add class:method if not at Level.INFO:
        if (record.getLevel() != Level.INFO)
        {
            if (record.getSourceClassName().startsWith(APP_PKG_PREFIX))
            {
                lineBuilder.append(record.getSourceClassName().substring(
                        APP_PKG_PREFIX.length()));
            }
            else
            {
                lineBuilder.append(record.getSourceClassName());
            }
            lineBuilder.append(":");
            lineBuilder.append(record.getSourceMethodName());
            lineBuilder.append(": ");
        }
        lineBuilder.append(record.getMessage());
        lineBuilder.append("\n");
        Object[] params = record.getParameters();
        if (params == null)
        {
            // Ensure that {0} is replaced by the empty string if parameters
            // happen to be null.
            params = new Object[] { "" };
        }
        for (int i = 0; i < params.length; i++)
        {
            String param = params[i].toString();
            String replaced = "{" + i + "}";
            // Track the end of the last replaced area just in case "{0}" gets
            // used as the first parameter, or something like that.
            int lastMatch = -1;  
            for (int matchIdx = lineBuilder.indexOf(replaced, lastMatch);
                    matchIdx != -1;
                    matchIdx = lineBuilder.indexOf(replaced, lastMatch))
            {
                lineBuilder.replace(matchIdx, matchIdx + replaced.length(),
                        param);
                lastMatch = matchIdx + param.length();
            }
        }
        if (record.getThrown() != null)
        {
            lineBuilder.append("\t");
            lineBuilder.append(record.getThrown().toString());
            lineBuilder.append("\n");
        }
        return lineBuilder.toString();
    }
    
}
