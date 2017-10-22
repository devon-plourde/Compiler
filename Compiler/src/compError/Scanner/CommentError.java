package compError.Scanner;

import compError.CompError;

/**
 * Error that occurs when a file ends without closing a multiline comment.
 * @author Bryan Storie
 * */
public class CommentError extends CompError {
	
	public CommentError(String lineText, int lineNum){
		super(lineText, lineNum);
	}

	public String toString(){
		return "Comment Error | End of file reached while within comment.  Expected: */\n";
	}
	
}
