package compParser.ast;

import java.util.ArrayList;

/**
 * ASTNode representing a Branch Statement
 * @author Bryan
 *
 */
public class BranchNode  extends ASTNode implements Statement {
	
	@Override
	public void addChild(ASTNode node){
		if(this.children.isEmpty()&&node instanceof Expression){
			this.children.add(node);
		}
		else if(node instanceof CaseNode){
			this.children.add(node);
		}
		else{
			//error
		}
	}
	
	public Expression getExpression(){
		if(!this.children.isEmpty())
			return (Expression) this.children.get(0);
		else
			return null;
	}
	
	public ArrayList<CaseNode> getCases(){
		ArrayList<CaseNode> cases = new ArrayList<CaseNode>();
		for(int i = 1; i < this.children.size(); i++){
			cases.add((CaseNode) this.children.get(i));
		}
		return cases;
	}
	
	public int getNumCases(){
		return this.children.size()-1;
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--BranchNode\n"+spaces+indent+"Expression:\n"+this.getExpression().treeString(spaces+spaceInc);
		ret += spaces+indent+"Cases:\n";
		for(CaseNode c: this.getCases()){
			ret+= c.treeString(spaces+spaceInc);
		}
		return ret;
	}
}
