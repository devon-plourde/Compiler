package compParser.ast;

/**
 * ASTNode that represents an If Statement
 * @author Bryan Storie
 *
 */
public class IfNode  extends ASTNode implements Statement {
	
	@Override
	public void addChild(ASTNode node){
		if(this.children.isEmpty()&&node instanceof Expression){
			this.children.add(node);
		}
		else if(this.children.size()<3&&node instanceof Statement){
			this.children.add(node);			
		}
		else{
			//error
		}
	}
	
	public Expression getExpression(){
		if(!this.children.isEmpty()){
			return (Expression) this.children.get(0);
		}
		else{
			return null;		//error
		}
	}
	
	public Statement getIfStatement(){
		if(this.children.size()>1){
			return (Statement) this.children.get(1);
		}
		else{
			return null; 		//error
		}
	}
	
	public Statement getElseStatement(){
		if(this.children.size()==3){
			return (Statement) this.children.get(2);
		}
		else{
			return null;		//error
		}
	}
	
	public boolean hasElse(){
		return (this.children.size() == 3);
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--IfNode\n"+spaces+indent+"Expression: \n";
		ret += this.getExpression().treeString(spaces+spaceInc);
		ret += spaces+indent+"If true statement: \n";
		ret += this.getIfStatement().treeString(spaces+spaceInc);
		if(this.children.size()>2){
			ret += spaces+indent+"Else statement: \n";
			ret += this.getElseStatement().treeString(spaces+spaceInc);
		}
		return ret;
	}
}
