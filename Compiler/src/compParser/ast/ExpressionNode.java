package compParser.ast;

/**
 * Base class for nodes that can be in expressions with the exception of function and variable calls.
 * @author Bryan Storie
 *
 */
public abstract class ExpressionNode extends ASTNode implements HasType, Expression {

	TypeEnum type;
	
	public TypeEnum getType(){
		return this.type;
	}
	
	public void setType(TypeEnum t){
		this.type = t;
	}
}
