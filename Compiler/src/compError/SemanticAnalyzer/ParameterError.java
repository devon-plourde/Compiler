package compError.SemanticAnalyzer;

import java.util.Iterator;

import compError.CompError;
import compParser.ast.Expression;
import compParser.ast.FunctionCallNode;
import compParser.ast.Parameter;

/**
 * Error that occurs when the arguments of a function call don't match up with the parameters of the definition.
 * @author Devon Plourde
 *
 */
public class ParameterError extends CompError {
	
	String functionName;
	String expectedParams = "";
	String actualParams =  "";
	
	
	public ParameterError(String lineText, int LineNum, String funcName, FunctionCallNode n)
	{
		super(lineText, LineNum);
		this.functionName = funcName;
		
		for(Iterator<Parameter> i = n.getExpectedParameters().iterator(); i.hasNext();){
			expectedParams += i.next().getType();
			if(i.hasNext())
				expectedParams += ",";
		}
		
		for(Iterator<Expression> i = n.getArguments().iterator(); i.hasNext();)
		{
			actualParams += i.next().getType();
			if(i.hasNext())
				actualParams += ",";
		}
	}
	
	public String toString(){
		return "Parameter Error | Parameters mismatched in function: "+functionName+". Expected: ("+ expectedParams +") Received: ("+actualParams+") starting on Line "+this.lineNum+":\n\n"+this.lineText;
	}
}
