package compError.Parser;

import compError.CompError;
import compScanner.token.Token;
import compScanner.token.TokenEnum;

/**
 * Error that occurs when the lookahead of the parser doesn't match the expected token based on the syntax.
 * @author Bryan Storie
 *
 */
public class SyntaxError extends CompError {
	
	private TokenEnum expected;
	private Token recieved;
	
	public SyntaxError(String lineText, int lineNum, TokenEnum expected, Token recieved){
		super(lineText, lineNum);
		this.expected = expected;
		this.recieved = recieved;
	}
	
	public String toString(){
		return "Syntax Error | on line: "+this.lineNum+" Expected: "+expected+" || Recieved: "+recieved+"\n"+
				lineText+"\n";
	}

}
