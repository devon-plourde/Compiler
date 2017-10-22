package compParser.ast;

/**
 * Interface for all ASTNodes that contain a type
 * @author Bryan Storie
 *
 */
public interface HasType {

	public TypeEnum getType();
	
	public void setType(TypeEnum t);
	
	public String treeString(String spaces);
	
	public String getStringRep();
}
