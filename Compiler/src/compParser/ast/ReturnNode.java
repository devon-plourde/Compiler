package compParser.ast;

/**
 * ASTNode representing a Return Statement.
 * @author Bryan Storie
 *
 */
public class ReturnNode extends ASTNode implements Statement {
	
	boolean hasExp;
	
	public ReturnNode(boolean hasExp){
		this.hasExp = hasExp;
	}
	
	@Override
	public void addChild(ASTNode node){
		if(this.children.isEmpty() && node instanceof Expression){
			this.children.add(node);
		}
		else{
			//error
		}
	}
	
	public boolean hasExp(){
		return this.hasExp;
	}
	
	public Expression getExpression(){
		if(!this.children.isEmpty()){
			return (Expression) this.children.get(0);
		}
		else{
			return null;	//error
		}
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--ReturnNode\n";
		if(this.hasExp){
			ret+=spaces+indent+"Expression: \n"+this.getExpression().treeString(spaces+spaceInc);
		}
		return ret;
	}

}
