package compError.Scanner;

import compError.CompError;

/**
 * Error that occurs when a symbol doesn't correspond to a Token of the C*16 language.
 * @author Bryan Storie
 *
 */
public class TokenError extends CompError {
	
	private String lexem;
	private int charNum;
	
	public TokenError(String lineText, int lineNum, String token, int charNum){
		super(lineText, lineNum);
		this.lexem = token;
		this.charNum = charNum;
	}
	
	public String toString(){
		return  "Token Error | "+this.lexem+ " doesn't correspond to token.  Line: "+this.lineNum+" Char: "+this.charNum+"\n"+
				this.lineText+"\n";
	}

}
