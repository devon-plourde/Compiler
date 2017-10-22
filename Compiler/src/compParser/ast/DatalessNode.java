package compParser.ast;

/**
 * ASTNode representing statements that contain no data.
 * @author Bryan
 *
 */
public class DatalessNode  extends ASTNode implements Statement {
	DatalessEnum nodeType;
	
	public DatalessNode(DatalessEnum e){
		this.nodeType = e;
	}
	
	public DatalessEnum getNodeType(){
		return this.nodeType;
	}
	
	@Override
	public void addChild(ASTNode node){
		if(this.nodeType==DatalessEnum.PROGRAM){
			super.addChild(node);
		}
		else{
			//error
		}
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--DatalessNode\n"+spaces+this.nodeType+"\n";
		for(ASTNode n:this.children){
			ret+= n.treeString(spaces+spaceInc);
		}
		return ret;
	}
}
