package compParser.ast;

import java.util.ArrayList;

/**
 * Represents function declaration in the AST.
 * @author Bryan Storie
 *
 */
public class FunctionDeclarationNode extends DeclarationNode {
	public FunctionDeclarationNode(TypeEnum type, int id, String lexem){
		super(type, id, lexem);
		if(!this.type.isFunc()){
			this.type = TypeEnum.VOID;
			//error
		}
	}
	
	private int numLocals;
	private int numParams;
	private int lineDecl;
	
	public void setLineDecl(int i){
		this.lineDecl = i;
	}
	
	public int getLineDecl(){
		return this.lineDecl;
	}
	
	public ArrayList<Parameter> getParameters(){
		ArrayList<Parameter> params = new ArrayList<Parameter>(0);
		
		for(ASTNode n:this.children){
			if(n instanceof Parameter){
				params.add((Parameter) n);
			}
			else{
				break;
			}
		}
		return params;
	}
	
	public ArrayList<Boolean> getParamIsRef(){
		ArrayList<Boolean> params = new ArrayList<Boolean>(0);
		
		for(ASTNode n:this.children){
			if(n instanceof ParamDeclarationNode){
				params.add(false);
			}
			else if(n instanceof RefParamDeclarationNode){
				params.add(true);
			}
			else{
				break;
			}
		}
		return params;
	}
	
	public CompoundNode getBody(){
		if(paramOver){
			return (CompoundNode) this.children.getLast();
		}
		else{
			return null;		//error
		}
	}
	
	private boolean paramOver = false;
	
	@Override
	public void addChild(ASTNode node){
		if(!paramOver && node instanceof Parameter){
			this.children.add(node);
		}
		else if(!paramOver && node instanceof CompoundNode){
			this.children.add(node);
			paramOver = true;
		}
		else{
			//error
			//System.out.println("Error in FunDecNode");
		}
	}
	
	public String treeString(String spaces){
		String ret = spaces+"--FunctionDeclarationNode\n"+spaces+indent+"Id: "+this.lexem+"\n";
		ret += spaces+indent+"ReturnType: "+this.type+"\n";
		ret += spaces+indent+"Parameters: \n";
		if(this.getParameters().isEmpty()){
			ret+= spaces+spaceInc+"VOID\n";
		}
		else{
			for(Parameter p: this.getParameters()){
				ret+= p.treeString(spaces+spaceInc);
			}
		}
		ret += spaces+indent+"Body: \n"+this.getBody().treeString(spaces+spaceInc);
		return ret;
	}
	
	public String toString(){
		return "Func: "+this.lexem + " Type: "+this.type;
	}
	
	public void setNumLocals(int i){
		this.numLocals = i;
	}
	
	public int getNumLocals(){
		return numLocals;
	}
	
	public void setNumParams(int i){
		this.numParams = i;
	}
	
	public int getNumParams(){
		return this.numParams;
	}
}
