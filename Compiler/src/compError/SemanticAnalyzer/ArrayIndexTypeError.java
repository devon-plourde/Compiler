package compError.SemanticAnalyzer;

import compError.CompError;

/**
 * An error that occurs when the size field in an array declaration is not an Int.
 * @author Bryan
 *
 */
public class ArrayIndexTypeError extends CompError {

	String id;
	
	public ArrayIndexTypeError(String lineText, int lineNumber, String id) {
		super(lineText, lineNumber);
		this.id = id;
	}
	
	public String toString(){
		return "Array Index Type Error | Array: '"+this.id+"' has index value that is not an INT starting on Line "+this.lineNum+":\n\n"
				+lineText;
	}

}
