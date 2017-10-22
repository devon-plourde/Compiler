package compError.SemanticAnalyzer;

import compError.CompError;

/**
 * Error that occurs when a function is missing a return statement.
 * @author Bryan Storie
 *
 */
public class MissingReturnError extends CompError {

	private String func;
	
	public MissingReturnError(String func) {
		super("", -1);
		this.func = func;
	}
	
	public String toString(){
		return "Missing Return Error | Function '"+this.func+"' is missing a return statement";
	}

}
