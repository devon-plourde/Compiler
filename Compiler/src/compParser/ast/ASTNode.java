package compParser.ast;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Base class for all nodes in the AST.
 * @author Bryan Storie
 *
 */
public abstract class ASTNode {
	final String spaceInc = " | |";
	final String indent = " |";
	LinkedList<ASTNode> children = new LinkedList<ASTNode>();
	
	public ASTNode(){}
	
	public void addChild(ASTNode node){
		children.add(node);
	}
	
	public Iterator<ASTNode> getChildren(){
		return this.children.iterator();
	}
	
	public abstract String treeString(String spaces);
		/*String ret = spaces+"ASTNode\n"+spaces+"Children:\n";
		for(ASTNode child:this.children){
			ret+=child.treeString(spaces+spaceInc);
		}
		return ret;
		return "";
	}*/
}
