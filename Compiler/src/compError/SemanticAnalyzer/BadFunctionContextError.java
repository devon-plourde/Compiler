package compError.SemanticAnalyzer;

import compError.CompError;
import compParser.ast.TypeEnum;

/**
 * An error that occurs when either a void returning function is called outside of a call statement
 * or a non-void returning function is called within a call statement.
 * @author Bryan
 *
 */
public class BadFunctionContextError extends CompError {

	private String func;
	private TypeEnum type;
	
	public BadFunctionContextError(String lineText, int lineNumber, String function, TypeEnum type) {
		super(lineText, lineNumber);
		this.func = function;
		this.type = type;
	}
	
	public String toString(){
		String ret = "Bad Function Context | Function: '"+func+"' of type "+this.type+" can't be ";
		if(this.type!=TypeEnum.VOID){
			ret+="in a call statement";
		}
		else{
			ret +="outside of a call statement";
		}
		ret += " starting on Line "+this.lineNum+":\n\n"+this.lineText;
		return ret;
	}

}
