/**
 * @file ArgParser.java
 * 
 * Processes a set of command line arguments.
 */

package com.centuryglass.mcmap.util.args;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a handler to manage a set of command line arguments.
 * 
 * @param <ArgEnum>  An Enum storing all possible argument types.
 */
public class ArgParser<ArgEnum extends Enum<ArgEnum>>
{
    /**
     * Parses and sorts argument options on construction.
     * 
     * @param optionData                 An object mapping each command line
     *                                   option type to an object that controls
     *                                   its properties.
     */
    protected ArgParser(Map<ArgEnum, OptionParams<ArgEnum>> optionData)
    {
        // Confirm data is provided for all options, map option flags, and
        // collect help text:
        optionFlags = new HashMap();
        String combinedHelpText = "";
        
        assert (optionData != null && ! optionData.isEmpty());
        ArgEnum arbitraryType = optionData.keySet().iterator().next();
        Class<ArgEnum> enumClass = (Class<ArgEnum>) arbitraryType.getClass();
        for (ArgEnum value : enumClass.getEnumConstants())
        {
            // All options must be defined:
            assert (optionData.containsKey(value));
            
            OptionParams<ArgEnum> option = optionData.get(value);
            combinedHelpText = combinedHelpText + "  " + option.getHelpText()
                    + "\n";
            String[] flags = option.getFlags();
            for (String flag : flags)
            {
                // Ensure flags are unique:
                assert (! optionFlags.containsKey(flag));
                
                optionFlags.put(flag, option);
            }
        }
        helpText = combinedHelpText;
        optionValues = new HashMap();
    }
    
    /**
     * Parses a set of command line arguments, finding option types and saving
     * option parameters.
     * 
     * @param args                       The set of command line arguments to
     *                                   process.
     * 
     * @throws InvalidArgumentException  If invalid option flags are found in
     *                                   the argument list, or expected option
     *                                   parameters are missing.
     */
    public void parseArguments(String[] args) throws InvalidArgumentException
    {    
        // Scan arguments for options and save option parameters:
        for(int i = 0; i < args.length; i++)
        {
            if (! optionFlags.containsKey(args[i]))
            {
                throw new InvalidArgumentException("Invalid option "
                        + args[i]);
            }
            OptionParams<ArgEnum> option = optionFlags.get(args[i]);
            if (option.maxParamCount == 0)
            {
                optionValues.put(option.getType(), null);
            }
            else
            {
                // Ensure the expected number of params was found. Option flags
                // cannot be valid parameters.
                int maxParamIdx = Math.min(i + option.maxParamCount,
                        args.length - 1);
                for (int pI = i + 1; pI <= maxParamIdx; pI++)
                {
                    if (optionFlags.containsKey(args[pI]))
                    {
                        maxParamIdx = pI - 1;
                        break;
                    }
                }
                int paramsFound = maxParamIdx - i;
                if (paramsFound < option.minParamCount)
                {
                    throw new InvalidArgumentException("Option "
                            + option.getType().toString()
                            + ": expected at least "
                            + String.valueOf(option.minParamCount)
                            + " parameters, found "
                            + String.valueOf(paramsFound) + ".");
                }
                String[] params = Arrays.copyOfRange(args, i + 1,
                        i + 1 + paramsFound);
                optionValues.put(option.getType(),
                        new ArgOption(option.getType(), params));
                i += paramsFound;
            }
        }
    }
    
    /**
     * Gets help text describing all argument options.
     * 
     * @return  Argument option help text, generated on construction. 
     */
    public String getHelpText()
    { 
        return helpText;
    }
    
    /**
     * Checks if a specific option was present in the argument list.
     * 
     * @param optionType  A command line option type.
     * 
     * @return            Whether that option was found. 
     */
    public boolean optionFound(ArgEnum optionType)
    {
        return optionValues.containsKey(optionType);
    }
    
    /**
     * Gets the parameter arguments provided for a particular command line
     * option.
     * 
     * @param optionType  A command line option type.
     * 
     * @return            The object holding an option and its associated
     *                    parameters, or null if the option was not found.
     */
    public ArgOption<ArgEnum> getOptionParams(ArgEnum optionType)
    {
        return optionValues.get(optionType);
    }
    
    /**
     * Gets all command line options selected, paired with any required
     * parameters.
     * 
     * @return  An array of objects holding detected options and their 
     *          parameters.
     */
    public ArgOption<ArgEnum>[] getAllOptions()
    {
        ArgOption<ArgEnum>[] options = new ArgOption[optionValues.size()];
        int idx = 0;
        for(Map.Entry<ArgEnum, ArgOption<ArgEnum>> e : optionValues.entrySet())
        {
            options[idx] = e.getValue();
            idx++;
        }
        return options;
    }

    private final Map<String, OptionParams<ArgEnum>> optionFlags;
    private final Map<ArgEnum, ArgOption<ArgEnum>> optionValues;
    private final String helpText;
}
