package compParser.ast;

/**
 * ASTNode representing a Binary Operator
 * @author Bryan Storie
 *
 */
public class BinaryOpNode extends OperatorNode {
	
	public BinaryOpNode(OperatorEnum op){
		if(!op.isUnary){
			this.op = op;
		}
		else{
			//error
		}
	}
	
	@Override
	public void addChild(ASTNode node){
		if(this.children.size()<2&&node instanceof Expression){
			this.children.add(node);
		}
		else{
			//error
		}
	}
	
	public Expression getLOperand(){
		if(!this.children.isEmpty()){
			return (Expression) this.children.get(0);
		}
		else{
			return null;	//error
		}
	
	}
	
	public Expression getROperand(){
		if(this.children.size()>1){
			return (Expression) this.children.get(1);
		}
		else{
			return null;	//error
		}
	}
	
	@Override
	public String treeString(String spaces){
		String ret = spaces+"--Binary Operator\n"+spaces+indent+"Operator: "+this.op+"\n";
		ret += spaces+indent+"Type: "+this.type+"\n";
		ret += spaces+indent+"Left Operand: \n";
		ret += this.getLOperand().treeString(spaces+spaceInc);
		ret += spaces+indent+"Right Operand: \n";
		ret += this.getROperand().treeString(spaces+spaceInc);
		return ret;
	}
}
