package compParser.ast;

/**
 * Represents variable calls in AST.
 * @author Bryan Storie
 *
 */
public class VariableCallNode extends CallNode {
	
	private int index;
	
	public VariableCallNode(int i, String lexem){
		super(i, lexem);
	}
	
	@Override
	public void addChild(ASTNode node){
		if(this.children.isEmpty() && node instanceof Expression){
			super.addChild(node);
		}
		else{
			//error
		}
	}
	
	public boolean declaredArray(){
		if(this.declaration!=null){
			return ((VariableDeclarationNode)this.declaration).isArray();
		}
		else{
			return false;
		}
	}
	
	public Expression getArraySize(){
		if(!this.children.isEmpty()){
			return (Expression) this.children.get(0);
		}
		else{
			return null;
		}
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--VariableCallNode\n"+spaces+indent+"Id: "+this.lexem+"\n";
		ret += spaces+indent+"Type: "+this.getType()+"\n";
		if(!this.children.isEmpty()){
			ret += spaces+indent+"Array Index:\n"+this.children.get(0).treeString(spaces+spaceInc);
		}
		return ret;
	}
	
	public VariableDeclarationNode getDeclaration(){
		return (VariableDeclarationNode) this.declaration;
	}
	
	public void setDeclaration(VariableDeclarationNode n){
		this.declaration = n;
	}
	
	public String toString(){
		return this.lexem+indent+"Type: "+this.getType();
		
	}
	
	public void setIndex(int i){
		this.index = i;
	}
	
	public int getIndex(){
		return this.index;
	}
}
