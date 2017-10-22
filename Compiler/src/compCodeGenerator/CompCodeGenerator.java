package compCodeGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import admin.Admin;
import compParser.ast.ASTNode;
import compParser.ast.AssignmentNode;
import compParser.ast.BranchNode;
import compParser.ast.CaseNode;
import compParser.ast.CompoundNode;
import compParser.ast.DatalessEnum;
import compParser.ast.DatalessNode;
import compParser.ast.FunctionCallNode;
import compParser.ast.FunctionDeclarationNode;
import compParser.ast.IfNode;
import compParser.ast.LiteralNode;
import compParser.ast.LoopNode;
import compParser.ast.OperatorEnum;
import compParser.ast.OperatorNode;
import compParser.ast.Parameter;
import compParser.ast.ReturnNode;
import compParser.ast.TypeEnum;
import compParser.ast.VariableCallNode;
import compParser.ast.VariableDeclarationNode;

public class CompCodeGenerator {

	private Admin myAdmin;
	
	public CompCodeGenerator(Admin a){
		this.myAdmin =a;
		resetLocalVariables();
	}
	
	public void resetLocalVariables(){
		numLocals.clear();
		numLocals.push(0);
		numParams = 0;
		varDis = 2;		
		paramDis = -1;
		inFuncDec = false;
		
		currentNumParams=0;	
		varStrings.clear();	
		inArray = false;
		branchEnd.clear();
		funcDec = false;
		endOfLoop.clear();
		startOfLoop.clear();
		caseLabelOffset.clear();
	}
	
	public String generateCode(ASTNode root){
		/*this.root = root;*/
		
		annotateNode(root,0);
		
		String code = createCodeHeader(root);
		
		//create quad code with labels
		code += parseTree(root,0,0,1);		//needs to be 1 because first is taken by code header
		
		//substitute labels in jumps for line nums and put line nums in function calls
		code = translateJumps(code);
		
		return code;
	}
	
		
	public String translateJumps(String code){
		HashMap<String,Integer> jumpMap = new HashMap<String,Integer>();
		ArrayList<String> codeParts = new ArrayList<String>(Arrays.asList(code.split("\n")));
		
		//map labels to line numbers
		//map functions to line numbers
		for(int i = 0;i<codeParts.size();i++){
			
			if(codeParts.get(i).startsWith("lab")){
				jumpMap.put(codeParts.get(i).substring(8), i);		//label string starts at char 8
				codeParts.remove(i);
				i--;
			}
			else if(codeParts.get(i).startsWith("fun")){
				String[] parts = codeParts.get(i).split(",");
				jumpMap.put(parts[0].substring(4), i);
			}
		}
		
		for(int i = 0;i<codeParts.size();i++){
			if(codeParts.get(i).startsWith("call")){
				int size = codeParts.get(i).length()-9;			//get size of func call
				String s = "call @"+jumpMap.get(codeParts.get(i).substring(5, 5+size))+",-,-";
				codeParts.set(i, s);
			}
			else if(codeParts.get(i).startsWith("if")){
				String[] parts = codeParts.get(i).split(",");
				parts[parts.length-1] = "@"+jumpMap.get(parts[parts.length-1])+"";
				String s = "";
				for(String t: parts)
					s+=t+",";
				s = s.substring(0, s.length()-1);		//remove last comma
				codeParts.set(i, s);
			}
			else if(codeParts.get(i).startsWith("goto")){
				String s = codeParts.get(i).substring(0, 9);
				s += "@"+jumpMap.get(codeParts.get(i).substring(9));
				codeParts.set(i, s);
			}
		}
		
		
		//replace labels in code with appropriate line nums
		//replace function names in code w/ line nums
		code = "";
		for(int i = 0; i < codeParts.size();i++)
			code+=i+"|||"+codeParts.get(i)+"\n";
		return code;
	}
	
	public String createCodeHeader(ASTNode root){
		int globalCount = 0;
		for(Iterator<ASTNode> i = root.getChildren();i.hasNext();){
			if(i.next() instanceof VariableDeclarationNode){
				globalCount++;
			}
		}
		return "start "+globalCount+",-,-\nrval -,-,(0,0)\ncall main,-,-\nhlt -,-,-\n";
	}
	
	private LinkedList<Integer> numLocals = new LinkedList<Integer>();
	private int numParams;
	private int varDis;
	private int paramDis;
	private boolean inFuncDec;
	
	private void annotateNode(ASTNode node, int level){
		
		if(node instanceof VariableDeclarationNode){
			if(node instanceof Parameter){
				Parameter temp = (Parameter) node;
				temp.setLevel(level);
				temp.setDisplacement(paramDis--);
				numParams++;
			}
			else{
				VariableDeclarationNode temp = (VariableDeclarationNode) node;
				temp.setLevel(level);
				if(level==0)
					temp.setDisplacement(varDis - 2);
				else
					temp.setDisplacement(varDis);
				numLocals.push(numLocals.pop()+1);
				if(temp.isArray())
					varDis += temp.getSize();		//take up space for array elements
				else
					varDis++;
			}
		}
		//keep track of level(reset displacements when new level/frame added
		else if(node instanceof CompoundNode){
			if(inFuncDec){
				inFuncDec = false;
			}
			else{
				level++;
				varDis = 1;
				numLocals.push(0);
			}
			
		}
		else if(node instanceof FunctionDeclarationNode){
			inFuncDec = true;
			level++;
			paramDis = -1;	
			varDis = 2;
			numLocals.push(0);
			numParams = 0;
		}
		
		
		//depth first recursion
		for(Iterator<ASTNode> i = node.getChildren(); i.hasNext();){
			annotateNode(i.next(), level);
		}
		
		if(node instanceof CompoundNode && level>1){
			((CompoundNode) node).setNumLocals(numLocals.pop());
		}			
		else if(node instanceof FunctionDeclarationNode){
			((FunctionDeclarationNode) node).setNumLocals(numLocals.pop());
			((FunctionDeclarationNode) node).setNumParams(numParams);
		}
		
	}
	
	private int currentNumParams;	//used by return stmt to fill field
	private LinkedList<String> varStrings = new LinkedList<String>();	//carries string representaions of variables (expressions, literals, or calls)
	private boolean inArray;
	private LinkedList<Integer> branchEnd = new LinkedList<Integer>();
	private boolean funcDec;
	private LinkedList<Integer> endOfLoop = new LinkedList<Integer>();
	private LinkedList<Integer> startOfLoop = new LinkedList<Integer>();
	private LinkedList<Integer> loopLevel = new LinkedList<Integer>();
	private LinkedList<Integer> caseLabelOffset = new LinkedList<Integer>();		//used to give cases labels
	boolean arrayFlag = false;
	
	private String parseTree(ASTNode node, int currentLevel, int currentDisplacement, int labCount){
		String ret = "";
		int inIf = 0;		//flag used to handle if stmts (-2 after expresion has else, -1 after ifstmt has else, 1 after expression no else)
		boolean shortCircuit = false;
		boolean skipCmpd = false;
		boolean branchLogic = false;
		boolean sCDone = false;
		boolean read = false;
		boolean ignoreRecur = false;
		
		//variable declaration ignored
		if(node instanceof FunctionDeclarationNode){
			FunctionDeclarationNode temp = (FunctionDeclarationNode) node;
			ret +="fun "+myAdmin.strID(temp.getId())+","+temp.getNumLocals()+",-\n";
			currentNumParams = temp.getNumParams();
			currentLevel++;
			currentDisplacement = 2+temp.getNumLocals();
			funcDec =true;
			if(myAdmin.strID(temp.getId()).equals("readint")){
				ignoreRecur = true;
				ret += "rdi -,-,("+currentLevel+","+currentDisplacement+")\n";
				ret += "retv "+this.currentNumParams+",("+currentLevel+","+currentDisplacement+"),-\n";
			}
			else if(myAdmin.strID(temp.getId()).equals("readbool")){
				ignoreRecur = true;
				ret += "rdb -,-,("+currentLevel+","+currentDisplacement+")\n";
				ret += "retv "+this.currentNumParams+",("+currentLevel+","+currentDisplacement+"),-\n";
			}
			else if(myAdmin.strID(temp.getId()).equals("writeint")){
				ignoreRecur = true;
				ret += "wri ("+currentLevel+",-1),-,-\n";		//num params is known
				ret += "ret "+this.currentNumParams+",-,-\n";
			}
			else if(myAdmin.strID(temp.getId()).equals("writebool")){
				ignoreRecur = true;
				ret += "wrb ("+currentLevel+",-1),-,-\n";		//num params is known
				ret += "ret "+this.currentNumParams+",-,-\n";
			}
			
		}
		else if(node instanceof OperatorNode){ //claim the next temp(which is where the results of this operator will go after recursion)
			currentDisplacement++;
			if(((OperatorNode) node).getOperator()==OperatorEnum.ANDTHEN || ((OperatorNode) node).getOperator()==OperatorEnum.ORELSE){
				shortCircuit = true;
				labCount++;  		//claim label for short circuit
			}
		}
		else if(node instanceof FunctionCallNode){	//check if function has return val and claim next temporary if it does have one
			FunctionCallNode temp = (FunctionCallNode) node;
			if(!(temp.getDeclaration().getType()==TypeEnum.VOID)){
				ret += "rval -,-,("+currentLevel+","+currentDisplacement+")\n";		
			}
			currentDisplacement++;	
		}
		else if(node instanceof VariableCallNode){
			inArray = true;
			if(!arrayFlag){
				currentDisplacement++;
			}
		}
		else if(node instanceof CompoundNode){
			if(!funcDec){
				CompoundNode temp = (CompoundNode) node;
				currentLevel++;
				currentDisplacement = 1+temp.getNumLocals();
				ret+="ecs "+temp.getNumLocals()+",-,-\n";
			}
			else{
				funcDec = false;
				skipCmpd = true;
			}
		}
		else if(node instanceof LoopNode){
			labCount+=3;
			endOfLoop.push(labCount-1);
			startOfLoop.push(labCount-2);
			loopLevel.push(currentLevel);
			ret+="lab -,-,l"+startOfLoop.peek()+"\n";//add label for top of loop
			}
		else if(node instanceof IfNode){
			IfNode temp = (IfNode) node;
			if(temp.hasElse()){
				inIf = -2;
				labCount+=2;		//need 2 labels, begin of else and end of if
			}
			else{
				inIf = 1;
				labCount++;
			}
		}
		else if(node instanceof DatalessNode){
			DatalessNode temp = (DatalessNode) node;
			if(temp.getNodeType() == DatalessEnum.EXIT){
				//jump to the end of the loop stmt
				for(int i = currentLevel; i > loopLevel.peek();i--)
					ret += "lcs -,-,-\n";
				ret+="goto -,-,l"+endOfLoop.peek()+"\n";
			}
			else if(temp.getNodeType() == DatalessEnum.CONTINUE){
				//jump to the start of the loop stmt
				for(int i = currentLevel; i > loopLevel.peek();i--)
					ret += "lcs -,-,-\n";
				ret+="goto -,-,l"+startOfLoop.peek()+"\n";
			}
		}
		else if(node instanceof BranchNode){
			BranchNode temp = (BranchNode) node;
			branchEnd.push(labCount++);		//claim label for end of branch
			labCount += temp.getNumCases();	//claim a label for each of the cases
			caseLabelOffset.push(1);
		}
		else if(node instanceof CaseNode){
			//for case n use the nth label claimed by the branch stmt
			ret+="lab -,-,l"+(labCount-caseLabelOffset.peek())+"\n";		//caseLabelOffset will ++ for every case
		}
		else if(node instanceof AssignmentNode){
			arrayFlag = true;
		}
	
				
		//depth first recursion
		for(Iterator<ASTNode> i = node.getChildren(); i.hasNext();){
			if(ignoreRecur)
				break;
			ret+=parseTree(i.next(), currentLevel, currentDisplacement, labCount);
			
			switch(inIf){
				case 1:
					ret+="iff "+varStrings.pop()+",-,l"+(labCount-1)+"\n";
					inIf = 0;
					break;
				case -2:
					ret+="iff "+varStrings.pop()+",-,l"+(labCount-2)+"\n";		//claimed 2 labels, -2 for else, -1 for end of if
					inIf = -1;break;
				case -1:
					ret+="goto -,-,l"+(labCount-1)+"\n";			//after ifstmt jump to end
					ret+="lab -,-,l"+(labCount-2)+"\n";				//label for start of else
					inIf = 0;
					break;
			}
			
			//logic for jumping to the correct case
			if(node instanceof BranchNode){
				if(!branchLogic){
					//create logic to jump to correct case
					int x = 1;
					for(CaseNode c:((BranchNode) node).getCases()){
						if(c.getCaseNum()!=-1){
							//use next available temp (can constantly overwrite as we only need it for jump check)
							ret+="eq "+c.getCaseNum()+","+this.varStrings.peek()+",("+currentLevel+","+currentDisplacement+")\n";		//check that the result of the branch expression matches the case num
							ret+="ift ("+currentLevel+","+currentDisplacement+"),-,l"+(labCount-x)+"\n";
							x++;
						}
					}
					//insert jump for default or end of branch
					ret+="goto -,-,l"+(labCount-x)+"\n";
					branchLogic = true;
				}
				else{
					caseLabelOffset.push(caseLabelOffset.pop()+1);	//increment so next case uses different label in claimed labels
				}
			}
			else if(node instanceof AssignmentNode && arrayFlag){
				arrayFlag = false;
			}
			
			if(shortCircuit && !sCDone){
				OperatorNode temp = (OperatorNode) node;
				if(temp.getOperator() == OperatorEnum.ANDTHEN){
					ret+= "asg "+this.varStrings.pop()+",-,("+currentLevel+","+(currentDisplacement-1)+")\n";
					this.varStrings.push("("+currentLevel+","+(currentDisplacement-1)+")");
					ret+= "iff "+this.varStrings.peek()+",-,l"+(labCount-1)+"\n";		//skip second expression if first is match for short circuit
				}
				else if(temp.getOperator() == OperatorEnum.ORELSE){
					ret+= "asg "+this.varStrings.pop()+",-,("+currentLevel+","+(currentDisplacement-1)+")\n";
					this.varStrings.push("("+currentLevel+","+(currentDisplacement-1)+")");
					ret+= "ift "+this.varStrings.peek()+",-,l"+(labCount-1)+"\n";		//skip second expression if first is match for short circuit
				}
				sCDone = true;
			}
		}
		
		
		if(node instanceof LiteralNode){
			this.varStrings.push(((LiteralNode) node).getStringRep());
		}
		else if(node instanceof VariableCallNode){												
			VariableCallNode temp = (VariableCallNode) node;
			if(temp.declaredArray()){	//check if array1
				if(!arrayFlag){
					ret+="fae ("+temp.getDeclaration().getLevel()+","+temp.getDeclaration().getDisplacement()+"),"+this.varStrings.pop()+",("+currentLevel+","+(currentDisplacement-1)+")\n";
					this.varStrings.push("("+currentLevel+","+(currentDisplacement-1)+")");
				}
				else{
					this.varStrings.push("("+temp.getDeclaration().getLevel()+","+temp.getDeclaration().getDisplacement()+")");
				}
			}
			else{
				this.varStrings.push("("+temp.getDeclaration().getLevel()+","+temp.getDeclaration().getDisplacement()+")");
			}
			inArray = false;
		}
		else if(node instanceof OperatorNode){		
			OperatorNode temp = (OperatorNode) node;
			//if inArray need to actually calculate			is in semantic analyzer but only remove here if time!!!!!!!!!!!!!!!!!!
			if(inArray){
				int var = 0;
				switch(temp.getOperator()){
					case PLUS: var = Integer.parseInt(varStrings.pop()) + Integer.parseInt(varStrings.pop());
						break;
					case MINUS: var = Integer.parseInt(varStrings.pop());		//need to pop this first as the top of the stack is the R side of the minus
						var = Integer.parseInt(varStrings.pop()) - var;			
						break;
					case MULT: var = Integer.parseInt(varStrings.pop()) * Integer.parseInt(varStrings.pop());
						break;
					case DIV: var = Integer.parseInt(varStrings.pop());			//need to pop this first as the top of the stack is the R side of the divide
						var = Integer.parseInt(varStrings.pop()) / var;			//error if this creates decimal!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						break;
					case UMINUS: var = Integer.parseInt(varStrings.pop())*-1;
						break;
					default:break;	//error
				}
				this.varStrings.push(var+"");
			}
			else{
				//check unary or binary
				if(temp.getOperator().isUnary()){
					ret+=temp.getOperator().getQuadString()+" "+this.varStrings.pop()+",-,("+currentLevel+","+(currentDisplacement-1)+")\n";
				}
				else{					
					ret+=temp.getOperator().getQuadString()+" "+this.varStrings.pop()+","+this.varStrings.pop()+",("+currentLevel+","+(currentDisplacement-1)+")\n";				
				}
				this.varStrings.push("("+currentLevel+","+(currentDisplacement-1)+")");
				if(shortCircuit){
					ret+="lab -,-,l"+(labCount-1)+"\n";
				}
			}	
		}
		else if(node instanceof ReturnNode){
			ReturnNode temp = (ReturnNode) node;
			
			for(int i = currentLevel;i>1;i--)
				ret+="lcs -,-,-\n";
			
			if(temp.hasExp()){
				ret+="retv "+this.currentNumParams+","+this.varStrings.pop()+",-\n";
			}
			else{
				ret+="ret "+this.currentNumParams+",-,-\n";
			}
		}
		else if(node instanceof FunctionCallNode  && !read){ //push params then add call
			FunctionCallNode temp = (FunctionCallNode) node;
			ArrayList<Boolean> paramIsRef = temp.getDeclaration().getParamIsRef();
			
			for(int i = 0; i < temp.getDeclaration().getNumParams();i++){
				if(paramIsRef.get(i)){
					ret+="arga ";
				}
				else{
					ret+="arg ";
				}
				ret+= this.varStrings.pop()+",-,-\n";
			}
			
			ret+="call "+myAdmin.strID(temp.getId())+",-,-\n";	//should this be the function name or the line to go to?????
			
			//set result temp as first of varStrings
			this.varStrings.push("("+currentLevel+","+(currentDisplacement-1)+")");
		}
		else if(node instanceof CompoundNode){
			if(!skipCmpd){				//used to skip function dec cmpd stmt
				ret+="lcs -,-,-\n";
			}
		}
		else if(node instanceof LoopNode){
			ret+="goto -,-,l"+startOfLoop.peek()+"\n";			//create a jump at the bottom of a loop to jump back to the top
			ret+="lab -,-,l"+endOfLoop.peek()+"\n";
			endOfLoop.pop();
			startOfLoop.pop();								
		}
		else if(node instanceof IfNode){						
			ret+="lab -,-,l"+(labCount-1)+"\n";		//insert label for end of if jump
		}
		else if(node instanceof AssignmentNode){
			//varStrings(0) should be the varcallnode that is being assigned, 1 should be the expression
			AssignmentNode temp = (AssignmentNode) node;
			if(temp.getAssignment().declaredArray()){
				String x = varStrings.pop();
				String a = varStrings.pop();
				
				ret+="tae "+x+","+this.varStrings.pop()+","+a+"\n";
			}
			else{
				ret+="asg "+this.varStrings.pop()+",-,"+this.varStrings.pop()+"\n";
			}
			
		}
		else if(node instanceof BranchNode){
			ret+="lab -,-,l"+branchEnd.pop()+"\n";		//label for end of branch stmt
			caseLabelOffset.pop();
		}
		else if(node instanceof CaseNode){
			//add jump to the end of branch stmt label
			ret+="goto -,-,l"+branchEnd.peek()+"\n";
		}		
		return ret;
	}
}