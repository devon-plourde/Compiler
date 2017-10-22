package compParser.ast;

/**
 * Represents variable declaractions in AST.
 * @author Bryan Storie
 *
 */
public class VariableDeclarationNode extends DeclarationNode {
	
	boolean isArray = false;
	private int size;
	private int level;
	private int displacement;
	
	public VariableDeclarationNode(TypeEnum type, int id, String lexem, boolean isArray){
		super(type, id, lexem);
		if(!this.type.isVar()){
			this.type = TypeEnum.UNIV;
			//error
		}
		this.isArray = isArray;
	}
	
	public boolean isArray(){
		return this.isArray;
	}

	@Override
	public void addChild(ASTNode node){
		if(this.children.isEmpty()&&node instanceof Expression){
			super.addChild(node);
		}
		else{
			//error
		}
	}
	
	public Expression getSizeExp(){
		if(isArray)
			return (Expression) this.children.get(0);
		else
			return null;
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--VariableDeclarationNode\n"+spaces+indent+"Id: "+this.lexem+"\n";
		ret += spaces+indent+"Type: "+this.type+"\n";
		if(isArray)
			ret += spaces+indent+"Array Size:\n"+this.getSizeExp().treeString(spaces+spaceInc);
		return ret;
	}
	public String toString(){
		return this.lexem+" Type: "+this.type;
	}
	
	public void setSize(int i){
		this.size = i;
	}
	
	public int getSize(){
		return this.size;
	}
	
	public void setLevel(int i){
		this.level = i;
	}
	
	public void setDisplacement(int i){
		this.displacement = i;
	}
	
	public int getLevel(){
		return this.level;
	}
	
	public int getDisplacement(){
		return this.displacement;
	}
}
