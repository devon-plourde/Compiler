package compParser.ast;

/**
 * Represents pass by reference function parameter declarations in AST.
 * @author Bryan Storie
 *
 */
public class RefParamDeclarationNode extends VariableDeclarationNode implements Parameter {
	public RefParamDeclarationNode(TypeEnum type, int id, String lexem){
		super(type, id, lexem, false);
	}
	
	public String toString(){
		return "RefParam "+super.toString();
	}
	
	public boolean isRef(){
		return true;
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--RefParamDeclarationNode\n"+spaces+indent+"Id: "+this.lexem+"\n";
		ret += spaces+indent+"Type: "+this.type+"\n";
		return ret;
	}
}
