package compError.SemanticAnalyzer;

import compError.CompError;
import compParser.ast.OperatorEnum;
import compParser.ast.TypeEnum;

/**
 * Error that occurs when an operator capable of handling different types gets one of both (eg. true GT 8)
 * @author Bryan Storie
 *
 */
public class TypeMismatchError extends CompError {

	private OperatorEnum op;
	private TypeEnum lType;
	private TypeEnum rType;

	
	public TypeMismatchError(String lineText, int lineNumber, OperatorEnum op, TypeEnum lType, TypeEnum rType) {
		super(lineText, lineNumber);
		this.op = op;
		this.lType = lType;
		this.rType = rType;
	}

	public String toString(){
		return "Type Mismatch Error | "+this.op+" cannot handle both type "+this.lType+" and type "+this.rType+" at the same time starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}
}
