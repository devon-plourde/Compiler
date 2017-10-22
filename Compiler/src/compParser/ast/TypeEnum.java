package compParser.ast;

/**
 * Enumeration of possible return types as well as operator exprected types.
 * @author Bryan Storie
 *
 */
public enum TypeEnum {
	BOOL, INT, UNIV, BOTH, VOID;
	
	public boolean isFunc(){
		return !(this==UNIV||this==BOTH);
	}
	
	public boolean isOpExp(){
		return !(this==VOID);
	}
	
	public boolean isVar(){
		return this==BOOL||this==INT;
	}
	
	public boolean matches(TypeEnum typeEnum){
		if(this == BOOL){
			return typeEnum == TypeEnum.BOOL;
		}
		else if(this == INT){
			return typeEnum == TypeEnum.INT;
		}
		else{
			return typeEnum == TypeEnum.BOOL || typeEnum == TypeEnum.INT;
		}
	}
}
