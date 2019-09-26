/**
 * @file ArgParser.java
 * 
 * Processes a set of command line arguments.
 */

package com.centuryglass.mcmap.util.args;

import com.centuryglass.mcmap.util.Pair;
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
    protected ArgParser(Map<ArgEnum, ArgOption<ArgEnum>> optionData)
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
            
            ArgOption<ArgEnum> option = optionData.get(value);
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
                throw new InvalidArgumentException("Invalid argument option "
                        + args[i]);
            }
            ArgOption<ArgEnum> option = optionFlags.get(args[i]);
            if (option.paramCount == 0)
            {
                optionValues.put(option.type, null);
            }
            else
            {
                if ((i + option.paramCount) >= args.length)
                {
                    throw new InvalidArgumentException("Option "
                            + option.type.toString() + ": expected "
                            + String.valueOf(option.paramCount)
                            + " parameters, found "
                            + String.valueOf(args.length - i - 1) + ".");
                }
                String[] params = Arrays.copyOfRange(args, i + 1,
                        i + 1 + option.paramCount);
                optionValues.put(option.type, params);
                i += option.paramCount;
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
     * @return            The list of arguments given for the selected option,
     *                    or null if the option was not present in the argument
     *                    list or does not use any parameter arguments.
     */
    public String[] getOptionParams(ArgEnum optionType)
    {
        return optionValues.get(optionType);
    }
    
    /**
     * Gets all command line options selected, paired with any required
     * parameters.
     * 
     * @return  An array of pairs, each holding an encountered option type, and
     *          the parameters given with that option type.
     */
    public Pair<ArgEnum, String[]>[] getAllOptions()
    {
        Pair<ArgEnum, String[]>[] options = new Pair[optionValues.size()];
        int i = 0;
        for (Map.Entry<ArgEnum, String[]> entry : optionValues.entrySet())
        {
            String[] params = entry.getValue();
            if (params != null)
            {
                params = Arrays.copyOf(params, params.length);
            }
            options[i] = new Pair(entry.getKey(), params);
            i++;
        }
        return options;
    }

    private final Map<String, ArgOption<ArgEnum>> optionFlags;
    private final Map<ArgEnum, String[]> optionValues;
    private final String helpText;
}
