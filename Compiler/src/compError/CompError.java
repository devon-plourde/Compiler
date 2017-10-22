package compError;

/**
 * Base class for all compiler errors.
 * @author Devon Plourde
 *
 */
public abstract class CompError 
{
	protected int lineNum;
	protected String lineText;


	public CompError(String lineText, int lineNumber){
		this.lineText = lineText;
		this.lineNum = lineNumber;
	}
}
