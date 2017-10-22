package compParser.ast;

/**
 * ASTNode representing a Call Statment
 * @author Bryan Storie
 *
 */
public class CallStmtNode extends ASTNode implements Statement {

	public void addChild(ASTNode node){
		if(this.children.isEmpty()&&node instanceof FunctionCallNode){
			super.addChild(node);
		}
		else{
			//error
		}
	}
	
	public FunctionCallNode getFunctionCall(){
		return (FunctionCallNode) this.children.get(0);
	}
	
	@Override
	public String treeString(String spaces) {
		return spaces+"--CallStmtNode\n"+spaces+indent+"Function:\n"+this.getFunctionCall().treeString(spaces+spaceInc);
	}

}
