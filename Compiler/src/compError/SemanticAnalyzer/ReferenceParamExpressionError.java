package compError.SemanticAnalyzer;

import compError.CompError;

/**
 * Error that occurs when the argument in a function call corresponding to a ref parameter isn't an L-expression.
 * @author Bryan Storie
 *
 */
public class ReferenceParamExpressionError extends CompError {

	private String pId;
	private String func;
	
	public ReferenceParamExpressionError(String lineText, int lineNumber, String func, String pId) {
		super(lineText, lineNumber);
		this.pId = pId;
		this.func = func;
	}
	
	public String toString(){
		return "Reference Parameter Expression Error | Function '"+func+"' has call with non-L Expression for Parameter '"+this.pId+"'  starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}

}
