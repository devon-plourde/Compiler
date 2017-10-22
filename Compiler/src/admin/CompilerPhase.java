package admin;
/**
 * Enum used within AdminParam to identify which stage of compilation the compile should end at.
 * @author Bryan Storie
 *
 */
public enum CompilerPhase {
	LEXICAL, PARSER, SEMANTIC, TUPLE, COMPILE;
}
