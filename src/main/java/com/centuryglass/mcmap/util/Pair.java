/**
 * @file Pair.java
 * 
 * Holds two objects of any two types.
 */
package com.centuryglass.mcmap.util;

/**
 * Allows any two object types to be packaged together, similar to the std::pair
 * class in C++.
 * 
 * @param <FirstType>   The first item's type.
 * 
 * @param <SecondType>  The second item's type.
 */
public class Pair <FirstType, SecondType> 
{
    public FirstType first;
    public SecondType second;
    
    /**
     * Initializes both pair values on construction.
     * 
     * @param first   The first object.
     * 
     * @param second  The second object. 
     */
    public Pair(FirstType first, SecondType second)
    {
        this.first = first;
        this.second = second;
    }
    
    /**
     * Initializes the Pair with null values.
     */
    public Pair()
    {
        first = null;
        second = null;
    }
}
