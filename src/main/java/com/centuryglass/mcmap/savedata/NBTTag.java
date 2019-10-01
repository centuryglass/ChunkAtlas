/**
 * @file NBTTag.java
 *
 *  Lists NBT tag types.
 */
package com.centuryglass.mcmap.savedata;

/**
 * Data tags found within Minecraft NBT files.
 */
enum NBTTag
{
    /**
     * Marks the end of a compound data structure.
     */
    END,
    /**
     * Marks a single stored byte. 
     */
    BYTE,
    /**
     * Marks a two-byte short integer value.
     */
    SHORT,
    /**
     * Marks a four-byte integer value.
     */
    INT,
    /**
     * Marks an eight-byte long integer value.
     */
    LONG,
    /**
     * Marks a four-byte floating point value.
     */
    FLOAT,
    /**
     * Marks an eight-byte double precision floating point value.
     */
    DOUBLE,
    /**
     * Marks an array of byte values.
     */
    BYTE_ARRAY,
    /**
     * Marks a string of characters.
     */
    STRING,
    /**
     * Marks a list of values sharing a single type.
     */
    LIST,
    /**
     * Marks a compound data structure holding key/value pairs.
     */
    COMPOUND,
    /**
     * Marks an array of four-byte integer values.
     */
    INT_ARRAY,
    /**
     * Marks an array of eight-byte long integer values.
     */
    LONG_ARRAY;
}
