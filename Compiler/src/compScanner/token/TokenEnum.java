package compScanner.token;
/**
 * Enum used by Token and TokenFactory to identify the type of token
 * @author Bryan Storie
 *
 */
public enum TokenEnum { 
	AND("and"),
	BOOL("bool"),
	BRANCH("branch"),
	CASE("case"),
	CONTINUE("continue"),
	DEFAULT("default"),
	ELSE("else"),
	END("end"),
	EXIT("exit"),
	IF("if"),
	INT("int"),
	LOOP("loop"),
	MOD("mod"),
	NOT("not"),
	OR("or"),
	REF("ref"),
	RETURN("return"),
	VOID("void"),
	ID(""),
	NUM(""),
	BLIT(""),
	ENDFILE(""),	
	ERROR(""),	
	PLUS("+"),
	MINUS("-"),
	MULT("*"),
	DIV("/"),
	ANDTHEN("&&"),
	ORELSE("||"),
	LT("<"),
	LTEQ("<="),
	GT(">"),
	GTEQ(">="),
	EQ("="),
	NEQ("/="),
	ASSIGN(":="),
	COLON(":"),
	SEMI(";"),
	COMMA(","),
	LPAREN("("),
	RPAREN(")"),
	LSQR("["),
	RSQR("]"),
	LCRLY("{"),
	RCRLY("}"),
	
	UNIV("");
	
	private String lexem;
	
	private TokenEnum(String lex){
		this.lexem = lex;
	}
	
	public String getLexem(){
		return this.lexem;
	}
	
	/**
	 * Creates and returns a list of TokenEnum's corresponding to all keywords in c*16 for use in keyword hash table
	 * generation.
	 * @return list of keyword TokenEnum's
	 */
	public static TokenEnum[] getWordTokens(){
		TokenEnum[] wordTokens = new TokenEnum[19];
		TokenEnum[] allTokens = TokenEnum.values();
		wordTokens[0] = TokenEnum.BLIT;
		for(int i = 0; i <18; i++){
			wordTokens[i+1] = allTokens[i];
		}
		
		return wordTokens;
	}
}

