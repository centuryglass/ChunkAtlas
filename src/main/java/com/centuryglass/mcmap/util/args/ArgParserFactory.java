/**
 * @file ArgParserFactory.java
 * 
 * Creates ArgParser objects to handle arbitrary command line argument option
 * sets.
 */

package com.centuryglass.mcmap.util.args;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates an ArgParser to read a set of possible command line argument option
 * types.
 * 
 * @param <ArgEnum>  An Enum declaring all permitted option types.
 */
public class ArgParserFactory<ArgEnum>
{
    public ArgParserFactory()
    {
        optionData = new HashMap();
    }
    
    /**
     * Sets the properties of an argument option type.
     * 
     * @param type               The type of option to define.
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
     *                           arguments that the option requires.
     *                           Negative values are not valid, and will be
     *                           replaced with zero.
     * 
     * @param maxParamCount      The maximum number of additional parameter
     *                           arguments that the option may take.
     *                           Negative values are not valid, and will be
     *                           replaced with zero.
     * 
     * @param paramDescription   An optional description of the argument
     *                           option's parameters, used when printing
     *                           the expected argument option format.
     * 
     * @param description        A short description of the argument
     *                           option, used when printing help text.
     */
    public void setOptionProperties(ArgEnum type, String shortFlag,
            String longFlag, int minParamCount, int maxParamCount,
            String paramDescription, String description)
    {
        optionData.put(type, new OptionParams(type, shortFlag, longFlag,
                minParamCount, maxParamCount, paramDescription, description));
    }
    
    /**
     * Creates a new ArgParser to process a set of command line arguments.
     * 
     * @return                           The ArgParser created to process
     *                                   arguments.
     */
    public ArgParser createParser()
    {
        return new ArgParser(optionData);
    }
    
    private final Map<ArgEnum, OptionParams> optionData; 
}
