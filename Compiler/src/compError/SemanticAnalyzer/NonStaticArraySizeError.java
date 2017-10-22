package compError.SemanticAnalyzer;

import compError.CompError;

/**
 * Error that occurs when the size field of an array declaration isn't static (eg. contains ids)
 * @author Bryan Storie
 *
 */
public class NonStaticArraySizeError extends CompError {

	private String id;
	
	public NonStaticArraySizeError(String lineText, int lineNumber, String id) {
		super(lineText, lineNumber);
		this.id = id;
	}

	public String toString(){
		return "NonStatic Array Size Error | The size in the declaration of '"+this.id+"'contains nonstatic elements(eg.variables) starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}
}
