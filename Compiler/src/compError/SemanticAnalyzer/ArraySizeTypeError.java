package compError.SemanticAnalyzer;

import compError.CompError;

/**
 * An error that occurs when the size field in an array declaration is not an Int.
 * @author Bryan
 *
 */
public class ArraySizeTypeError extends CompError {

	String id;
	
	public ArraySizeTypeError(String lineText, int lineNumber, String id) {
		super(lineText, lineNumber);
		this.id = id;
	}
	
	public String toString(){
		return "Array Size Type Error | Array: '"+this.id+"' has size value that is not an INT starting on Line "+this.lineNum+":\n\n"
				+lineText;
	}

}
