package compError.SemanticAnalyzer;

import compError.CompError;
import compParser.ast.TypeEnum;

/**
 * Error that occurs when the type of a return statement's expression doesn't match the return type of it's
 * containing function.
 * @author Bryan Storie
 *
 */
public class ReturnTypeError extends CompError {

	private String func;
	private TypeEnum ex;
	private TypeEnum ret;
	
	public ReturnTypeError(String lineText, int lineNumber, String func, TypeEnum ex, TypeEnum ret) {
		super(lineText, lineNumber);
		this.func = func;
		this.ex = ex;
		this.ret = ret;
	}
	
	public String toString(){
		return "Return Type Error | Function '"+this.func+"' declared return type "+this.ex+" but returned "+this.ret+" starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}
	

}
