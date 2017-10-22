package compParser;

import compScanner.token.TokenEnum;

/**
 * Set of final TokenEnum arrays that represent the follow sets of each Nonterminal in the C*16
 * grammar.
 * @author Bryan Storie
 *
 */
public class FollowSets {
	
	private static final TokenEnum[] RELOP = {TokenEnum.LTEQ, TokenEnum.LT, 
			TokenEnum.GT, TokenEnum.GTEQ, TokenEnum.EQ, TokenEnum.NEQ};
	
	private static final TokenEnum[] ADDOP = {TokenEnum.PLUS,
			TokenEnum.MINUS, TokenEnum.OR, TokenEnum.ORELSE};
	
	private static final TokenEnum[] MULTOP = {TokenEnum.MULT, 
			TokenEnum.DIV, TokenEnum.MOD, TokenEnum.AND, TokenEnum.ANDTHEN};
	
	protected static final TokenEnum[] PROGRAM = {TokenEnum.ENDFILE};
	
	protected static final TokenEnum[] DEC = {TokenEnum.INT, TokenEnum.BOOL, 
			TokenEnum.VOID, TokenEnum.ENDFILE};
	
	protected static final TokenEnum[] NONVOIDSPEC = {TokenEnum.ID};
	
	protected static final TokenEnum[] VARDECTAIL = {TokenEnum.INT, TokenEnum.BOOL,
			TokenEnum.VOID, TokenEnum.ENDFILE, TokenEnum.LCRLY, TokenEnum.IF,
			TokenEnum.LOOP, TokenEnum.EXIT, TokenEnum.CONTINUE, TokenEnum.RETURN,
			TokenEnum.SEMI, TokenEnum.ID, TokenEnum.BRANCH};
	
	protected static final TokenEnum[] VARNAME = {TokenEnum.COMMA, TokenEnum.SEMI};
	
	protected static final TokenEnum[] PARAMS = {TokenEnum.RPAREN};
	
	protected static final TokenEnum[] PARAM = {TokenEnum.COMMA, TokenEnum.RPAREN};
	
	protected static final TokenEnum[] STMT = {TokenEnum.LCRLY, TokenEnum.IF,
			TokenEnum.LOOP, TokenEnum.EXIT, TokenEnum.CONTINUE, TokenEnum.RETURN, 
			TokenEnum.SEMI, TokenEnum.ID, TokenEnum.RCRLY, TokenEnum.ELSE, 
			TokenEnum.END, TokenEnum.BRANCH, TokenEnum.CASE, TokenEnum.DEFAULT};
	
	protected static final TokenEnum[] CALLFACT = {TokenEnum.LTEQ, TokenEnum.LT, 
			TokenEnum.GT, TokenEnum.GTEQ, TokenEnum.EQ, TokenEnum.NEQ,TokenEnum.PLUS,
			TokenEnum.MINUS, TokenEnum.OR, TokenEnum.ORELSE, TokenEnum.MULT, 
			TokenEnum.DIV, TokenEnum.MOD, TokenEnum.AND, TokenEnum.ANDTHEN, 
			TokenEnum.RPAREN, TokenEnum.RSQR, TokenEnum.SEMI, TokenEnum.COMMA};
	
	protected static final TokenEnum[] ARGUMENTS = {TokenEnum.RPAREN};
	
	protected static final TokenEnum[] COMPOUNDSTMT = {TokenEnum.LCRLY, TokenEnum.IF,
			TokenEnum.LOOP, TokenEnum.EXIT, TokenEnum.CONTINUE, TokenEnum.RETURN, 
			TokenEnum.SEMI, TokenEnum.ID, TokenEnum.RCRLY, TokenEnum.ELSE, 
			TokenEnum.END, TokenEnum.BRANCH, TokenEnum.CASE, TokenEnum.DEFAULT, TokenEnum.INT, TokenEnum.BOOL, 
			TokenEnum.VOID, TokenEnum.ENDFILE}; 
	
	protected static final TokenEnum[] EXPRESSION = {TokenEnum.RPAREN, TokenEnum.SEMI, 
			TokenEnum.COMMA};
	
	protected static final TokenEnum[] ADDEXP = {TokenEnum.LTEQ, TokenEnum.LT, 
			TokenEnum.GT, TokenEnum.GTEQ, TokenEnum.EQ, TokenEnum.NEQ, TokenEnum.RPAREN,
			TokenEnum.RSQR, TokenEnum.SEMI, TokenEnum.COMMA};
	protected static final TokenEnum[] TERM = {TokenEnum.LTEQ, TokenEnum.LT, 
			TokenEnum.GT, TokenEnum.GTEQ, TokenEnum.EQ, TokenEnum.NEQ, TokenEnum.PLUS,
			TokenEnum.MINUS, TokenEnum.OR, TokenEnum.ORELSE, TokenEnum.RPAREN,
			TokenEnum.RSQR, TokenEnum.SEMI, TokenEnum.COMMA};
}
