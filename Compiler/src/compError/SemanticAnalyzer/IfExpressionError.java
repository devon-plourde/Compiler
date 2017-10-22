package compError.SemanticAnalyzer;

import compError.CompError;

public class IfExpressionError extends CompError {

	public IfExpressionError(String lineText, int lineNumber) {
		super(lineText, lineNumber);
		// TODO Auto-generated constructor stub
	}
	
	public String toString(){
		return "If Expression Error | The expression in an If statement must be of type Bool starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}

}
