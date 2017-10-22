package compParser.ast;

/**
 * ASTNode representing a Number literal
 * @author Bryan
 *
 */
public class NumNode extends LiteralNode {

	int value;
	
	public NumNode(int value){
		this.value = Math.max(0, value);
		this.type = TypeEnum.INT;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public String treeString(String spaces){
		return spaces+"--NumNode\n"+spaces+indent+"Value: "+value+"\n";
	}
	
	public String getStringRep(){
		return ""+this.value;
	}
	
	public int getArrayVal(){
		return this.value;
	}
}
