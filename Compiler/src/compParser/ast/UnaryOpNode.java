package compParser.ast;

/**
 * ASTNode representing a Unary Operator
 * @author Bryan Storie
 *
 */
public class UnaryOpNode extends OperatorNode {

	public UnaryOpNode(OperatorEnum op){
		if(op.isUnary){
			this.op = op;
		}
		else{
			//error
		}
	}
	
	@Override
	public void addChild(ASTNode node){
		if(this.children.isEmpty() && node instanceof Expression){
			this.children.add(node);
		}
		else{
			//error
		}
	}
	
	public Expression getOperand(){
		if(!this.children.isEmpty()){
			return (Expression) this.children.get(0);
		}
		else{
			return null; 		//error
		}
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--UnaryOpNode\n"+spaces+indent+"Operator: "+this.getOperator()+"\n";
		ret += spaces+indent+"Type: "+this.type+"\n";
		ret += spaces+indent+"Operand:\n"+this.getOperand().treeString(spaces+spaceInc)+"\n";
		return ret;
	}
}
