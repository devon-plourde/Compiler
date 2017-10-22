package admin;

import java.util.ArrayList;

import compScanner.token.Token;
import compScanner.token.TokenEnum;

/**
 * This class creates formated strings which contain trace information.
 * @author Bryan
 *
 */
public class Tracer {
	
	private int currentLine;
	private int tokenCount;
	private ArrayList<Token> tokenList;
	private ArrayList<Integer> tokPerLine;
	private ArrayList<String> progText;
	private ArrayList<Integer> stmtLine;
	private String parserTrace;
	private final String BLANK_LINE = "/**/";

	public void resetLocalVariables(){
		newToken = true;
		newLine = false;
		count = 0;
		currentLine = 0;
		tokenCount = 0;
		stmtPointer = -2;		//start at -2 because incremented for PROGRAM node and because it should have the first stmt be 0
		tokenList = new ArrayList<Token>(1);
		tokPerLine = new ArrayList<Integer>(1);
		progText = new ArrayList<String>(1);
		stmtLine = new ArrayList<Integer>(1);
		parserTrace = "[TRACE ENABLED]\nParser Trace\n\n";
		progText.add("");
	}
	
	public Tracer(){
		resetLocalVariables();
	}
	
	public String getCurrentLine(){
		return progText.get(currentLine);
	}
	
	public int getCurrentLineNum(){
		return this.currentLine;
	}
	
	public int getCurrentCharacter(){
		return progText.get(progText.size()-1).length();
	}
	
	/**
	 * Called by Admin whenever a new character is read from the source file.  Used to record line information for all
	 * Traces.
	 * @param c the character read
	 */
	public void addCh(int c){
		//means that last char was 10
		if(newLine){
			newline();
		}		
		if(c==10){
			newLine = true;
			if(progText.get(currentLine).equals("")){
				progText.set(currentLine, BLANK_LINE);
			}
		}
		else if(c!=-1){
			progText.set(currentLine, progText.get(currentLine)+(char) c);
		}
	}
	
	private void newline(){
		newLine = false;
		progText.add("");
		tokPerLine.add(tokenCount);
		tokenCount =0;
		currentLine++;
	}
	
	/**
	 * Called whenever a token is created from the source file.  Used to record token information for creating
	 * traces.
	 * @param t
	 */
	public void addToken(Token t){
		tokenList.add(t);
		tokenCount++;
		if(newLine){
			newline();
		}
		if(t.getTokenName()==TokenEnum.ENDFILE){
			if(progText.get(currentLine).equals("")){
				progText.set(currentLine, BLANK_LINE);
			}
			tokPerLine.add(tokenCount);
			newLine = true;
			tokenCount = 0;
			currentLine = 0;
		}
	}
	
	public String getScannerTrace(){
		String ret = "[TRACE ENABLED]\nScanner Trace\n";
		int count = tokPerLine.get(0);
		for(int i = 1; i < progText.size(); i++){
			ret+="\n["+(i)+"] "+(progText.get(i).trim().length()==0 ? "/**/" :progText.get(i).trim())+"\n";
			for(int j = 0; j < tokPerLine.get(i); j++){
				ret += "   ["+(i)+"] "+tokenList.get(count++)+"\n";
			}
		}		
		return ret;
	}
	
	
	private boolean newToken;
	private boolean newLine;
	private int count;
	
	/**
	 * Used by calls in parser to establish the line that a statement starts on.
	 * @param s String passed to addToParserTrace
	 * @param x flag
	 */
	public void addToParserTrace(String s, boolean x){
		stmtLine.add(currentLine);
		addToParserTrace(s);
	}	
	
	/**
	 * Called by parser to add parts to the Parser's trace.  Adds line and token information to the information
	 * from the Parser.
	 * @param s
	 */
	public void addToParserTrace(String s){
		if(currentLine!=0){	//skip first line as it contains built in functions
			//if on a new line print the new line's text
			if(newLine){
				while(currentLine < tokPerLine.size() && tokPerLine.get(currentLine)==0){
					this.parserTrace += "["+(currentLine)+"] " + "/**/"+"\n";
					currentLine++;
				}
				if(currentLine == tokPerLine.size()-1&&tokPerLine.get(currentLine)==1){
					this.parserTrace += "["+(currentLine)+"] " + "/**/"+"\n";
				}
				else{
					this.parserTrace += "["+(currentLine)+"] " + this.progText.get(currentLine).trim()+"\n";
				}
				newLine = false;
			}
			
			if(newToken&&count<tokenList.size()){
				this.parserTrace += "   "+"["+(currentLine)+"] "+this.tokenList.get(count++)+"\n";
				newToken = false;
			}
	
			//add the parser's input
			this.parserTrace += "       "+s+"\n";
		}
		else{
			count = this.tokPerLine.get(0);
		}
		//if a token has been consumed move to and print next token
		if(s.startsWith("Match:") || s.startsWith("Recovery:")){
			if(currentLine != 0){
				this.parserTrace+="\n";
			}
			newToken = true;
			tokenCount++;
			if(tokenCount==tokPerLine.get(currentLine)&&!s.startsWith("Match: ENDFILE")&&currentLine<tokPerLine.size()-1){
					currentLine++;
					tokenCount = 0;
					newLine = true;
			}
		}
	}
	
	public String getParserTrace(){
		return this.parserTrace;
	}
	
	private int stmtPointer;
	private ArrayList<Integer> stmtList = new ArrayList<Integer>();
	
	
	public void incrementStmt(){
		stmtList.add(stmtPointer);
		stmtPointer++;
	}
	
	public int getLineSA(){
		return stmtLine.get(stmtPointer);
	}
	
	public String getTextSA(){
		String ret = ""; 
		for(int i = this.stmtLine.get(stmtPointer) ; (stmtPointer <this.stmtLine.size()-1 && i < this.stmtLine.get(stmtPointer+1)) || (!(stmtPointer <this.stmtLine.size()-1 ) && i < this.progText.size()) ; i++){
			ret += this.progText.get(i)+"\n";
		}
		
		return ret;
	}
}
