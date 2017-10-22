package compError.SemanticAnalyzer;

import compError.CompError;
import compParser.ast.TypeEnum;

/**
 * Error that occurs when code attempts to assign a value with a different type to the variable it
 * is being assigned to.
 * @author Bryan Storie
 *
 */
public class IlegalAssignmentError extends CompError {

	private String var;
	private TypeEnum vartype;
	private TypeEnum assignType;
	
	public IlegalAssignmentError(String lineText, int lineNumber, String var, TypeEnum varType, TypeEnum assignType) {
		super(lineText, lineNumber);
		this.var = var;
		this.vartype = varType;
		this.assignType = assignType;
		// TODO Auto-generated constructor stub
	}
	
	public String toString(){
		return "Illegal Assignment Error | Cannot assign "+this.assignType+" to "+this.var+" which is of type "+this.vartype+" starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}

}
