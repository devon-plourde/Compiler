package compParser.ast;

/**
 * Interface representing nodes that can appear in expressions
 * @author Bryan Storie
 *
 */
public interface Expression {
	public TypeEnum getType();
	public String treeString(String spaces);
	public String getStringRep();
}
