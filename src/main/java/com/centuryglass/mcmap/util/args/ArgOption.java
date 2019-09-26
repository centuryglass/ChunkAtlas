/**
 * @file ArgOption.java
 * 
 * Holds parameters passed to a command line option.
 */
package com.centuryglass.mcmap.util.args;

/**
 * Represents an option found in a list of command line arguments, possibly
 * with a set of parameter strings.
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
    
    private final ArgEnum optionType;
    private final String[] parameters;
}
