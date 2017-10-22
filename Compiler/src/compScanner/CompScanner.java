package compScanner;

import admin.Admin;
import admin.Tracer;
import compError.Scanner.CommentError;
import compError.Scanner.TokenError;
import compScanner.CommentStatusEnum;
import compScanner.token.*;
/**
 * An instance of this class can return c*16 tokens found from the characters pulled from the Admin instance given 
 * when constructed.
 * @author Bryan Storie
 */
public class CompScanner {
	
	private CommentStatusEnum commentStatus = CommentStatusEnum.OUTOFCOMMENT;
	private TokenFactory tokenFactory;
	private Admin admin;
	private boolean needNewChar = true;
	private boolean multiChar = false;	//if token has multiple characters
	private int nextCode=-1;
	private boolean whiteSpace = false;
	private Tracer myTracer;
	
	/**
	 * 
	 * @param a Admin from which the characters are pulled to create tokens
	 * @param t Tracer used for building trace
	 */
	public CompScanner(Admin a, Tracer t){
		this.tokenFactory = new TokenFactory();
		this.admin = a;
		this.myTracer = t;
	}
	
	/**
	 * Resets all variables back to a new state in order to correctly scan a new file.
	 */
	public void resetLocalVariables(){
		tokenFactory.resetWordTokenTable();
		commentStatus = CommentStatusEnum.OUTOFCOMMENT;
		needNewChar = true;
		multiChar = false;
		nextCode=-1;
		whiteSpace = false;
	}
	
	
	/**
	 * Returns the next token from the characters pulled from the CompScanner's Admin's getCh() method
	 * based on the c*16 specification.
	 * <p>
	 * It uses a do-while loop that keeps the method pulling characters until the end of a token is found
	 * (multiChar = false), the characters are not just whitespace, and it is not within a comment.  The method
	 * must also remember whether the last character it pulled before returning the last token was used in the 
	 * token or was the first character in the next token(needNewChar).  Once this has been handled execution enters
	 * one of two switch statements based on whether the current character is the first in a new token or not.
	 * Finally if it is not whitespace the character is added to <i>lexem</i> which is used in the creation of 
	 * the token to be returned.  If a character is read that isn't recognized by the CompScanner it will report a
	 * character not found error to Admin.
	 * <p>
	 * The CompScanner doesn't differentiate between ID, BLIT, and keyword tokens.  Instead this is left up to the 
	 * TokenFactory. 
	 * @return the next token from the characters fetched from CompScanner's Admin 
	 */
	public Token getNextToken(){
			
		TokenEnum tokenName = null;
		String lexem = "";
		
		do{	
			//reset whiteSpace each pass
			whiteSpace = false;
			
			//only retrieve if the current nextCode has been used else set needNewChar to true (the character guaranteed to be used in the next pass)
			if(needNewChar){
				nextCode = this.getNextCharCode();}	
			else{
				needNewChar = true;
			}
			
			//check if current nextCode could be part of a multi-character token			
			if(multiChar){
				switch(tokenName){				
					//when one & or | received tokenName set preemptively to ANDTHEN or ORELSE, changed to error if second character doesn't match
					case ANDTHEN:{
						//if an & is not followed by another then it doesn't match any token
						if(nextCode!='&'){
							tokenName = TokenEnum.ERROR;
							needNewChar = false;
						}
						multiChar = false;
						break;
					}
					case ORELSE:{
						//if a | is not followed by another then it doesn't match any token
						if(nextCode!='|'){
							tokenName = TokenEnum.ERROR;
							needNewChar = false;
						}
						multiChar = false;
						break;
					}
					
					/*in these cases if a certain character is received after the initial then it matches a different token type otherwise the second character is saved
					to be used in the next token and the tokenName left as is*/
					case LT:{
						if(nextCode!='='){
							needNewChar = false;
							multiChar = false;
						}
						//matching '<='
						else{
							tokenName = TokenEnum.LTEQ;
						}
						multiChar = false;
						break;
					}
					case GT:{
						if(nextCode!='='){
							needNewChar = false;
							multiChar = false;
						}
						//matching '>='
						else{
							tokenName = TokenEnum.GTEQ;
						}
						multiChar = false;
						break;
					}
					case COLON:{
						if(nextCode!='='){
							needNewChar = false;
							multiChar = false;
						}
						//matching ':='
						else{
							tokenName = TokenEnum.ASSIGN;
						}
						multiChar = false;
						break;
					}
					//if the first character was '/' then depending on the next character the token could either be '/=', '/' or the start of a comment
					case DIV:{
						//start of multiline comment
						if(nextCode == '*'){
							//reset and have system ignore this token because this marks a comment until end of comment token (*/)
							commentStatus = CommentStatusEnum.MULTILINE;
							tokenName = null;
							lexem = "";
						}
						else if(nextCode!='='){
							needNewChar = false;
						}
						//matching '/='
						else{
							tokenName = TokenEnum.NEQ;
						}
						multiChar = false;
						break;
					}
					//if second character is another '-' then it is the start of a single line comment
					case MINUS:{
						if(nextCode!='-'){
							needNewChar = false;
							multiChar = false;
						}
						else{
							//reset and have system ignore because this marks a comment until a newline
							commentStatus = CommentStatusEnum.SINGLELINE;
							tokenName = null;
							multiChar = false;
							lexem = "";
							continue;
						}
						break;
					}
					case NUM:{
																	
						if(nextCode>='0' && nextCode<='9'){
							lexem +=(char) nextCode;		//number is not finished so add current char to lexem string and continue on this multi-character lexem
						}
						else{	
							multiChar = false;		//number is finished so stop on this multi-character lexem
							needNewChar=false;
						}
						break;
					}
					case ID:{
						if((nextCode>='0' && nextCode<='9') || (nextCode>='A'&&nextCode<='Z') || (nextCode>='a'&&nextCode<='z') || (nextCode ==  36) || (nextCode ==  95)){
							lexem +=(char) nextCode;		//identifier is not finished so add current char to lexem string and continue on this multi-character lexem
						}
						else{
							multiChar = false;		//identifier is finished so stop on this multi-character lexem
							needNewChar = false;	
						}
						break;
					}
				default:break;
				}
				
			}
			else{
				switch(nextCode){
					case -1:tokenName = TokenEnum.ENDFILE; break;
					case ' ' :
					case '\n':
					case '	':whiteSpace = true; break;
					case '+':tokenName = TokenEnum.PLUS; break;
					case '*':tokenName = TokenEnum.MULT; break;
					case ';':tokenName = TokenEnum.SEMI; break;
					case ',':tokenName = TokenEnum.COMMA; break;
					case '(':tokenName = TokenEnum.LPAREN; break;
					case ')':tokenName = TokenEnum.RPAREN; break;
					case '[':tokenName = TokenEnum.LSQR; break;
					case ']':tokenName = TokenEnum.RSQR; break;
					case '{':tokenName = TokenEnum.LCRLY; break;
					case '}':tokenName = TokenEnum.RCRLY; break;
					case '|':tokenName = TokenEnum.ORELSE; multiChar = true; break;
					case '&':tokenName = TokenEnum.ANDTHEN; multiChar = true; break;
					case '-':tokenName = TokenEnum.MINUS; multiChar = true; break;
					case '/':tokenName = TokenEnum.DIV; multiChar = true; break;
					case '<':tokenName = TokenEnum.LT; multiChar = true; break;
					case '>':tokenName = TokenEnum.GT; multiChar = true; break;
					case '=':tokenName = TokenEnum.EQ; break;
					case ':':tokenName = TokenEnum.COLON; multiChar = true; break;
					default:{
						if(nextCode>='0' && nextCode<='9'){
							tokenName = TokenEnum.NUM;
							multiChar = true;
						}
						else if((nextCode>='A'&&nextCode<='Z')||(nextCode>='a'&&nextCode<='z')){
							tokenName = TokenEnum.ID;
							multiChar = true;
						}
						else{
							//not a valid token
							tokenName = TokenEnum.ERROR;
						}
					}
				}
				if(!whiteSpace){
					lexem += (char)nextCode;
				}
			}//end else
		}while(multiChar||whiteSpace||commentStatus!=CommentStatusEnum.OUTOFCOMMENT);		

		if(tokenName == TokenEnum.ERROR)
			admin.errorM(new TokenError(myTracer.getCurrentLine(), myTracer.getCurrentLineNum(), lexem, myTracer.getCurrentCharacter()));
		
		Token temp = tokenFactory.newToken(tokenName, lexem);
		myTracer.addToken(temp);
		return temp;
	}
	

	/**
	 * Used by getNextToken in order to take characters from CompScanner's Admin while ignoring all characters within
	 * a comment.  This method will only return a character if it is a symbol, letter, number, space, tab, or 
	 * end of file
	 * @return integer corresponding to next character useful to getNextToken
	 */
	private int getNextCharCode(){
		int returnChar = 0;
		int commentDepth = 1;
		//Ignore all characters except escape characters while in comment
		while(commentStatus!=CommentStatusEnum.OUTOFCOMMENT){
			returnChar = admin.getCh();
			myTracer.addCh(returnChar);
			switch(commentStatus){
				case SINGLELINE:{
					if(returnChar =='\n'||returnChar == -1){
						commentStatus = CommentStatusEnum.OUTOFCOMMENT;
						return '\n';
					}
					break;
				}
				case MULTILINE:{
					if(returnChar =='*'){
						commentStatus = CommentStatusEnum.MULTILINEESCAPE;
					}
					else if(returnChar =='/'){
						commentStatus = CommentStatusEnum.MULTILINEENTER;
					}
					else if(returnChar == -1){
						admin.errorM(new CommentError(myTracer.getCurrentLine(), myTracer.getCurrentLineNum()));
						commentStatus = CommentStatusEnum.OUTOFCOMMENT;
					}
					break;
				}
				case MULTILINEENTER:{			//need error case here
					if(returnChar =='*'){
						commentDepth++;
						commentStatus = CommentStatusEnum.MULTILINE;
					}
					else if(returnChar == -1){
						admin.errorM(new CommentError(myTracer.getCurrentLine(), myTracer.getCurrentLineNum()));
						commentStatus = CommentStatusEnum.OUTOFCOMMENT;
					}
					else{
						commentStatus = CommentStatusEnum.MULTILINE;
					}
				}
				case MULTILINEESCAPE:{
					if(returnChar == '/'){
						commentDepth--;
						if(commentDepth == 0){
							commentStatus = CommentStatusEnum.OUTOFCOMMENT;	
						}
						else{
							commentStatus = CommentStatusEnum.MULTILINE;
						}
					}
					else if(returnChar == -1){
						admin.errorM(new CommentError(myTracer.getCurrentLine(), myTracer.getCurrentLineNum()));
						commentStatus = CommentStatusEnum.OUTOFCOMMENT;
					}
					else{
						commentStatus = CommentStatusEnum.MULTILINE;
					}
					break;				
				}
			}
		}

		do{		//ignore non visible characters except tab(11), space(10) and end of file(-1)
			returnChar = admin.getCh();
		}while(returnChar < 32&&returnChar>-1&&returnChar!=11&&returnChar!=10);
		myTracer.addCh(returnChar);
		return returnChar;		
	}
	/**
	 * Pulls the lexem corresponding to the given identifier from TokenFactory.
	 * @param ident identifier number corresponding to an ID token
	 * @return the lexem that is associated with ident
	 */	
	public String strID(int ident){
		return tokenFactory.getIdentString(ident);
	}	
}

