package compSemanticAnalyzer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import admin.Admin;
import admin.Tracer;
import compError.SemanticAnalyzer.ArrayIndexTypeError;
import compError.SemanticAnalyzer.ArraySizeTypeError;
import compError.SemanticAnalyzer.BadFunctionContextError;
import compError.SemanticAnalyzer.DoubleDefinitionError;
import compError.SemanticAnalyzer.DuplicateCaseError;
import compError.SemanticAnalyzer.IfExpressionError;
import compError.SemanticAnalyzer.IlegalAssignmentError;
import compError.SemanticAnalyzer.LoopStatementError;
import compError.SemanticAnalyzer.MainError;
import compError.SemanticAnalyzer.MainErrorEnum;
import compError.SemanticAnalyzer.MissingReturnError;
import compError.SemanticAnalyzer.NonStaticArraySizeError;
import compError.SemanticAnalyzer.OperatorTypeMismatchError;
import compError.SemanticAnalyzer.ParameterError;
import compError.SemanticAnalyzer.ReferenceParamExpressionError;
import compError.SemanticAnalyzer.ReturnTypeError;
import compError.SemanticAnalyzer.TypeMismatchError;
import compError.SemanticAnalyzer.UndefinedFunctionError;
import compError.SemanticAnalyzer.UndefinedVariableError;
import compParser.ast.*;

/**
 * This class takes the root of an AST created by the CompParser and checks that it matches the semantics of C*16 and annotates it
 * with information such as types and matching variables to their declarations.
 * 
 * @author Bryan Storie
 * @author Devon Plourde
 */
public class CompSemanticAnalyzer {
	
	Admin myAdmin;
	Tracer myTracer;

	LinkedList<Hashtable<Integer,DeclarationNode>> symbolTable = new LinkedList<Hashtable<Integer,DeclarationNode>>();
	HashSet<Integer> errorTable = new HashSet<Integer>();
	
	/**
	 * Create a new analyzer and link it to the admin and the admin's tracer.
	 * @param a The Admin of the Compiler.
	 * @param t	The Tracer used by all of the other parts of the Compiler
	 */
	public CompSemanticAnalyzer(Admin a, Tracer t){
		this.myAdmin = a;
		this.myTracer = t;
		resetLocalVariables();
	}
	
	/**
	 * Resets the Semantic Analyzer to prepare it for a new tree to analyze
	 */
	public void resetLocalVariables(){
		symbolTable.clear(); 
		errorTable.clear();
		inFunDec = false;
		inCallStmt = false;
		inArgs = 0;
		inArraySize = false;
		arrayErr = false;
		arrayId = "";
		expectedReturn = null;
		expectedReturnFunc = "";
		inLoop = false;
	}
	
	/**
	 * Takes the root of an AST and adds annotations for type and instance-declaration links and checks its semantics.
	 * @param root The root of the AST to be checked
	 */
	public void analyze(ASTNode root){
		resetLocalVariables();
		traversalInit(root);
		traversalFull(root);
	}
	
	/**
	 * Checks if an index has an entry at any level of the symbol table.
	 * @param index The index to be checked
	 * @return The node linked to the index or null if it isn't in the symbol table
	 */
	private DeclarationNode retrieve(int index){
		DeclarationNode ret = null;
		boolean found = false;
		
		for(int i = 0; i < symbolTable.size() && !found; ++i){
			Hashtable<Integer,DeclarationNode> table = symbolTable.get(i);
			if(table.containsKey(index)){
				ret = table.get(index);
				found = true;
			}
		}
		return ret;
	}
	
	/**
	 * A method which iterates across the top level of the AST to collect all global declarations and check that Main matches
	 * the required Main template.
	 * @param root The root of the AST to be analyzed
	 */
	private void traversalInit(ASTNode root)
	{		
		Hashtable<Integer,DeclarationNode> table = new Hashtable<Integer,DeclarationNode>();
		Iterator<ASTNode> i = root.getChildren();
		DeclarationNode n = null;
		while(i.hasNext()){
			n = (DeclarationNode) i.next();
			
			if(table.put(n.getId(), n) != null){
				//throw double definition error
				myAdmin.errorM(new DoubleDefinitionError(myTracer.getTextSA(), myTracer.getLineSA(), myAdmin.strID(n.getId())));
			}
		}
		
		//Checking the last declaration that was added. This has to be a function called "main" according to the
		//rules of the language. "Main" must also have a return type as INT and it's parameters must be VOID.

		if(n != null && n instanceof FunctionDeclarationNode && myAdmin.strID(n.getId()).equals("main")){
			if(!(n.getType() == TypeEnum.INT)){	
				myAdmin.errorM(new MainError(MainErrorEnum.RETURN));
			}
			
			if(!((FunctionDeclarationNode) n).getParameters().isEmpty()&&((FunctionDeclarationNode) n).getParameters().get(0).getType() != TypeEnum.VOID){
			myAdmin.errorM(new MainError(MainErrorEnum.PARAMETER));
			}
		}
		else{
			myAdmin.errorM(new MainError(MainErrorEnum.MISSING));
		}
		
		symbolTable.addFirst(table);		
	}	
	
	/**
	 * A method that checks that every instance corresponds to a definition and that no variables are declared multiple times within
	 * the same scope.
	 * @param node The node to check.
	 */
	private void checkScoping(ASTNode node){
		if( node instanceof FunctionDeclarationNode){
			symbolTable.addFirst(new Hashtable<Integer,DeclarationNode>());
		}
		else if( node instanceof CompoundNode){
			if(inFunDec){
				inFunDec = false;
			}
			else{
				//create a new stack frame as we have entered a new scope
				symbolTable.addFirst(new Hashtable<Integer,DeclarationNode>());		
			}
		}
		else if(node instanceof VariableDeclarationNode){
			DeclarationNode temp = (DeclarationNode) node;
			if(!symbolTable.peek().containsKey(temp.getId())){
				symbolTable.peek().put(temp.getId(), temp);
			}
			else if(symbolTable.size()==1){
				//do nothing as this is at global level and has already been handled
			}
			else{
				//variable has already been declared at this scope
				myAdmin.errorM(new DoubleDefinitionError(myTracer.getTextSA(), myTracer.getLineSA(), myAdmin.strID(temp.getId())));
			}
		}
		else if(node instanceof VariableCallNode){
			VariableCallNode temp = (VariableCallNode) node;
			DeclarationNode n = retrieve(temp.getId());
						
			//check if it exists in the symbol table and if it is the correct type of declaration(function or variable)
			//if so, add to callNode's declaration field
			if(n != null && n instanceof VariableDeclarationNode){
				temp.setDeclaration((VariableDeclarationNode) n);
			}
			//if not, undeclared identifier error
			else{
				if(!errorTable.contains(temp.getId())){
					myAdmin.errorM(new UndefinedVariableError(myTracer.getTextSA(), myTracer.getLineSA(), myAdmin.strID(temp.getId())));
					errorTable.add(temp.getId());
				}
			}
		}
		else if(node instanceof FunctionCallNode){
			FunctionCallNode temp = (FunctionCallNode) node;
			DeclarationNode n = symbolTable.peekLast().get(temp.getId());
			
			//check if it exists in the symbol table (go right to global vars)
			//if so, add to callNode's declaration field
			if(n != null && n instanceof FunctionDeclarationNode){
				temp.setDeclaration((FunctionDeclarationNode)n);
			}
			//if not, undeclared function error
			else{				
				if(!errorTable.contains(temp.getId())){
					myAdmin.errorM(new UndefinedFunctionError(myTracer.getTextSA(), myTracer.getLineSA(), myAdmin.strID(temp.getId())));
					errorTable.add(temp.getId());
				}
			}
		}
	}
	
	/**
	 * Fills in the type of expression nodes based on their children.  Also checks that the types of an operators operands match its expected type, 
	 * that the expression in an Assignment matches the declared type of the variable, and that the type of the expression representing an array's
	 * size is of type Int. 
	 * @param node The node to check
	 */
	private void checkTypes(ASTNode node){
		//check that operands match operator expected type
		if(node instanceof ExpressionNode && ((ExpressionNode) node).getType()== null){
			OperatorNode tRoot = (OperatorNode) node;
			TypeEnum ex = ExpectedOperatorTypes.getExpectedType(tRoot.getOperator());
			TypeEnum type = null;
			for(Iterator<ASTNode> i = tRoot.getChildren(); i.hasNext();){
				Expression x = (Expression) i.next();
				//types match
				if(ex.matches(x.getType())){
					if(type==null){
						type = x.getType();
					}
				}
				//one operand is UNIV
				else if(x.getType()==TypeEnum.UNIV){
					type = TypeEnum.UNIV;
				}
				//types don't match
				else{
					myAdmin.errorM(new OperatorTypeMismatchError(myTracer.getTextSA(), myTracer.getLineSA(), tRoot.getStringRep(),x.getStringRep() , x.getType()));
					type = TypeEnum.UNIV;
				}
			}
			//set the parent type based on children's types
			tRoot.setType(type);
			
			//check that if an operator can take multiple types, the types match ((bool and bool) or (int and int))
			if(ex == TypeEnum.BOTH && tRoot instanceof BinaryOpNode){
				BinaryOpNode temp = (BinaryOpNode) tRoot;
				if(temp.getLOperand().getType()!=temp.getROperand().getType()&&temp.getLOperand().getType()!=TypeEnum.UNIV&&temp.getROperand().getType()!=TypeEnum.UNIV){
					myAdmin.errorM(new TypeMismatchError(myTracer.getTextSA(), myTracer.getLineSA(), temp.getOperator(), temp.getLOperand().getType(), temp.getROperand().getType()));
					tRoot.setType(TypeEnum.UNIV);
				}
				else{
					tRoot.setType(TypeEnum.BOOL);
				}
			}
		}
		
		//check that the expression's type matches the id's in an assignment
		if(node instanceof AssignmentNode){
			AssignmentNode tRoot = (AssignmentNode) node;
			if(tRoot.getExpression().getType()!=TypeEnum.UNIV&&tRoot.getExpression().getType()!=TypeEnum.VOID&&
					tRoot.getAssignment().getType()!=tRoot.getExpression().getType()){
				myAdmin.errorM(new IlegalAssignmentError(myTracer.getTextSA(), myTracer.getLineSA(), tRoot.getAssignment().getLexem(), tRoot.getAssignment().getType(), tRoot.getExpression().getType()));
			}
		}
		
		//check that the expression representing the size of an array is an int
		else if(node instanceof VariableDeclarationNode && !(node instanceof ParamDeclarationNode)&&((VariableDeclarationNode) node).isArray()){
			Expression ex = ((VariableDeclarationNode) node).getSizeExp();
			if(ex.getType()==TypeEnum.BOOL){
				myAdmin.errorM(new ArraySizeTypeError(myTracer.getTextSA(), myTracer.getLineSA(), ((VariableDeclarationNode)node).getLexem()));
			}
		}
		else if(node instanceof VariableCallNode && ((VariableCallNode) node).declaredArray()){
			Expression ex = ((VariableCallNode) node).getArraySize();
			if(ex.getType()==TypeEnum.BOOL){
				myAdmin.errorM(new ArrayIndexTypeError(myTracer.getTextSA(), myTracer.getLineSA(), ((VariableCallNode)node).getLexem()));
			}
		}
	}
	
	/**
	 * Checks that the number of arguments in a function call match the number in the declaration, that all of the types of the arguments
	 * match the parameter's expected types, and that the arguments for Ref parameters are L-expressions.
	 * @param node The node to check
	 */
	private void checkParams(ASTNode node){
		if(node instanceof FunctionCallNode && !errorTable.contains(((FunctionCallNode)node).getId())){
			FunctionCallNode fRoot = (FunctionCallNode) node;
			ArrayList<Parameter> expectedParam = fRoot.getExpectedParameters();
			ArrayList<Expression> arguments = fRoot.getArguments();
			
			//check number of params match
			if(expectedParam.size()==arguments.size()){
				boolean error = false;
				for(int i = 0; i < expectedParam.size(); i++){
					//check individual types match or that the arg is UNIV
					if(expectedParam.get(i).getType()!=arguments.get(i).getType()&&arguments.get(i).getType()!=TypeEnum.UNIV){	//if var is undeclared will have UNIV type
						error = true;//one parameter's types don't match
					}
					//check that all ref parameters are l-expresionss
					if(expectedParam.get(i).isRef()&&!(arguments.get(i) instanceof VariableCallNode)){
						myAdmin.errorM(new ReferenceParamExpressionError(myTracer.getTextSA(), myTracer.getLineSA(), fRoot.getLexem(), expectedParam.get(i).getLexem()));
					}
				}
				if(error){
					//report paramater mismatch
					myAdmin.errorM(new ParameterError(myTracer.getTextSA(), myTracer.getLineSA(), myAdmin.strID(fRoot.getId()), fRoot));
				}
			}
			else{
				//num params don't match
				myAdmin.errorM(new ParameterError(myTracer.getTextSA(), myTracer.getLineSA(), myAdmin.strID(fRoot.getId()), fRoot));
			}
		}
	}
	
	/**
	 * Checks that void returning functions only appear in Call Statements, that non-void returning statements don't appear in Call Statements,
	 * and that the type of the expression in a return statement matches it's function's declared return type.
	 * @param node The node to be checked
	 */
	private void checkReturn(ASTNode node){
		//check that non-void returning functions are not in call statements
		if(node instanceof CallStmtNode){
			FunctionCallNode tRoot = ((CallStmtNode) node).getFunctionCall();
			if(tRoot.getType()!=TypeEnum.VOID && !errorTable.contains(tRoot.getId())){
				myAdmin.errorM(new BadFunctionContextError(myTracer.getTextSA(), myTracer.getLineSA(), myAdmin.strID(tRoot.getId()), tRoot.getType()));
			}
		}
		
		//check that void returning functions are only in call stmts (and not one of the args in a call stmt which is a different error)
		if(node instanceof FunctionCallNode){
			FunctionCallNode tRoot = (FunctionCallNode) node;
			if(tRoot.getType() == TypeEnum.VOID && !errorTable.contains(tRoot.getId())){
				//can't have void returning functions anywhere but CallStmt and not in the args of a call stmt
				if(!inCallStmt&&inArgs==0){
					myAdmin.errorM(new BadFunctionContextError(myTracer.getTextSA(), myTracer.getLineSA(), myAdmin.strID(tRoot.getId()), tRoot.getType()));
				}
			}
		}
		
		//Check that return stmt matches expected return type
		if(node instanceof ReturnNode){
			foundReturn = true;
			ReturnNode tRoot = (ReturnNode) node;
			if(!tRoot.hasExp()&&expectedReturn != TypeEnum.VOID){
				myAdmin.errorM(new ReturnTypeError(myTracer.getTextSA(), myTracer.getLineSA(),expectedReturnFunc, expectedReturn, TypeEnum.VOID ));
			}
			else if(tRoot.hasExp()&& tRoot.getExpression().getType()!=expectedReturn){
				myAdmin.errorM(new ReturnTypeError(myTracer.getTextSA(), myTracer.getLineSA(),expectedReturnFunc, expectedReturn, tRoot.getExpression().getType()));
			}
		}
	}
	
	/**
	 * Checks that expression representing array size is static (expression with no function or variable calls) and that
	 * if a variable is declared as an array, it's calls have an index and vice versa.
	 * @param node The node to check
	 */
	private void checkArrayStatic(ASTNode node){
		if(inArraySize && node instanceof CallNode){
			myAdmin.errorM(new NonStaticArraySizeError(myTracer.getTextSA(), myTracer.getLineSA(), arrayId));
			arrayErr = true;			
		}
	}
		
	/**
	 * Fills in the arraySize field of operators which will be used to compress the expression representing
	 * the array size/index calculation in the code generation stage
	 * @param root The node to check
	 */
	private void calcArrayIndSize(ASTNode root) {
		//if this condition fails either there will be a type error or the node isn't an operator and already has an array val
		if(inArraySize && root instanceof OperatorNode && !arrayErr && ((OperatorNode)root).getType()==TypeEnum.INT){
			OperatorNode node = (OperatorNode) root;
		
			//can only be uminus here as we checked type
			if(node.getOperator().isUnary()){
				Expression exp = ((UnaryOpNode)node).getOperand();
				if(exp instanceof NumNode){
					node.setArrayVal(-1*((NumNode)exp).getArrayVal());
				}
				else{
					node.setArrayVal(-1*((OperatorNode)exp).getArrayVal());
				}
			}
			else{
				Expression l = ((BinaryOpNode)node).getLOperand();
				int lVal = 0;
				if(l instanceof NumNode){
					lVal = ((NumNode)l).getArrayVal();
				}
				else{
					lVal = ((OperatorNode)l).getArrayVal();
				}
				
				Expression r = ((BinaryOpNode)node).getROperand();
				int rVal=0;
				if(r instanceof NumNode){
					rVal = ((NumNode)r).getArrayVal();
				}
				else{
					rVal = ((OperatorNode)r).getArrayVal();
				}
				switch(node.getOperator()){
				case PLUS:
					node.setArrayVal(lVal+rVal);
					break;
				case MINUS:
					node.setArrayVal(lVal-rVal);
					break;
				case DIV:
					//decimals handled implicitly by int division
					node.setArrayVal(lVal/rVal);
					break;
				case MULT:
					node.setArrayVal(lVal*rVal);
					break;
				case MOD:
					node.setArrayVal(lVal%rVal);
					break;
				default: //err inapropriate operator
					//shouldn't be possible due to type check in earlier if
				}
			}
		}
		else if(root instanceof VariableDeclarationNode && ((VariableDeclarationNode)root).isArray()){
			VariableDeclarationNode temp = (VariableDeclarationNode) root;
			if(temp.getSizeExp() instanceof NumNode){
				temp.setSize(((NumNode)temp.getSizeExp()).getArrayVal());
			}
			else{
				temp.setSize(((OperatorNode)temp.getSizeExp()).getArrayVal());
			}
		}
		else if(root instanceof VariableCallNode && ((VariableCallNode)root).declaredArray()){
			VariableCallNode temp = (VariableCallNode) root;
			if(temp.getArraySize() instanceof NumNode){
				temp.setIndex(((NumNode)temp.getArraySize()).getArrayVal());
			}
			else{
				temp.setIndex(((OperatorNode)temp.getArraySize()).getArrayVal());
			}
		}
	}
	

	/**
	 * Check that continue and exit nodes are only found within loops.
	 * @param node The node to check
	 */
	private void checkLoopOnly(ASTNode node){
		if(!inLoop){
			if(node instanceof DatalessNode){
				DatalessEnum tRoot = ((DatalessNode) node).getNodeType();
				if(tRoot==DatalessEnum.CONTINUE||tRoot==DatalessEnum.EXIT){
					myAdmin.errorM(new LoopStatementError(myTracer.getTextSA(), myTracer.getLineSA(), tRoot));
				}
			}
		}
	}
	
	/**
	 * Checks that the expression of an if statment is of type Bool
	 * @param node the node to check
	 */
	private void checkIf(ASTNode node){
		if(node instanceof IfNode){
			Expression eNode = ((IfNode) node).getExpression();
			if(eNode.getType()==TypeEnum.INT){	//don't care in UNIV or NULL
				myAdmin.errorM(new IfExpressionError(myTracer.getTextSA(), myTracer.getLineSA()));
			}
		}
	}
	
	private Hashtable<Integer, Integer> caseHash = new Hashtable<Integer, Integer>();
	/**
	 * Checks that a branch statement has at most one case statement per number
	 * @param node The node to check
	 */
	private void checkCase(ASTNode node){
		if(node instanceof CaseNode){
			CaseNode cNode = (CaseNode) node;
			Integer i = caseHash.get(cNode.getCaseNum());
			if(!(i==null||i>1)){
				myAdmin.errorM(new DuplicateCaseError(myTracer.getTextSA(), myTracer.getLineSA(), cNode.getCaseNum()));
			}
			if(i!=null){
				caseHash.put(cNode.getCaseNum(), caseHash.get(cNode.getCaseNum())+1);
			}
			else{
				caseHash.put(cNode.getCaseNum(), 1);
			}
		}
	}
	
	//flags used during SA
	private boolean inFunDec;
	private boolean inCallStmt;
	private int inArgs;
	private boolean inArraySize;
	private boolean arrayErr;
	private String arrayId;
	private TypeEnum expectedReturn;
	private String expectedReturnFunc;
	private boolean foundReturn = false;
	private boolean inLoop;
	
	/**
	 * Traverses the entire tree recursively calling all of the various semantic checks and updating context flags 
	 * for their use.
	 * @param root The node to check
	 */
	private void traversalFull(ASTNode root){
		if(root instanceof FunctionDeclarationNode || (root instanceof VariableDeclarationNode && !(root instanceof Parameter)) || (root instanceof Statement && !(root instanceof CompoundNode))){
			myTracer.incrementStmt();
		}
		
		//set context flags before checks
		if( root instanceof FunctionDeclarationNode){
			inFunDec = true;
			expectedReturn = ((FunctionDeclarationNode) root).getType();
			expectedReturnFunc = ((FunctionDeclarationNode) root).getLexem();
		}
		else if(root instanceof CallStmtNode){
			inCallStmt = true;
		}
		
		//scoping (before recursion)
		checkScoping(root);
		
		//set context flags after checks(for next level of recursion)
		if(root instanceof FunctionCallNode)
			inArgs++;
		else if(root instanceof VariableDeclarationNode){
			inArraySize = true;
			arrayId = ((VariableDeclarationNode) root).getLexem();
		}
		else if(root instanceof VariableCallNode){
			inArraySize = true;
		}
		else if(root instanceof LoopNode)
			inLoop = true;
			
		
		//Loop for depth-first recursion of AST-----RECURSION IS HERE
		for(Iterator<ASTNode> i = root.getChildren(); i.hasNext();){
			traversalFull(i.next());
		}
		
		//set context flags back from recursion & check for return stmt
		if(root instanceof FunctionCallNode)
			inArgs--;
		else if(root instanceof VariableDeclarationNode || root instanceof VariableCallNode){
			inArraySize = false;																		
			/*//evaluate array size!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			calcArraySize(root);*/
			arrayErr = false;
		}
		else if(root instanceof LoopNode)
			inLoop = false;
		else if(root instanceof BranchNode){
			caseHash.clear();
		}
		else if(root instanceof FunctionDeclarationNode){
			if(!foundReturn & expectedReturn != TypeEnum.VOID)
				myAdmin.errorM(new MissingReturnError(expectedReturnFunc ));
			expectedReturn = null;
			foundReturn = false;
		}
		
		//remove level of symbol table if exiting compound (changing scope)
		if(root instanceof CompoundNode){
			symbolTable.pop();
		}
		
		//typing(after recursion)
		checkTypes(root);
		
		//parameters(after recursion)
		checkParams(root);
		
		//check that return type matches context (after recursion)
		checkReturn(root);
		
		//check that array size values are static (when doesn't matter)
		checkArrayStatic(root);
		
		calcArrayIndSize(root);
		
		//check that continue and exit are only inside a loop
		checkLoopOnly(root);
		
		//check that the expression of an if node is of type bool(after type checking)
		checkIf(root);
		
		//check that case statement numbers are disjoint
		checkCase(root);
		
		//set context flags back from start of this call
		if(root instanceof CallStmtNode){
			inCallStmt = false;
		}
	}
}
