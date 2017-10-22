package compParser.ast;

import java.util.ArrayList;

/**
 * Represents function call in the AST.
 * @author Bryan Storie
 *
 */
public class FunctionCallNode extends CallNode{
	public FunctionCallNode(int id, String lexem){
		super(id, lexem);
	}
	
	public String toString(){
		return "Function"+super.toString();
	}
	
	public FunctionDeclarationNode getDeclaration(){
		return (FunctionDeclarationNode) this.declaration;
	}
	
	public void setDeclaration(FunctionDeclarationNode n){
		this.declaration = n;
	}
	
	public ArrayList<Parameter> getExpectedParameters(){
		if(this.getDeclaration()!=null)
			return ((FunctionDeclarationNode) this.getDeclaration()).getParameters();
		else
			return new ArrayList<Parameter>(0);
	}
	
	public ArrayList<Expression> getArguments(){
		ArrayList<Expression> args = new ArrayList<Expression>(1);
		
		for(ASTNode n:this.children){
			args.add((Expression) n);
		}
		return args;
	}
	
	@Override
	public void addChild(ASTNode node){
		if(node instanceof Expression){
			this.children.add(node);
		}
		else{
			System.out.println("Error in FuncCall");//error
		}
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--FunctionCallNode\n"+spaces+indent+"Id: "+this.lexem+"\n"+spaces+indent+"Parameters:\n";	//fix id
		if(this.getArguments().isEmpty()){
			ret+=spaces+"VOID\n";
		}
		else{
			for(Expression p: this.getArguments()){
				ret += p.treeString(spaces+spaceInc);
			}
		}
		return ret;
	}
}
