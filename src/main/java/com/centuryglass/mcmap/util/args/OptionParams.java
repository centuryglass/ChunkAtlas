/**
 * @file OptionParams.java
 * 
 * Provides a container class for storing command line argument option data.
 */
package com.centuryglass.mcmap.util.args;

/**
 * Immutably stores the properties of a single command line argument type.
 * 
 * @param <ArgEnum>  An Enum type defining all possible argument options.
 */
public class OptionParams<ArgEnum>
{
    /**
     * Sets argument type information on construction.
     * 
     * @param type               An Enum value representing this argument
     *                           option type.
     * 
     * @param shortFlag          A short flag string (usually one hyphen and
     *                           one letter) used to select this argument
     *                           option type.
     * 
     * @param longFlag           A long flag string (usually two hyphens and
     *                           one or more dash-separated words) used to
     *                           select this argument option type.
     * 
     * @param minParamCount      The minimum number of additional parameter
     *                           arguments that this option will take.
     *                           Negative values are not valid, and will be
     *                           replaced with zero.
     * 
     * @param maxParamCount      The maximum number of additional parameter
     *                           arguments that this option may take.
     *                           Negative values are not valid, and will be
     *                           replaced with zero.
     * 
     * @param paramDescription   An optional description of this argument
     *                           option's parameters, used when printing
     *                           the expected argument option format.
     * 
     * @param description        A short description of this argument
     *                           option, used when printing help text.
     */
    protected OptionParams(ArgEnum type, String shortFlag, String longFlag,
            int minParamCount, int maxParamCount, String paramDescription,
            String description)
    {
        this.type = type;
        this.shortFlag = shortFlag;
        this.longFlag = longFlag;
        this.minParamCount = Math.max(0, minParamCount);
        this.maxParamCount = Math.max(0, maxParamCount);
        this.paramDescription = paramDescription;
        this.description = description;
    }

    /**
     * Gets the help text used to describe this option.
     * 
     * @return  The help text, listing option flags, describing parameters,
     *          and explaining the purpose of this option.
     */
    protected String getHelpText()
    {
        return "(" + shortFlag + " | " + longFlag + ") " + paramDescription
                + "\n\t" + description;
    }

    /**
     * Gets the flag strings used to select this option.
     * 
     * @return  An array holding the short and long option flag values.
     */
    protected String[] getFlags()
    {
        return new String[] { shortFlag, longFlag };
    }
        
    /**
     * Gets the type of command line option this object represents.
     * 
     * @return  The option's type. 
     */
    protected ArgEnum getType() 
    {
        return type;
    }
    
    /**
     * Gets the minimum number of parameters required by this option.
     * 
     * @return  The minimum parameter count. 
     */
    protected int getMinParamCount()
    {
        return minParamCount;
    }
    
    /**
     * Gets the maximum number of parameters this option may take.
     * 
     * @return  The maximum parameter count. 
     */
    protected int getMaxParamCount()
    {
        return maxParamCount;
    }

    // The argument option type this OptionParams object describes:
    private final ArgEnum type;
    // The minimum number of parameter arguments that the option expects:
    protected final int minParamCount;
    // The maximum number of parameter arguments that the option may take:
    protected final int maxParamCount;
    // The short flag string used to select this option:
    private final String shortFlag;
    // The long flag string used to select this option:
    private final String longFlag;
    // A string used to represent this option's parameters:
    private final String paramDescription;
    // A brief description of this option's purpose:
    private final String description;  
}
