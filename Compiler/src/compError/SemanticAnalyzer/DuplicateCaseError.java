package compError.SemanticAnalyzer;

import compError.CompError;

/**
 * Error that occurs when a branch statement has multiple cases with the same number
 * @author Bryan Storie
 *
 */
public class DuplicateCaseError extends CompError {

	private int caseNum;
	
	public DuplicateCaseError(String lineText, int lineNumber, int caseNum) {
		super(lineText, lineNumber);
		this.caseNum = caseNum;
	}
	
	public String toString(){
		String ret = "";
		if(caseNum!=-1)
			ret = "Duplicate Case Error | Case '"+this.caseNum+"' has duplicate definitions";
		else
			ret = "Duplicate Case Error | Default case has duplicate definitions";
		ret += " starting on Line "+this.lineNum+":\n\n"+this.lineText;
		return ret;
	}

}
