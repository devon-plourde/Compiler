package compParser.ast;

/**
 * Enum of operators used by OperatorNode
 * @author Bryan Storie
 *
 */
public enum OperatorEnum {
	AND(false,"and"), MOD(false,"mod"), NOT(true,"not"), OR(false,"or"), PLUS(false,"add"), MINUS(false,"sub"), UMINUS(true,"uminus"), MULT(false,"mul"), DIV(false,"div"), 
	ANDTHEN(false,"and"), ORELSE(false,"or"), GT(false,"gt"), LT(false,"lt"), GTEQ(false,"gte"), LTEQ(false,"lte"), EQ(false,"eq"), NEQ(false,"neq");

	boolean isUnary;
	String quadString;
	
	OperatorEnum(boolean isUnary, String quadString){
		this.isUnary = isUnary;
		this.quadString = quadString;
	}
	
	public boolean isUnary(){
		return isUnary;
	}
	
	public String getQuadString(){
		return this.quadString;
	}
}
