package compScanner.token;

/**
 * Used to contain information about Tokens identified by CompScanner.
 * @author Bryan Storie
 *
 */
public class Token {

	private TokenEnum tokenType;
	private int attribute = 0;
	private String lexem = "";
	
	/**
	 * Creates a token dynamically based on tokenType.
	 * <p>
	 * If the token is an ID, Num, Error, or boolean literal the idNum and lexem fields are filled as the given values
	 * for these fields is relevant.  Otherwise only the tokenType is filled in and the other fields left as default.
	 * @param tokenType
	 * @param attr identifier number given by TokenFactory for ID error tokens, numerical value for Num and boolean literals
	 * @param lexem string the token was created from for Id, Num, Error, and boolean literal tokens (only used for printing)
	 */
	public Token(TokenEnum tokenType, int attr, String lexem){
	this.tokenType = tokenType;
		//select which lexem to fill based on the contents of lexem
		if(this.tokenType==TokenEnum.ID||this.tokenType==TokenEnum.NUM||this.tokenType==TokenEnum.ERROR||this.tokenType==TokenEnum.BLIT){
			this.attribute = attr;
			this.lexem = lexem;
		}
		
	}
	
	public TokenEnum getTokenName(){
		return this.tokenType;
	}
	
	/**
	 * Creates a formated string for printing based on tokenType
	 * 
	 * @return formated string
	 */
	public String toString(){
		String s = "";
		
		s+=this.tokenType+", ";
		
		switch(this.tokenType){
			case ID:
			case ERROR:
				s+=this.attribute+" => "+this.lexem;
				break;
			case NUM:
			case BLIT:
				s+=this.attribute;
				break;
			default:
				s+="null";
				break;
			
		}		
		return s;
	}
	
	public int getAttribute(){
		return this.attribute;
	}
	
	public boolean equals(Token t){
		boolean result = false;
		if(this.getTokenName()==t.getTokenName()){
			if(this.getTokenName()==TokenEnum.ID||this.getTokenName()==TokenEnum.BLIT||this.getTokenName()==TokenEnum.NUM||
					this.getTokenName() == TokenEnum.ERROR){
				if(this.getAttribute()==t.getAttribute()){
					result = true;
				}
			}
			else{
				result = true;
			}
		}	
		return result;
	}
	
}
