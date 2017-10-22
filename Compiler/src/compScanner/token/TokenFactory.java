package compScanner.token;

import java.util.ArrayList;
import java.util.Hashtable;


/**
 * Creates Tokens based on given information.  Also keeps track of all keywords and identifiers found thus far in 
 * order to ensure that each ID lexem only gets one identifier number.
 * @author Bryan Storie
 *
 */
public class TokenFactory {

	private Hashtable<String,Token> wordTokenTable;
	private int currentIDNumber = 0;
	private int currentErrorCount = 0;
	private ArrayList<String> identMapping = new ArrayList<String>();
	
	public TokenFactory(){
		//set up initial hashmap with keywords
		resetWordTokenTable();
	}
	/**
	 * Resets all identifiers discovered so far as will as the ID and error number counter.
	 */
	public void resetWordTokenTable(){
		wordTokenTable = new Hashtable<String,Token>();
		identMapping = new ArrayList<String>();
		insertKeyWordTokens();
		currentIDNumber = 0;
		currentErrorCount = 0;
	}
	

	/**
	 * Inserts all of the c*16 keywords and boolean literals in the hash table used to look up ID tokens.
	 */
	private void insertKeyWordTokens(){
		TokenEnum[] wordTokens = TokenEnum.getWordTokens();
		Token t = new Token(wordTokens[0],1,"true");
		wordTokenTable.put("true", t);
		t = new Token(wordTokens[0],0,"false");
		wordTokenTable.put("false", t);
		
		for(int i = 1; i < wordTokens.length; i++){
			t = new Token(wordTokens[i],0,"");
			wordTokenTable.put(wordTokens[i].getLexem(), t);
		}
		
	}
	//Creates new Token and inserts in table if Token doesn't exist already within Hashtable else returns existing
	/**
	 * Returns a token based on the given information.
	 * <p>
	 * If the tokenType is not an ID then the token is created directly and returned.  If the token is an ID then a lookup
	 * is performed on the hash table using lexem containing keywords, boolean literals, and all discovered keywords. 
	 * If a match is found then the Token held there is returned, otherwise a new token is created and inserted into the
	 * hash table and returned. A result of this process is that receiving a tokenType ID may not result in the return 
	 * of a Token with type ID as it may be a keyword or a boolean literal.
	 * @param tokenType the type of the token to be created
	 * @param lexem the string corresponding to that token
	 * @return Token based on parameters
	 */
	public Token newToken(TokenEnum tokenType, String lexem){
		Token returnToken;
		if(tokenType == TokenEnum.ID){
			returnToken = wordTokenTable.get(lexem);
			if(returnToken==null){
				returnToken = new Token(tokenType, currentIDNumber, lexem);	//ID inserted into table with unique ID number (incremented after used)
				wordTokenTable.put(lexem, returnToken);
				identMapping.add(currentIDNumber++, lexem);
			}
		}
		else if(tokenType == TokenEnum.NUM){
			returnToken = new Token(tokenType,Integer.parseInt(lexem), lexem);
		}
		else if(tokenType == TokenEnum.ERROR){
			returnToken = new Token(tokenType,currentErrorCount++,lexem);
		}
		else{
			returnToken = new Token(tokenType,0, "");
		}
		return returnToken;
	}
	
	/**
	 * Method used to return a string corresponding to an ID Token identification number.  Not useful currently as
	 * the Tokens contain this information already.
	 * @param identNum
	 * @return String corresponding to identNum
	 */
	public String getIdentString(int identNum){
		String identString = "";
		if(identNum<identMapping.size()){
			identString = identMapping.get(identNum);	
		}		
		return identString;	//"" indicates no match in array
	}	
}
