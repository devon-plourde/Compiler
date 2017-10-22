package compParser.ast;

import java.util.ArrayList;

/**
 * ASTNode representing a Loop Statment
 * @author Bryan Storie
 *
 */
public class LoopNode  extends ASTNode implements Statement{
	
	@Override
	public void addChild(ASTNode node){
		if(node instanceof Statement){
			this.children.add(node);
		}
		else{
			//error
		}
	}
	
	public ArrayList<Statement> getStatements(){
		ArrayList<Statement> ret = new ArrayList<Statement>();
		for(ASTNode child:this.children){
			ret.add((Statement) child);
		}
		return ret;
	}
	
	public String treeString(String spaces){//may want to add label to each statement
		String ret = spaces+"--LoopNode\n"+spaces+indent+"Statements: \n";
		for(Statement s:this.getStatements()){
			ret += s.treeString(spaces+spaceInc);
		}
		return ret;
	}

}
