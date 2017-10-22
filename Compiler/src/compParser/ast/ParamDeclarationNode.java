package compParser.ast;

/**
 * Represents function parameter declarations in AST.
 * @author Bryan
 *
 */
public class ParamDeclarationNode extends VariableDeclarationNode implements Parameter {
	public ParamDeclarationNode(TypeEnum type, int id, String lexem, boolean isArray){
		super(type, id, lexem, isArray);
	}
	
	public String toString(){
		return "Param "+super.toString();
	}
	
	public boolean isRef(){
		return false;
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--ParamDeclarationNode\n"+spaces+indent+"Id: "+this.lexem+"\n";
		ret += spaces+indent+"Type: "+this.type+"\n";
		return ret;
	}
}
