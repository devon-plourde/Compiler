package compParser;

import java.util.LinkedList;
import java.util.ListIterator;

import admin.Admin;
import admin.Tracer;
import compError.Parser.SyntaxError;
import compParser.ast.*;
import compScanner.token.Token;
import compScanner.token.TokenEnum;

/**
 * Creates an AST from a given LinkedList of Tokens.  If a token doesn't fit the syntax rules the Parser will
 * report an error to Admin and try to recover from the error using the set of synchronization symbols.  
 * See the c*16 syntax specification for the grammatical rules this class is based on.
 * @author Bryan Storie
 * @author Devon Plourde
 */
public class CompParser {
	
	private Admin myAdmin = null;
	private ListIterator<Token> tokenIterator = null;
	private Token look = null;
	private boolean error = false;
	private Tracer myTracer;
	private boolean panicMode;
	private SyncList syncList;
	

	
	/**
	 * Create a new Parser linked to Admin a
	 * @param a Calling Admin
	 * @param t Tracer used to create trace
	 */
	public CompParser(Admin a, Tracer t){
		this.myAdmin = a;
		this.myTracer = t;
		syncList = new SyncList();
		resetLocalVariables();
		this.resetLocalVariables();
	}
	
	/**
	 * Resets all internal variables of the Parser
	 */
	public void resetLocalVariables(){
		this.tokenIterator = null;
		this.look = null;
		this.error = false;
		this.panicMode = false;
		syncList.resetLocalVariables();
	}
	
	/**
	 * Creates an Abstract Syntax Tree based on the c*16 syntax specification from the given list of tokens
	 * @param t list of tokens
	 * @return the root of the AST
	 */
	public ASTNode parse(LinkedList<Token> t){
		this.tokenIterator = t.listIterator();
		getNextT();
		return program();
	}
	
	private void getNextT(){
		look = tokenIterator.next();
	}
	
	/**
	 * Checks if the lookahead token matches the expected type e.  If it does then the token is returned and the lookahead
	 * is advanced.  If not then an error is reported to myAdmin and <i>error</i> is set to true which ceases construction
	 * of the AST before the token is returned.  Once an error is encountered Panic Mode is entered.  In this mode the parser
	 * attempts to recover from the error that was encountered by advancing both the parser's execution and iterating the
	 * lookahead until one of the two encounter a symbol in the synchronization set.  Once one finds a synchronization symbol
	 * the other continues iterating until it encounters the same symbol.  Once this occurs Panic Mode is exited and regular
	 * parsing continues but the AST is no longer constructed.
	 * @param e Token type to match
	 * @return the token that was the lookahead or an error token if the token read wasn't used
	 */
	private Token match(TokenEnum e){	//returns token that it just checked
		if(!panicMode){ //Enters if not in panic mode (normal behaviour).
			Token ret = null;
			if(look.getTokenName() == e){
				myTracer.addToParserTrace("Match: "+look);
				if(e!=TokenEnum.ENDFILE){
					ret = look;
					getNextT();
				}
			}
			
			else{
				//SYNTAX ERROR
				myAdmin.errorM(new SyntaxError(myTracer.getCurrentLine(), myTracer.getCurrentLineNum(), e, look));	//just a general syntax error for now
				error = true;
				ret = look;

				myTracer.addToParserTrace("Syntax Error: "+look.getTokenName()+"  --  Expected: "+e);
				this.panicMode = true;
				
				myTracer.addToParserTrace("***ENTERED PANIC MODE");
			}
			return ret;
		}
				
		else{ //Panic mode
			
			if(syncList.contains(look.getTokenName())){  /*look.getTokenName().isAStopSymbol*/
				if(e==look.getTokenName()){  /*if e IS a stop symbol*/
					this.panicMode = false; //We are back on track and can leave panic mode
					myTracer.addToParserTrace("***RECOVERED FROM PANIC MODE on: "+e);

					return match(e);
				}
				else{  //if e is NOT stop symbol
					//bogus return to move parsing ahead and find stop symbol.
					return new Token(TokenEnum.ERROR, 0, "");
				}
			}
			else{
				if(syncList.contains(e)){  /*e IS a stop symbol*/
					//move look ahead until stop symbol is found
					myTracer.addToParserTrace("Recovery: not a sync symbol: "+look);

					getNextT();
					return match(e);
				}
				else{  //if e IS NOT a stop symbol
					//move 'look' forward to find stop symbol.
					myTracer.addToParserTrace("Recovery: not a sync symbol: "+look);
					getNextT();
					//bogus return to move parsing ahead and find stop symbol.
					return new Token(TokenEnum.ERROR, 0, "");
				}
			}
		}
	}
	
	/**
	 * Calls addChild for each of the children in children
	 * @param parent
	 * @param children
	 */
	private void addChildren(ASTNode parent, LinkedList<ASTNode> children){
		if(children!=null){
			for(ASTNode n: children){
				addChild(parent, n);
			}
		}
	}
	
	/**
	 * Adds child as a child of parent as long as an error hasn't been encountered in the parse (error!=true)
	 * @param parent
	 * @param child
	 */
	private void addChild(ASTNode parent, ASTNode child){
		if(!error){
			parent.addChild(child);
		}
	}

	private ASTNode program(){
		myTracer.addToParserTrace("Entering Program");
		syncList.add(FollowSets.PROGRAM);
		DatalessNode root = new DatalessNode(DatalessEnum.PROGRAM);//create root node
		addChildren(root, declaration());
		while(look.getTokenName() != TokenEnum.ENDFILE){
			addChildren(root, declaration());
		}
		match(TokenEnum.ENDFILE);
		syncList.remove(FollowSets.PROGRAM);
		myTracer.addToParserTrace("Exiting Program");
		if(!error){
			return root;
		}
		else{
			return null;
		}
	}
	
	private LinkedList<ASTNode> declaration(){
		myTracer.addToParserTrace("Entering Declaration");
		syncList.add(FollowSets.DEC);
		LinkedList<ASTNode> ret = new LinkedList<ASTNode>();
		if(look.getTokenName() == TokenEnum.VOID){
			match(TokenEnum.VOID);
			int t = match(TokenEnum.ID).getAttribute();
			FunctionDeclarationNode node = new FunctionDeclarationNode(TypeEnum.VOID, t, myAdmin.strID(t));
			addChildren(node, fun_dec_tail());	//pass func node to this
			ret.add(node);
		}
		else{
			TypeEnum temp = nonvoid_specifier();
			int t = match(TokenEnum.ID).getAttribute();
			VariableDeclarationNode node = new VariableDeclarationNode(temp, t,myAdmin.strID(t),false);	//don't add as child of parent because may actually need to be a function node
			ret = dec_tail(node);
		}
		syncList.remove(FollowSets.DEC);
		myTracer.addToParserTrace("Exiting Declaration");
		return ret;
	}
	
	private TypeEnum nonvoid_specifier(){//return type token
		TypeEnum temp;
		myTracer.addToParserTrace("Entering Nonvoid_Specifier");
		syncList.add(FollowSets.NONVOIDSPEC);
		if(look.getTokenName() == TokenEnum.INT){
			match(TokenEnum.INT);
			temp =  TypeEnum.INT;
		}
		else{
			match(TokenEnum.BOOL);
			temp = TypeEnum.BOOL;
		}
		syncList.remove(FollowSets.NONVOIDSPEC);
		myTracer.addToParserTrace("Exiting Nonvoid_Specifier");
		return temp;
		
	}
	
	private LinkedList<ASTNode> dec_tail(VariableDeclarationNode node){	//take placeholder node
		myTracer.addToParserTrace("Entering Dec_Tail");
		syncList.add(FollowSets.DEC);
		LinkedList<ASTNode> ret = new LinkedList<ASTNode>();
		if(look.getTokenName() == TokenEnum.LPAREN){
			FunctionDeclarationNode n = new FunctionDeclarationNode(node.getType(), node.getId(),  node.getLexem());
			addChildren(n, fun_dec_tail());	//create func node from placeholder and pass
			ret.add(n);
		}
		else{			
			ret= var_dec_tail(node); //create var node from placeholder and pass
		}
		syncList.remove(FollowSets.DEC);
		myTracer.addToParserTrace("Exiting Dec_Tail");
		return ret;
	}
	
	private LinkedList<ASTNode> var_dec_tail(VariableDeclarationNode first){
		myTracer.addToParserTrace("Entering Var_Dec_Tail",true);
		syncList.add(FollowSets.VARDECTAIL);
		LinkedList<ASTNode> ret = new LinkedList<ASTNode>();
		if(look.getTokenName() == TokenEnum.LSQR){//this is for referencing items in a list (ie. a[x])
			match(TokenEnum.LSQR);
			first = new VariableDeclarationNode(first.getType(), first.getId(),first.getLexem(), true);
			addChild(first,add_exp());
			match(TokenEnum.RSQR);
		}
		ret.add(first);
		while(look.getTokenName() == TokenEnum.COMMA){
			match(TokenEnum.COMMA);
			ret.add(var_name(first.getType()));
		}
		match(TokenEnum.SEMI);
		syncList.remove(FollowSets.VARDECTAIL);
		myTracer.addToParserTrace("Exiting Var_Dec_Tail");
		return ret;
	}
	
	private ASTNode var_name(TypeEnum returnType){//create variable node and fill with id
		myTracer.addToParserTrace("Entering Var_Name");
		syncList.add(FollowSets.VARNAME);
		int t = match(TokenEnum.ID).getAttribute();
		VariableDeclarationNode node = null;
		if(look.getTokenName() == TokenEnum.LSQR){
			node = new VariableDeclarationNode(returnType, t,myAdmin.strID(t),true);
			match(TokenEnum.LSQR);
			addChild(node, add_exp());//pass variable node  In this case this is like id[index] where index is the add_exp
			match(TokenEnum.RSQR);
		}
		else{
			node = new VariableDeclarationNode(returnType, t,myAdmin.strID(t),false);
		}
		syncList.remove(FollowSets.VARNAME);
		myTracer.addToParserTrace("Exiting Var_Name");
		return node;
	}
	
	private LinkedList<ASTNode> fun_dec_tail(){
		myTracer.addToParserTrace("Entering Fun_Dec_Tail",true);
		syncList.add(FollowSets.DEC);
		LinkedList<ASTNode> ret = new LinkedList<ASTNode>();
		match(TokenEnum.LPAREN);
		ret.addAll(params());//pass func node to params
		match(TokenEnum.RPAREN);
		ret.add(compound_stmt());//create compound node and add to func children
		syncList.remove(FollowSets.DEC);
		myTracer.addToParserTrace("Exiting Fun_Dec_Tail");
		return ret;
	}
	
	private LinkedList<ASTNode> params(){
		myTracer.addToParserTrace("Entering Params");
		syncList.add(FollowSets.PARAMS);
		LinkedList<ASTNode> ret = new LinkedList<ASTNode>();
		if(look.getTokenName() == TokenEnum.VOID){
			match(TokenEnum.VOID);	//create nothing
		}
		else{
		ret.add(param());//pass func node to each param call
			while(look.getTokenName() == TokenEnum.COMMA){
				match(TokenEnum.COMMA);
				ret.add(param());
			}
		}
		syncList.remove(FollowSets.PARAMS);
		myTracer.addToParserTrace("Exiting Params");
		return ret;
	}
	
	private DeclarationNode param(){
		myTracer.addToParserTrace("Entering Param");
		syncList.add(FollowSets.PARAM);
		if(look.getTokenName() == TokenEnum.REF){
			match(TokenEnum.REF);
			TypeEnum temp = nonvoid_specifier();
			int t = match(TokenEnum.ID).getAttribute();
			RefParamDeclarationNode node = new RefParamDeclarationNode(temp,t,myAdmin.strID(t));	//create ref param node and add to func children
			syncList.remove(FollowSets.PARAM);
			myTracer.addToParserTrace("Exiting Param");
			return node;
		}
		else{
			TypeEnum temp = nonvoid_specifier();
			int t = match(TokenEnum.ID).getAttribute();
			ParamDeclarationNode node = null;
			if(look.getTokenName() == TokenEnum.LSQR){
				node = new ParamDeclarationNode(temp,t,myAdmin.strID(t),true);
				match(TokenEnum.LSQR);
				match(TokenEnum.RSQR);
			}
			else{
				node = new ParamDeclarationNode(temp,t,myAdmin.strID(t), false);
			}
			syncList.remove(FollowSets.PARAM);
			myTracer.addToParserTrace("Exiting Param");
			return node;
		}
	}
	
	private ASTNode statement(){//just passes gotten node to whichever option
		myTracer.addToParserTrace("Entering Statement");
		syncList.add(FollowSets.STMT);
		ASTNode temp;
		if(look.getTokenName() == TokenEnum.ID){
			temp = id_stmt();
		}
		else if(look.getTokenName() == TokenEnum.LCRLY){
			temp = compound_stmt();
		}
		else if(look.getTokenName() == TokenEnum.IF){
			temp = if_stmt();
		}
		else if(look.getTokenName() == TokenEnum.LOOP){
			temp = loop_stmt();
		}
		else if(look.getTokenName() == TokenEnum.EXIT){
			temp = exit_stmt();
		}
		else if(look.getTokenName() == TokenEnum.CONTINUE){
			temp = continue_stmt();
		}
		else if(look.getTokenName() == TokenEnum.RETURN){
			temp = return_stmt();
		}
		else if(look.getTokenName() == TokenEnum.BRANCH){
			temp = branch_stmt();
		}
		else{
			temp = null_stmt();
		}
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Exiting Statement");
		return temp;
	}

	private ASTNode id_stmt(){
		myTracer.addToParserTrace("Entering Id_Stmt",true);
		syncList.add(FollowSets.STMT);
		int t = match(TokenEnum.ID).getAttribute();
		VariableCallNode node = new VariableCallNode(t,myAdmin.strID(t));	//may need temporary here as well(can lead to function call or id assignment)
		ASTNode ret = id_stmt_tail(node);//pass temporary
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Exiting Id_Stmt");
		return ret;
	}
	
	private ASTNode id_stmt_tail(VariableCallNode node){//get temp node
		myTracer.addToParserTrace("Entering Id_Stmt_Tail");
		syncList.add(FollowSets.STMT);
		if(look.getTokenName() == TokenEnum.LPAREN){
			CallStmtNode state = new CallStmtNode();
			FunctionCallNode func = new FunctionCallNode(node.getId(), node.getLexem());
			addChild(state, func);
			addChildren(func,call_stmt_tail());
			syncList.remove(FollowSets.STMT);
			myTracer.addToParserTrace("Exiting Id_Stmt_Tail");
			return state;
		}
		else{	
			syncList.remove(FollowSets.STMT);
			myTracer.addToParserTrace("Exiting Id_Stmt_Tail");
			return assign_stmt_tail(node); 
		}
	}
	
	private AssignmentNode assign_stmt_tail(VariableCallNode node){
		myTracer.addToParserTrace("Entering Assign_Stmt_Tail");
		syncList.add(FollowSets.STMT);
		if(look.getTokenName() == TokenEnum.LSQR){
			match(TokenEnum.LSQR);
			addChild(node, add_exp());  
			match(TokenEnum.RSQR);
		}
		match(TokenEnum.ASSIGN);
		AssignmentNode sym = new AssignmentNode();	//create assign node and make var node first child(insert this between gotten var node and it's parent)
		addChild(sym, node);
		addChild(sym, expression());	//pass assign node as parent
		match(TokenEnum.SEMI);
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Exiting Assign_Stmt_Tail");
		return sym;
	}
	
	private LinkedList<ASTNode> call_stmt_tail(){
		myTracer.addToParserTrace("Entering Call_Stmt_Tail");
		syncList.add(FollowSets.STMT);
		LinkedList<ASTNode> ret = call_tail();
		match(TokenEnum.SEMI);
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Exiting Call_Stmt_Tail");
		return ret;
	}
	
	private LinkedList<ASTNode> call_tail(){
		myTracer.addToParserTrace("Entering Call_Tail");
		syncList.add(FollowSets.CALLFACT);
		LinkedList<ASTNode> ret = null;
		match(TokenEnum.LPAREN);
		if(look.getTokenName() == TokenEnum.RPAREN){
			match(TokenEnum.RPAREN);
		}
		else{
			ret = arguments();
			match(TokenEnum.RPAREN);
		}
		syncList.remove(FollowSets.CALLFACT);
		myTracer.addToParserTrace("Exiting Call_Tail");
		return ret;
	}
	
	private LinkedList<ASTNode> arguments(){
		myTracer.addToParserTrace("Entering Arguments");
		syncList.add(FollowSets.ARGUMENTS);
		LinkedList<ASTNode> ret = new LinkedList<ASTNode>();
		ret.add(expression());	//pass fucntion node
		while(look.getTokenName() == TokenEnum.COMMA){
			match(TokenEnum.COMMA);
			ret.add(expression());//pass function node
		}
		syncList.remove(FollowSets.ARGUMENTS);
		myTracer.addToParserTrace("Exiting Arguments");
		return ret;
	}
	
	private ASTNode compound_stmt(){
		myTracer.addToParserTrace("Entering Compound_Stmt");
		syncList.add(FollowSets.COMPOUNDSTMT);
		match(TokenEnum.LCRLY);
		CompoundNode node = new CompoundNode();//create compound statement node
		while(look.getTokenName() == TokenEnum.INT || look.getTokenName() == TokenEnum.BOOL){
			TypeEnum temp = nonvoid_specifier();
			int t = match(TokenEnum.ID).getAttribute();
			VariableDeclarationNode var = new VariableDeclarationNode(temp,t,myAdmin.strID(t),false);
			addChildren(node, var_dec_tail(var));//pass compound node
		}
		addChild(node, statement());//pass compound node
		while(look.getTokenName() == TokenEnum.ID || look.getTokenName() == TokenEnum.LCRLY ||
				look.getTokenName() == TokenEnum.IF || look.getTokenName() == TokenEnum.LOOP ||
				look.getTokenName() == TokenEnum.EXIT || look.getTokenName() == TokenEnum.CONTINUE ||
				look.getTokenName() == TokenEnum.RETURN || look.getTokenName() == TokenEnum.SEMI ||
				look.getTokenName() == TokenEnum.BRANCH){
			addChild(node, statement());//pass compound node
		}				
		match(TokenEnum.RCRLY);
		syncList.remove(FollowSets.COMPOUNDSTMT);
		myTracer.addToParserTrace("Exiting Compound_Stmt");
		return node;
	}
	
	private IfNode if_stmt(){
		myTracer.addToParserTrace("Entering If_Stmt",true);
		syncList.add(FollowSets.STMT);
		match(TokenEnum.IF);
		IfNode node = new IfNode();//create IF node
		match(TokenEnum.LPAREN);
		addChild(node, expression());//pass if node
		match(TokenEnum.RPAREN);
		addChild(node, statement());//pass if node
		if(look.getTokenName() == TokenEnum.ELSE){
			match(TokenEnum.ELSE);
			addChild(node, statement());//pass if node
		}
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Exiting If_Stmt");
		return node;
	}
	
	private LoopNode loop_stmt(){
		myTracer.addToParserTrace("Entering Loop_Stmt",true);
		syncList.add(FollowSets.STMT);
		match(TokenEnum.LOOP);
		LoopNode node = new LoopNode();
		addChild(node, statement());
		while(look.getTokenName() == TokenEnum.ID || look.getTokenName() == TokenEnum.LCRLY ||
				look.getTokenName() == TokenEnum.IF || look.getTokenName() == TokenEnum.LOOP ||
				look.getTokenName() == TokenEnum.EXIT || look.getTokenName() == TokenEnum.CONTINUE ||
				look.getTokenName() == TokenEnum.RETURN || look.getTokenName() == TokenEnum.SEMI ||
				look.getTokenName() == TokenEnum.BRANCH){
			addChild(node, statement());
		}		
		match(TokenEnum.END);		//need end node????
		match(TokenEnum.SEMI);
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Exiting Loop_Stmt");
		return node;
	}
	
	private DatalessNode exit_stmt(){
		myTracer.addToParserTrace("Entering Exit_Stmt",true);
		syncList.add(FollowSets.STMT);
		match(TokenEnum.EXIT);
		DatalessNode node = new DatalessNode(DatalessEnum.EXIT);//make exit node
		match(TokenEnum.SEMI);
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Exiting Exit_Stmt");
		return node;
	}
	
	private DatalessNode continue_stmt(){
		myTracer.addToParserTrace("Entering Continue_Stmt",true);
		syncList.add(FollowSets.STMT);
		match(TokenEnum.CONTINUE);
		DatalessNode node = new DatalessNode(DatalessEnum.CONTINUE);
		match(TokenEnum.SEMI);
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Exiting Continue_Stmt");
		return node;
	}
	
	private ReturnNode return_stmt(){
				
		myTracer.addToParserTrace("Entering Return_Stmt",true);
		syncList.add(FollowSets.STMT);
		match(TokenEnum.RETURN);
		ReturnNode node;//make return node		
		if(look.getTokenName() == TokenEnum.MINUS || look.getTokenName() == TokenEnum.NOT ||
				look.getTokenName() == TokenEnum.LPAREN || look.getTokenName() == TokenEnum.NUM ||
				look.getTokenName() == TokenEnum.BLIT || look.getTokenName() == TokenEnum.ID){
			 node = new ReturnNode(true);
			addChild(node, expression());//pass return node
		}
		else{
			node  = new ReturnNode(false);
		}
		match(TokenEnum.SEMI);
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Exiting Return_Stmt");
				
		return node;
	}
	
	private DatalessNode null_stmt(){
		myTracer.addToParserTrace("Entering Null_Stmt",true);
		syncList.add(FollowSets.STMT);
		DatalessNode node = new DatalessNode(DatalessEnum.NULL);
		match(TokenEnum.SEMI);
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Entering Null_Stmt");
		return node;
	}
	
	private BranchNode branch_stmt(){
		myTracer.addToParserTrace("Entering Branch_Stmt",true);
		syncList.add(FollowSets.STMT);
		match(TokenEnum.BRANCH);
		BranchNode node = new BranchNode();//make branch node
		match(TokenEnum.LPAREN);
		addChild(node,add_exp());//pass branch node
		match(TokenEnum.RPAREN);
		addChild(node, case_s());//pass branch node
		while(look.getTokenName() == TokenEnum.CASE || look.getTokenName() == TokenEnum.DEFAULT){
			addChild(node, case_s());//pass branch node
		}
		match(TokenEnum.END);
		match(TokenEnum.SEMI);
		syncList.remove(FollowSets.STMT);
		myTracer.addToParserTrace("Exiting Branch_Stmt");
		return node;
	}
	
	private CaseNode case_s(){
		myTracer.addToParserTrace("Entering Case_S",true);
		syncList.add(FollowSets.STMT);
		if(look.getTokenName() == TokenEnum.CASE){	
			match(TokenEnum.CASE);
			CaseNode caseN = new CaseNode(match(TokenEnum.NUM).getAttribute());//make case node
			match(TokenEnum.COLON);
			addChild(caseN, statement());
			syncList.remove(FollowSets.STMT);
			myTracer.addToParserTrace("Exiting Case_S");
			return caseN;
		}
		else{
			match(TokenEnum.DEFAULT);
			CaseNode node = new CaseNode(-1);
			match(TokenEnum.COLON);//make case node with default as attribute???
			addChild(node, statement());
			syncList.remove(FollowSets.STMT);
			myTracer.addToParserTrace("Exiting Case_S");
			return node;
		}
		
	}
	
	private ASTNode expression(){
		myTracer.addToParserTrace("Entering Expression");
		syncList.add(FollowSets.EXPRESSION);
		ASTNode left = add_exp();	//pass gotten node for parent
		if(look.getTokenName() == TokenEnum.LTEQ || look.getTokenName() == TokenEnum.LT ||
				look.getTokenName() == TokenEnum.GT || look.getTokenName() == TokenEnum.GTEQ ||
				look.getTokenName() == TokenEnum.EQ || look.getTokenName() == TokenEnum.NEQ){
			BinaryOpNode node = new BinaryOpNode(relop());	//insert between gotten node and last child added to it
			addChild(node, left);
			addChild(node, add_exp());  //pass relop node
			syncList.remove(FollowSets.EXPRESSION);
			myTracer.addToParserTrace("Exiting Expression");
			return node;
		}
		else{
			syncList.remove(FollowSets.EXPRESSION);
			myTracer.addToParserTrace("Exiting Expression");
			return left;
		}
	}
	
	private ASTNode add_exp(){
		myTracer.addToParserTrace("Entering Add_Exp");
		syncList.add(FollowSets.ADDEXP);
		ASTNode left;
		ExpressionNode node;
		if(look.getTokenName() == TokenEnum.MINUS){
			left = new UnaryOpNode(uminus());	//make child of gotten node
			addChild(left, term());
		}
		else{
			left = term();		
		}
		while(look.getTokenName() == TokenEnum.PLUS || look.getTokenName() == TokenEnum.MINUS ||
				look.getTokenName() == TokenEnum.OR || look.getTokenName() == TokenEnum.ORELSE){
			node = new BinaryOpNode(addop()); //insert between gotten node and last child added to it
			addChild(node, left);
			addChild(node, term());
			left = node;
		}
		syncList.remove(FollowSets.ADDEXP);
		myTracer.addToParserTrace("Exiting Add_Exp");
		return left;
	}
	
	private ASTNode term(){
		myTracer.addToParserTrace("Entering Term");
		syncList.add(FollowSets.TERM);
		ExpressionNode node;
		ASTNode left = factor();
		while(look.getTokenName() == TokenEnum.MULT || look.getTokenName() == TokenEnum.DIV ||
				look.getTokenName() == TokenEnum.MOD || look.getTokenName() == TokenEnum.AND ||
				look.getTokenName() == TokenEnum.ANDTHEN){
			node = new BinaryOpNode(multop());//insert between gotten node and last child
			addChild(node, left);
			addChild(node, factor());//pass gotten node
			left = node;
		}
		syncList.remove(FollowSets.TERM);
		myTracer.addToParserTrace("Exiting Term");
		return left;
	}
	
	private ASTNode factor(){
		myTracer.addToParserTrace("Entering Factor");
		syncList.add(FollowSets.CALLFACT);
		ASTNode temp;
		if(look.getTokenName() == TokenEnum.ID){
			temp = id_factor();//pass gotten
		}
		else{
			temp = nid_factor();//pass gotten
		}
		syncList.remove(FollowSets.CALLFACT);
		myTracer.addToParserTrace("Exiting Factor");
		return temp;
	}
	
	private ASTNode nid_factor(){
		myTracer.addToParserTrace("Entering Nid_Factor");
		syncList.add(FollowSets.CALLFACT);
		ExpressionNode temp;
		if(look.getTokenName() == TokenEnum.NOT){
			match(TokenEnum.NOT);
			UnaryOpNode node = new UnaryOpNode(OperatorEnum.NOT);
			addChild(node, factor());
			syncList.remove(FollowSets.CALLFACT);
			myTracer.addToParserTrace("Exiting Nid_Factor");
			return node;
		}
		else if(look.getTokenName() == TokenEnum.LPAREN){
			match(TokenEnum.LPAREN);
			ASTNode ret = expression();//pass gotten node
			match(TokenEnum.RPAREN);
			syncList.remove(FollowSets.CALLFACT);
			myTracer.addToParserTrace("Exiting Nid_Factor");
			return ret;
		}
		else if(look.getTokenName() == TokenEnum.NUM){
			temp = new NumNode(match(TokenEnum.NUM).getAttribute());//make num node
		}
		else{
			boolean t;
			if(match(TokenEnum.BLIT).getAttribute()==1){
				t = true;
			}
			else{
				t = false;
			}
			temp = new BlitNode(t);//make BLIt node
		}
		syncList.remove(FollowSets.CALLFACT);
		myTracer.addToParserTrace("Exiting Nid_Factor");
		return temp;
	}
	
	private CallNode id_factor(){
		myTracer.addToParserTrace("Entering Id_Factor");
		syncList.add(FollowSets.CALLFACT);
		int t = match(TokenEnum.ID).getAttribute();
		VariableCallNode node = new VariableCallNode(t,myAdmin.strID(t));//make temporary node
		CallNode temp = id_tail(node);
		syncList.remove(FollowSets.CALLFACT);
		myTracer.addToParserTrace("Exiting Id_Factor");
		return temp;
	}
	
	private CallNode id_tail(VariableCallNode node){//get temp node
		myTracer.addToParserTrace("Entering Id_Tail");
		syncList.add(FollowSets.CALLFACT);
		if(look.getTokenName() == TokenEnum.LPAREN){
			FunctionCallNode func = new FunctionCallNode(node.getId(), node.getLexem());	//need to build func node with same id (guess was wrong)
			addChildren(func, call_tail());
			syncList.remove(FollowSets.CALLFACT);
			myTracer.addToParserTrace("Exiting Id_Tail");
			return func;
		}
		else{
			ASTNode temp = var_tail();
			if(temp!=null){
				addChild(node, temp);
			}
			syncList.remove(FollowSets.CALLFACT);
			myTracer.addToParserTrace("Exiting Id_Tail");
			return node;
		}
	}
	
	private ASTNode var_tail(){
		myTracer.addToParserTrace("Entering Var_Tail");
		syncList.add(FollowSets.CALLFACT);
		if(look.getTokenName() == TokenEnum.LSQR){
			match(TokenEnum.LSQR);
			ASTNode ret = add_exp();//pass gotten
			match(TokenEnum.RSQR);
			syncList.remove(FollowSets.CALLFACT);
			myTracer.addToParserTrace("Exiting Var_Tail");
			return ret;
		}
		syncList.remove(FollowSets.CALLFACT);
		myTracer.addToParserTrace("Exiting Var_Tail");
		return null;
	}
	
	private OperatorEnum relop(){	
		myTracer.addToParserTrace("Entering Relop");
		OperatorEnum temp;
		if(look.getTokenName() == TokenEnum.LTEQ){
			match(TokenEnum.LTEQ);
			temp =  OperatorEnum.LTEQ;
		}
		else if(look.getTokenName() == TokenEnum.LT){
			match(TokenEnum.LT);
			temp = OperatorEnum.LT;
		}
		else if(look.getTokenName() == TokenEnum.GT){
			match(TokenEnum.GT);
			temp = OperatorEnum.GT;
		}
		else if(look.getTokenName() == TokenEnum.GTEQ){
			match(TokenEnum.GTEQ);
			temp = OperatorEnum.GTEQ;
		}
		else if(look.getTokenName() == TokenEnum.EQ){
			 match(TokenEnum.EQ);
			 temp = OperatorEnum.EQ;
		}
		else{
			match(TokenEnum.NEQ);
			temp = OperatorEnum.NEQ;
		}
		myTracer.addToParserTrace("Exiting Relop");
		return temp;
	}
	
	private OperatorEnum addop(){
		myTracer.addToParserTrace("Entering Addop");
		OperatorEnum temp;
		if(look.getTokenName() == TokenEnum.PLUS){
			match(TokenEnum.PLUS);
			temp = OperatorEnum.PLUS;
		}
		else if(look.getTokenName() == TokenEnum.MINUS){
			match(TokenEnum.MINUS);
			temp = OperatorEnum.MINUS;
		}
		else if(look.getTokenName() == TokenEnum.OR){
			match(TokenEnum.OR);
			temp = OperatorEnum.OR;
		}
		else{
			match(TokenEnum.ORELSE);
			temp = OperatorEnum.ORELSE;
		}
		myTracer.addToParserTrace("Exiting Addop");
		return temp;
	}
	
	private OperatorEnum multop(){	
		myTracer.addToParserTrace("Entering Multop");
		OperatorEnum temp;
		if(look.getTokenName() == TokenEnum.MULT){
			match(TokenEnum.MULT);
			temp = OperatorEnum.MULT;
		}
		else if(look.getTokenName() == TokenEnum.DIV){
			match(TokenEnum.DIV);
			temp = OperatorEnum.DIV;
		}
		else if(look.getTokenName() == TokenEnum.MOD){
			match(TokenEnum.MOD);
			temp = OperatorEnum.MOD;
		}
		else if(look.getTokenName() == TokenEnum.AND){
			match(TokenEnum.AND);
			temp = OperatorEnum.AND;
		}
		else{
			match(TokenEnum.ANDTHEN);
			temp = OperatorEnum.ANDTHEN;
		}
		myTracer.addToParserTrace("Exiting Multop");
		return temp;
	}
	
	private OperatorEnum uminus(){	
		myTracer.addToParserTrace("Entering Uminus");
		match(TokenEnum.MINUS);
		myTracer.addToParserTrace("Exiting Uminus");
		return OperatorEnum.UMINUS;
	}
}
