package compError.SemanticAnalyzer;

import compError.CompError;
import compParser.ast.DatalessEnum;

/**
 * Error that occurs when continue or exit statements are found outside of
 * a loop statement.
 * @author Bryan Storie
 *
 */
public class LoopStatementError extends CompError {

	private DatalessEnum e;
	
	public LoopStatementError(String lineText, int lineNumber, DatalessEnum e) {
		super(lineText, lineNumber);
		this.e = e;
	}
	
	public String toString(){
		return "Loop Statement Error | The '"+this.e+"' statement can only be within a loop starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}

}
