package compParser.ast;

/**
 * Base class for all ASTNodes representing operators.
 * @author Bryan Storie
 *
 */
public abstract class OperatorNode extends ExpressionNode {
	OperatorEnum op;
	int arrayVal;
	
	public OperatorEnum getOperator(){
		return this.op;
	}
	
	@Override
	public void setType(TypeEnum t){
		if(t.isOpExp()){
			super.setType(t);
		}
		else{
			//error
		}
	}
	
	public void setArrayVal(int i){
		this.arrayVal = i;
	}
	
	public int getArrayVal(){
		return arrayVal;
	}
	
	public String getStringRep(){
		return ""+this.op;
	}
}
