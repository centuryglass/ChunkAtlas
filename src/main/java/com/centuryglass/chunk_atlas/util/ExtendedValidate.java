/**
 * @file ExtendedValidate.java
 * 
 * Provides additional validation functions for ensuring values meet expected
 * parameters.
 */
package com.centuryglass.chunk_atlas.util;

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.lang.Validate;

/**
 * Provides additional validation functions for ensuring values meet expected
 * parameters.
 */
public class ExtendedValidate
{
    /**
     * Tests that a file is not null.
     * 
     * @param file           The File object to validate.
     * 
     * @param messagePrefix  A string to print before the error message if the
     *                       file is null.
     */
    public static void fileNotNull(File file, String messagePrefix)
    {
        assert (messagePrefix != null);
        Validate.notNull(file, messagePrefix + " cannot be null.");
    }
    
    /**
     * Tests that a file object is not null, and it exists in the file system.
     * 
     * @param file                    The File object to validate.
     * 
     * @param messagePrefix           A string to print before the error
     *                                message if the file is null or not in the
     *                                file system.
     * 
     * @throws FileNotFoundException  If the file does not exist.
     */
    public static void fileExists(File file, String messagePrefix)
            throws FileNotFoundException
    {
        fileNotNull(file, messagePrefix);
        if (! file.exists())
        {
            throw new FileNotFoundException(messagePrefix + " path \""
                    + file.toString() 
                    + "\" does not exist on the file system.");
        }
    }
    
    /**
     * Tests that a File is not null, and could exist as a file.
     * 
     * @param file           The file to validate.
     * 
     * @param messagePrefix  The string to print before the error message if
     *                       the file is null or is a directory.
     */
    public static void couldBeFile(File file, String messagePrefix)
    {
        fileNotNull(file, messagePrefix);
        Validate.isTrue(! file.isDirectory(), messagePrefix
                + " at \"" + file.toString() + "\" cannot be a file, it is a "
                + "directory.");
    }
    
    /**
     * Tests that a File is not null, and could exist as a directory.
     * 
     * @param file           The file to validate.
     * 
     * @param messagePrefix  The string to print before the error message if
     *                       the file is null or exists as a non-directory
     *                       file.
     */
    public static void couldBeDirectory(File file, String messagePrefix)
    {
        fileNotNull(file, messagePrefix);
        Validate.isTrue(! file.isFile(), messagePrefix
                + " at \"" + file.toString() + "\" cannot be a directory, it "
                + "already exists as a file.");
    }
    
    /**
     * Tests that a file is not null and exists in the file system as a file.
     * 
     * @param file                    The file to validate.
     * 
     * @param messagePrefix           The string to print before the error
     *                                message if the file does not exist as a
     *                                non-directory file.
     * 
     * @throws FileNotFoundException  If the file does not exist.
     */
    public static void isFile(File file, String messagePrefix)
            throws FileNotFoundException
    {
        fileExists(file, messagePrefix);
        Validate.isTrue(file.isFile(), messagePrefix
                + " at \"" + file.toString() + "\" exists but is not a file.");
    }
    
    /**
     * Tests that a file is not null and exists in the file system as a
     * directory.
     * 
     * @param file                    The file to validate.
     * 
     * @param messagePrefix           The string to print before the error
     *                                message if the file does not exist as a
     *                                directory file.
     * 
     * @throws FileNotFoundException  If the file does not exist.
     */
    public static void isDirectory(File file, String messagePrefix)
            throws FileNotFoundException
    {
        fileExists(file, messagePrefix);
        Validate.isTrue(file.isDirectory(), messagePrefix
                + " at \"" + file.toString()
                + "\" exists but is not a directory.");
    }
    
    /**
     * Tests that an integer is greater than zero.
     * 
     * @param value          The integer to validate.
     * 
     * @param messagePrefix  The string to print before the error message if
     *                       the integer is not positive.
     */
    public static void isPositive(int value, String messagePrefix)
    {
        assert (messagePrefix != null);
        Validate.isTrue(value > 0, messagePrefix + " should be positive,"
                + "found " + String.valueOf(value) + ".");
    }
    
        
    /**
     * Tests that an integer is greater than or equal to zero.
     * 
     * @param value          The integer to validate.
     * 
     * @param messagePrefix  The string to print before the error message if
     *                       the integer is not positive.
     */
    public static void isNotNegative(int value, String messagePrefix)
    {
        assert (messagePrefix != null);
        Validate.isTrue(value >= 0, messagePrefix + " should not be negative,"
                + " found " + String.valueOf(value) + ".");
    }
    
    /**
     * Checks that a floating point value is within an expected set of
     * inclusive bounds.
     * 
     * @param value          The value to validate.
     * 
     * @param min            The lowest value that would be valid.
     * 
     * @param max            The highest value that would be valid.
     * 
     * @param messagePrefix  The string to print before the error message if
     *                       the value is not within the bounds.
     */
    public static void inInclusiveBounds(double value, double min,
            double max, String messagePrefix)
    {
        assert (messagePrefix != null);
        Validate.isTrue(value >= min && value <= max, messagePrefix
            + " should have been at least " + String.valueOf(min)
            + " and no more than " + String.valueOf(max) + ", found "
            + String.valueOf(value));
    }
 
    /**
     * Checks that an integer value is within an expected set of inclusive
     * bounds.
     * 
     * @param value          The value to validate.
     * 
     * @param min            The lowest value that would be valid.
     * 
     * @param max            The highest value that would be valid.
     * 
     * @param messagePrefix  The string to print before the error message if
     *                       the value is not within the bounds.
     */
    public static void inInclusiveBounds(int value, int min,
            int max, String messagePrefix)
    {
        assert (messagePrefix != null);
        Validate.isTrue(value >= min && value <= max, messagePrefix
            + " should have been at least " + String.valueOf(min)
            + " and no more than " + String.valueOf(max) + ", found "
            + String.valueOf(value));
    }
    
    /**
     * Checks that an index value is within the range of the container it will
     * access.
     * 
     * @param index          The index value to validate.
     * 
     * @param containerSize  The size of the container the index will access.
     * 
     * @param messagePrefix  The string to print before the error message if
     *                       the index is out of bounds.
     */
    public static void validIndex
    (int index, int containerSize, String messagePrefix)
    {
        assert (containerSize >= 0);
        if (index < 0 || index >= containerSize)
        {
            throw new IndexOutOfBoundsException(messagePrefix
                    + " out of bounds at " + String.valueOf(index)
                    + ", indexed container is size "
                    + String.valueOf(containerSize));
        }
    }
    
    /**
     * Checks that a string value is neither null nor empty.
     * 
     * @param value          The string value to test.
     * 
     * @param messagePrefix  The string to print before the error message if
     *                       the value is null or empty.
     */
    public static void notNullOrEmpty(String value, String messagePrefix)
    {
        assert (messagePrefix != null);
        Validate.notNull(value, messagePrefix + " cannot be null.");
        Validate.notEmpty(value, messagePrefix + " cannot be empty.");
    }
}
