package compParser.ast;

/**
 * ASTNode representing an Assignment
 * @author Bryan Storie
 *
 */
public class AssignmentNode  extends ASTNode implements Statement{
	
	@Override
	public void addChild(ASTNode node){
		if(children.isEmpty()&& node instanceof VariableCallNode){
			this.children.add(node);
		}
		else if(children.size()==1 && (node instanceof Expression)){
			this.children.add(node);
		}
		else{
			//error
		}		
	}
	
	public VariableCallNode getAssignment(){
		if(!this.children.isEmpty()){
			return (VariableCallNode) this.children.get(0);
		}
		else{
			return null;
		}
	}
	
	public Expression getExpression(){
		if(this.children.size()==2){
			return (Expression) this.children.get(1); 
		}
		else{
			return null;
		}
	}
	

	public String treeString(String spaces){
		String ret = spaces+"--AssignmentNode\n"+spaces+"  Id:\n";
		ret+= this.getAssignment().treeString(spaces+spaceInc);
		ret+=spaces+indent+"Expression:\n";
		ret+=this.getExpression().treeString(spaces+spaceInc);
		return ret;
	}

}
