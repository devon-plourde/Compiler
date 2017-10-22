package compParser.ast;

/**
 * Base type for all function and variable declarations(including function parameters).
 * @author Bryan Storie
 *
 */
public abstract class DeclarationNode extends ASTNode implements HasType{
	
	int id;
	TypeEnum type;
	String lexem;
	
	public DeclarationNode(TypeEnum type, int id, String lexem){
		this.id = id;
		this.type = type;
		this.lexem = lexem;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getLexem(){
		return this.lexem;
	}
	
	public void setType(TypeEnum t){
		//should never happen
	}
	
	public TypeEnum getType(){
		return this.type;
	}
	
	public String getStringRep(){
		return this.lexem;
	}
	
	/*public String toString(){
		return "DeclarationNode Ident: "+this.id+"|||Type: "+this.type;
	}*/

}
