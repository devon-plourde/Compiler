package compScanner;
/**
 * An Enum used by CompScanner for remembering what comment state the scanner is in
 * @author Bryan Storie
 *
 */
public enum CommentStatusEnum {
	OUTOFCOMMENT, SINGLELINE, MULTILINE, MULTILINEESCAPE, MULTILINEENTER;
}
