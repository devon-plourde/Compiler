package compError.SemanticAnalyzer;

import compScanner.token.Token;

/**
 * Error that occurs when a funciton is called but hasn't been defined
 * @author Bryan
 *
 */
public class UndefinedFunctionError extends UndefinedIdentifierError {

	public UndefinedFunctionError(String lineText, int lineNum, String id) {
		super(lineText, lineNum, id);
		// TODO Auto-generated constructor stub
	}

	public String toString(){
		return super.toString("Function");
	}
	
}
