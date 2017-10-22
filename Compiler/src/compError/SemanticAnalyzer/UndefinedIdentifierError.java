package compError.SemanticAnalyzer;

import compError.CompError;
import compScanner.token.Token;

/**
 * Parent class for undefined id's
 * @author Bryan
 *
 */
public abstract class UndefinedIdentifierError extends CompError {

	String id;
	
	public UndefinedIdentifierError(String lineText, int lineNum, String id){
		super(lineText, lineNum);
		this.id = id;
	}
	
	public String toString(String type){
		return "Undefined "+type+" Declaration | Function: "+this.id+" starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}
}
