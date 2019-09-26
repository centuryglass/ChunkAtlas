/**
 * @file InvalidArgumentException.java
 * 
 * Represents an invalid command line argument value, encountered either when
 * creating an ArgHandler or handling arguments.
 */
package com.centuryglass.mcmap.util.args;

/**
 * Represents an invalid command line argument value, encountered either when
 * creating an ArgHandler or handling arguments.
 */
public class InvalidArgumentException extends Exception
{
    /**
     * Saves the error message on construction.
     * 
     * @param message  A short message describing the error.
     */
    protected InvalidArgumentException(String message)
    {
        super("Invalid command line argument value: " + message);
    }
}
