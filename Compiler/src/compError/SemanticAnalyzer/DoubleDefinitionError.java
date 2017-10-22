package compError.SemanticAnalyzer;

import compError.CompError;

/**
 * Error that occurs when two declarations are found for the same id within the same scope.
 * @author Devon Plourde
 *
 */
public class DoubleDefinitionError extends CompError {
	
	private String id;


	public DoubleDefinitionError(String lineText, int lineNumber, String id) {
		super(lineText, lineNumber);
		this.id = id;
	}
	
	public String toString(){
		return "Double Definition Error | Double definition of identifier: "+id+" starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}
}
