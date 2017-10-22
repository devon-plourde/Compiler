package compParser.ast;

import java.util.ArrayList;

/**
 * ASTNode representing a Compound Statment
 * @author Bryan
 *
 */
public class CompoundNode  extends ASTNode implements Statement {
	
	private boolean varDecOver = false;
	private int numLocals;
	
	public void setNumLocals(int i){
		this.numLocals = i;
	}
	
	public int getNumLocals(){
		return numLocals;
	}
	
	@Override
	public void addChild(ASTNode node){
		if(node instanceof VariableDeclarationNode && !varDecOver){
			children.add(node);
		}
		else if(node instanceof Statement){
			children.add(node);
			varDecOver=true;
		}
		else{
			//error
		}
	}
	
	public ArrayList<VariableDeclarationNode> getDeclarations(){
		int count = 0;
		ArrayList<VariableDeclarationNode> ret = new ArrayList<VariableDeclarationNode>();
		while(this.children.get(count)instanceof VariableDeclarationNode){
			ret.add((VariableDeclarationNode) this.children.get(count++)); 
		}
		return ret;
	}
	
	public ArrayList<Statement> getStatements(){
		int count = 0;
		ArrayList<Statement> ret = new ArrayList<Statement>();
		while(this.children.get(count) instanceof VariableDeclarationNode){count++;}
		while(count<this.children.size()){
			ret.add((Statement) this.children.get(count++));
		}
		return ret;		
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--CompoundStatement\n"+spaces+indent+"Declarations:\n";
		for(VariableDeclarationNode n:this.getDeclarations()){
			ret += n.treeString(spaces+spaceInc);
		}
		ret +=spaces+indent+"Statements:\n";
		for(Statement s: this.getStatements()){
			ret += s.treeString(spaces+spaceInc);
		}
		return ret;
	}
}
