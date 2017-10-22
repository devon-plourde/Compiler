package compError.SemanticAnalyzer;

import compError.CompError;

/**
 * Error that represents a couple of different types of Main related errors.
 * @author Devon Plourde
 *
 */
public class MainError extends CompError
{
	private String errMsg = "Main Error | ";
	
	public MainError(MainErrorEnum e){
		super("0", 0);
		switch(e)
		{
		case MISSING:
				errMsg += "Main method was not detected. Main must be the final function declaration.";
				break;
		case PARAMETER:
				errMsg += "Main method must have parameter VOID.";
				break;
		case RETURN:
				errMsg += "Return type of main must be INT.";
		}
	}
	
	public String toString()
	{
		return errMsg;
	}
}
