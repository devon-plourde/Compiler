package compParser.ast;

import java.util.Iterator;

/**
 * Utility class for actions such as creating a formated string for the printing of an AST
 * @author Bryan
 *
 */
public class ASTUtil {
	
	/**
	 * Recursively creates a String of the AST with root as its root.  
	 * @param root root of AST
	 * @return formated string
	 */
	public static String createPrintableASTString(ASTNode root){
		
		Iterator<ASTNode> it = root.getChildren();
		String ret = "";
		
		for(int i = 0; i < 4; i++){
			it.next();	//skip the first 4 declarations in the tree as they are the predefined functions
		}
		
		while(it.hasNext()){
			ret+=it.next().treeString("");
		}
		
		return ret;
	}
	
}
