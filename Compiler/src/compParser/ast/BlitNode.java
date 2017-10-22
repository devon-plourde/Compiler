package compParser.ast;

/**
 * ASTNode representing a boolean literal
 * @author Bryan
 *
 */
public class BlitNode extends LiteralNode {
	
	boolean value;
	
	public BlitNode(boolean value){
		this.value = value;
		this.type = TypeEnum.BOOL;
	}

	public boolean getValue(){
		return this.value;
	}	
	
	public String treeString(String spaces){
		return spaces+"--BlitNode\n"+spaces+indent+"Value: "+this.value+"\n";
	}
	
	public String getStringRep(){
		return ""+this.value;
	}
}
