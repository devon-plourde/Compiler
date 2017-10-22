package compError.SemanticAnalyzer;

import compScanner.token.Token;
/**
 * Error that occurs when an id is called without being declared first
 * @author Bryan
 *
 */
public class UndefinedVariableError extends UndefinedIdentifierError {

	public UndefinedVariableError(String lineText, int lineNum, String id) {
		super(lineText, lineNum, id);
		// TODO Auto-generated constructor stub
	}
	
	public String toString(){
		return super.toString("Variable");
	}

}
