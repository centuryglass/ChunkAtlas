/**
 * @file ArgOption.java
 * 
 * Manages parameters passed to a command line option.
 */
package com.centuryglass.chunk_atlas.util.args;

import java.util.function.Predicate;
import org.apache.commons.lang.Validate;

/**
 * Represents an option found in a list of command line arguments, possibly
 * with a set of parameter strings.
 * 
 * @param <ArgEnum>  An enumerator specifying all argument types.
 */
public class ArgOption<ArgEnum>
{
    /**
     * Stores an encountered command line option with its parameter strings.
     * 
     * @param optionType  The type of option encountered.
     * 
     * @param parameters  All parameter strings associated with the option.
     */
    protected ArgOption(ArgEnum optionType, String[] parameters)
    {
        Validate.notNull(optionType, "Option type cannot be null.");
        this.optionType = optionType;
        this.parameters = parameters;
    }
    
    /**
     * Gets the type of command line option this object represents.
     * 
     * @return  The option's type. 
     */
    public ArgEnum getType() 
    {
        return optionType;
    }
    
    /**
     * Gets the number of parameters associated with this option.
     * 
     * @return  The number of stored parameter strings. 
     */
    public int getParamCount()
    {
        if (parameters == null)
        {
            return 0;
        }
        return parameters.length;
    }
    
    /**
     * Gets one of this option's parameters.
     * 
     * @param index  A valid parameter index.
     * 
     * @return       The parameter at that index.
     */
    public String getParameter(int index)
    {
        if (parameters == null || index < 0 || index >= parameters.length)
        {
            throw new IndexOutOfBoundsException("Invalid parameter index "
                    + String.valueOf(index) + " to command line option "
                    + optionType.toString() + " with "
                    + String.valueOf(parameters.length) + " parameters.");
        }
        return parameters[index];
    }
    
    /**
     * Represents possible outcomes when checking for a boolean value in a
     * parameter.
     */
    public enum BoolStatus
    {
        TRUE,
        FALSE,
        NOT_BOOLEAN; 
    }
    
    /**
     * Attempts to parse an integer value from an option parameter.
     * 
     * @param index                      The index of the parameter to read.
     * 
     * @param validator                  An optional Predicate used to add
     *                                   additional requirements that the
     *                                   number parameter must meet.
     * 
     * @return                           The numeric value of the chosen
     *                                   parameter.
     * 
     * @throws IllegalArgumentException  If the index is invalid, the parameter
     *                                   is non-numeric, or the value is
     *                                   rejected by the validator.
     */
    public int parseIntParam(int index, Predicate<Integer> validator)
            throws IllegalArgumentException
    {
        if (getParamCount() <= index || index < 0)
        {
            throw new IllegalArgumentException("Option " + getType().toString()
                    + ": Parameter index " + String.valueOf(index)
                    + " does not exist");
        } 
        String param = getParameter(index);
        try
        {
            int value = Integer.parseInt(param);
            if (validator != null && ! validator.test(value))
            {
                throw new NumberFormatException();
            }
            return value;
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Option " + getType().toString()
                    + ": Invalid parameter \"" + param + "\".");
        }
    }
    
    /**
     * Checks what the value of this option is if it represents a boolean
     * value.
     * 
     * Boolean options either have no arguments, in which case they are always
     * true, or their first argument is a true/false value.
     * 
     * @return                           The option's boolean value.
     * 
     * @throws IllegalArgumentException  If the option has a first argument
     *                                   that does not represent a boolean.
     */
    public boolean boolOptionStatus() throws IllegalArgumentException
    {
        if (parameters == null || parameters.length == 0)
        {
            return true;
        }
        String param = getParameter(0);
        if (param.equals("1") || param.equalsIgnoreCase("true"))
        {
            return true;
        }
        if (param.equals("0") || param.equalsIgnoreCase("false"))
        {
            return false;
        }
        throw new IllegalArgumentException(optionType.toString()
                + ": parameter \"" + param + "\" is not true/false.");
    }
    
    private final ArgEnum optionType;
    private final String[] parameters;
}
