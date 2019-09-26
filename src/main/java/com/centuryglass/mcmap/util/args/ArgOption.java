/**
 * @file ArgData.java
 * 
 * Provides a container class for storing command line argument option data.
 */
package com.centuryglass.mcmap.util.args;

/**
 * Immutably stores the properties of a single command line argument type.
 * 
 * @param <ArgEnum>  An Enum type defining all possible argument options.
 */
public class ArgOption<ArgEnum>
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
     * @param paramCount         The number of additional parameter
     *                           arguments that this option will take.
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
    protected ArgOption(ArgEnum type, String shortFlag, String longFlag,
            int paramCount, String paramDescription, String description)
    {
        this.type = type;
        this.shortFlag = shortFlag;
        this.longFlag = longFlag;
        this.paramCount = Math.max(0, paramCount);
        this.paramDescription = paramDescription;
        this.description = description;
    }

    /**
     * Gets the help text used to describe this option.
     * 
     * @return  The help text, listing option flags, describing parameters,
     *          and explaining the purpose of this option.
     */
    public String getHelpText()
    {
        return "[" + shortFlag + " | " + longFlag + "]" + paramDescription
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
     * The argument option type this ArgOption object describes.
     */
    public final ArgEnum type;

    /**
     * The number of parameter arguments that the option expects.
     */
    protected final int paramCount;

    private final String shortFlag;
    private final String longFlag;
    private final String paramDescription;
    private final String description;  
}
