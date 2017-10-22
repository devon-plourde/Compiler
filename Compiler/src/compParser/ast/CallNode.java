package compParser.ast;

/**
 * Base type for function and variable calls.
 * @author Bryan
 *
 */
public abstract class CallNode extends ASTNode implements HasType, Expression{
	
	int id;
	String lexem;
	DeclarationNode declaration;
	
	public CallNode(int id, String lexem){
		this.id = id;
		this.lexem = lexem;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getLexem(){
		return this.lexem;
	}
	
	public TypeEnum getType(){
		if(this.declaration!=null){
			return this.declaration.getType();
		}
		else{
			return TypeEnum.UNIV;
		}
	}
	
	public void setType(TypeEnum t){
		//do nothing as type is from declaration
	}
	
	public String getStringRep(){
		return this.lexem;
	}
}
