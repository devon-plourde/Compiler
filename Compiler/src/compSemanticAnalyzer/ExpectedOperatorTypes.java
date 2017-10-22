package compSemanticAnalyzer;

import java.util.Arrays;

import compParser.ast.OperatorEnum;
import compParser.ast.TypeEnum;

/**
 * Class used to contain the types expected by operators.
 * @author Bryan Storie
 *
 */
public final class ExpectedOperatorTypes {

	private static final OperatorEnum[] bool = {OperatorEnum.LT, OperatorEnum.LTEQ, OperatorEnum.GT, OperatorEnum.GTEQ, OperatorEnum.EQ, OperatorEnum.NEQ, OperatorEnum.OR, OperatorEnum.ORELSE, OperatorEnum.AND, OperatorEnum.ANDTHEN, OperatorEnum.NOT};
	private static final OperatorEnum[] integer = {OperatorEnum.LT, OperatorEnum.LTEQ, OperatorEnum.GT, OperatorEnum.GTEQ, OperatorEnum.EQ, OperatorEnum.NEQ, OperatorEnum.MINUS, OperatorEnum.PLUS, OperatorEnum.MULT, OperatorEnum.DIV, OperatorEnum.MOD, OperatorEnum.UMINUS};
	
	
	public static TypeEnum getExpectedType(OperatorEnum operator){
		if(Arrays.asList(bool).contains(operator)){
			if(Arrays.asList(integer).contains(operator)){
				return TypeEnum.BOTH;
			}
			return TypeEnum.BOOL;
		}
		else if(Arrays.asList(integer).contains(operator)){
			return TypeEnum.INT;
		}
		else{
			return null;
		}
	}
}
