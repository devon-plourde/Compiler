package compError.SemanticAnalyzer;

import compError.CompError;
import compParser.ast.TypeEnum;

/**
 * Error that occurs when the type of one of an operator's operands doesn't match with the operator's
 * expected type.
 * @author Bryan Storie
 *
 */
public class OperatorTypeMismatchError extends CompError {
	
	private String operator;
	private String var;
	private TypeEnum type;

	public OperatorTypeMismatchError(String lineText, int lineNumber, String operator, String var, TypeEnum type) {
		super(lineText, lineNumber);
		this.operator = operator;
		this.var = var;
		this.type = type;
	}
	
	public String toString(){
		return "Operator Type Mismatch Error | "+this.operator+" is undefined for '"+this.var+"' which is of type "+this.type +" starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}

}
