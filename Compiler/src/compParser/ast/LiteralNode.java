package compParser.ast;

/**
 * Base class for all ASTNodes representing Literals
 * @author Bryan
 *
 */
public abstract class LiteralNode extends ExpressionNode {

	TypeEnum type;
	
	@Override
	public TypeEnum getType() {
		return this.type;
	}

	@Override
	public void setType(TypeEnum t) {
		//do nothing as types of literals are static
	}
	
	@Override
	public void addChild(ASTNode node){
		//error
	}
	
	public String getStringRep() {
		return "";
	}
}
