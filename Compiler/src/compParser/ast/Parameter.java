package compParser.ast;

/**
 * Interface for all ASTNodes representing Parameters
 * @author Bryan Storie
 *
 */
public interface Parameter {
	public TypeEnum getType();
	public boolean isRef();
	public String getLexem();
	public String treeString(String spaces);
	public void setLevel(int i);
	public void setDisplacement(int i);
	public int getLevel();
	public int getDisplacement();
}
