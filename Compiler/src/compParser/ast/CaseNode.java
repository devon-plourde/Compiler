package compParser.ast;

/**
 * ASTNode representing a Case Statement
 * @author Bryan
 *
 */
public class CaseNode  extends ASTNode implements Statement {
	//-1 represents Default case statement
	int caseNum;
	
	public CaseNode(int num){
		this.caseNum = Math.max(-1,num);
	}
	
	@Override
	public void addChild(ASTNode node){
		if(this.children.isEmpty() && node instanceof Statement){
			children.add(node);
		}
		else{
			//error
		}
	}
	
	public int getCaseNum(){
		return this.caseNum;
	}
	
	public Statement getStatement(){
		if(!this.children.isEmpty())
			return (Statement) this.children.get(0);
		else
			return null;	//error
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--CaseNode\n"+spaces+indent+"CaseNum: ";
		if(this.caseNum==-1){
			ret+="Default";
		}
		else{
			ret += this.caseNum+"";
		}
		ret+="\n"+spaces+indent+"Statement:\n";
		ret += this.getStatement().treeString(spaces+spaceInc);
		return ret;
	}

}
