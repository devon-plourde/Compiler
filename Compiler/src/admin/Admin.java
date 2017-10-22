package admin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import compCodeGenerator.CompCodeGenerator;
import compError.CompError;
import compParser.CompParser;
import compParser.ast.ASTNode;
import compParser.ast.ASTUtil;
import compScanner.CompScanner;
import compScanner.token.Token;
import compScanner.token.TokenEnum;
import compSemanticAnalyzer.CompSemanticAnalyzer;
import fileUtil.FileUtil;

/**
 * An instance of this class acts as the main controller for the compilation of a c*16 file.  An Admin will instantiate
 * all parts of the compiler and then take a file and compile it into the triples language specified in the Cpsc 425
 * Compiler documentation.
 * @author Bryan Storie
 * @author Devon Plourde
 *
 */
public class Admin {
	
	private final int maxErrors = 10;
	private InputStreamReader myReader = null;
	private StringReader builtInReader = null;
	private Tracer myTracer;
	private CompScanner cScanner;
	private CompParser myParser;
	private CompSemanticAnalyzer mySA;
	private CompCodeGenerator myGen;
	private LinkedList<CompError> errorList = new LinkedList<CompError>();
	private ASTNode root;
	private boolean builtInAdded = false;
	
	/**
	 * Creates an Admin and instantiates all needed parts of the compiler
	 */
	public Admin(){
		this.myTracer = new Tracer();
		this.cScanner = new CompScanner(this, this.myTracer);
		this.myParser = new CompParser(this, this.myTracer);
		this.mySA = new CompSemanticAnalyzer(this, this.myTracer);
		this.myGen = new CompCodeGenerator(this);
		resetLocalVariables();
	}
	
	/**
	 * Temporary method used to run tests. Sets the admin's file to be compiled to the file at the specified file
	 * path
	 * @param fileName the path to the file to be compiled
	 */
	public void setFile(String fileName){
		cScanner.resetLocalVariables();
		myReader = FileUtil.getReader(fileName);
	}
	
	/**
	 * Method used upon every compile or setFile call to reset the local variables to default in order to properly
	 * compile a new file
	 */
	private void resetLocalVariables(){
		cScanner.resetLocalVariables();	
		myParser.resetLocalVariables();
		myTracer.resetLocalVariables();
		mySA.resetLocalVariables();
		myGen.resetLocalVariables();
		errorList = new LinkedList<CompError>();
		myReader = null;
		builtInReader = FileUtil.getStringReader(BuiltInFunctions.FUNCTIONS);
		root = null;
		builtInAdded = false;
	}
	
	/**
	 * Compiles a c*16 file given in param object to the stage specified in param and creates output files and error
	 * files at locations specified in param.
	 * <p>
	 * This method passes the file information through each of the parts of the compiler and builds file strings (to
	 * be printed to file) and verbose strings(for -v print outs) for each stage.  The calls are all within a for loop
	 * with one iteration in order to allow breaking out of the loop whenever the stage in param has been completed.
	 * Methods in the FileUtil class are used to read and write the files.
	 * @param param AdminParameter object containing compile information
	 */
	public void compile(AdminParam param){
		this.resetLocalVariables();
		String fileString = "";
		myReader = FileUtil.getReader(param.getInputFile());
		
		//First line of print out is file results are printed to
		System.out.println(param.getOutputFile());
		if(param.getErrorPrint()){
			System.out.println(param.getErrorFile()+"\n");
		}
		else{
			System.out.println("Error file excluded");
		}

		
		//1 iteration loop that allows breaking out of when phase of termination is reached
		for(int i = 0; i < 1; i++){
				
			LinkedList<Token> tokenList = new LinkedList<Token>();
			
			do{
				tokenList.add(cScanner.getNextToken());
			}while(tokenList.peekLast()  .getTokenName() != TokenEnum.ENDFILE);
			
			if(param.getPhase()==CompilerPhase.LEXICAL || !errorList.isEmpty()){
				if(param.getOutputPrint()){
					if(param.getErrorPrint()){
						FileUtil.printToFile(fileString+"\n\n"+errorsToString(), param.getOutputFile());
						printErrors(param.getErrorPrint(), param.getErrorFile());
					}
					else{
						FileUtil.printToFile(fileString+"\n\n"+errorsToString(), param.getOutputFile());
					}
				}
				else{
					if(param.getErrorPrint()){
						printErrors(param.getErrorPrint(), param.getErrorFile());
					}
				}
				
				
				if(param.getVerbose()){
					System.out.println(myTracer.getScannerTrace());
				}
				else{
					System.out.println("[TRACE DISABLED]");
				}
				break;
			}
			try{
				root = myParser.parse(tokenList);	
			}
			catch(NoSuchElementException e){				
			}
			if(param.getPhase()==CompilerPhase.PARSER||root==null){	
				if(param.getOutputPrint()){
					if(param.getErrorPrint()){
						FileUtil.printToFile(ASTUtil.createPrintableASTString(root)+"\n\n"+errorsToString(), param.getOutputFile());
						printErrors(param.getErrorPrint(), param.getErrorFile());
					}
					else{
						FileUtil.printToFile(ASTUtil.createPrintableASTString(root)+"\n\n"+errorsToString(), param.getOutputFile());
					}
				}
				else{
					if(param.getErrorPrint()){
						printErrors(param.getErrorPrint(), param.getErrorFile());
					}
				}
				if(param.getVerbose()){
					if(root==null){
						System.out.println("Syntax Errors Encountered, Parse is innaccuarate");
					}
					System.out.println(myTracer.getParserTrace());
				}
				else{
					System.out.println("[TRACE DISABLED]");
				}
				break;
			}
			
			mySA.analyze(root);
			
			if(param.getPhase()==CompilerPhase.SEMANTIC || !errorList.isEmpty()){
				if(param.getOutputPrint()){
					if(param.getErrorPrint()){
						FileUtil.printToFile(ASTUtil.createPrintableASTString(root)+"\n\n"+errorsToString(), param.getOutputFile());
						printErrors(param.getErrorPrint(), param.getErrorFile());
					}
					else{
						FileUtil.printToFile(ASTUtil.createPrintableASTString(root)+"\n\n"+errorsToString(), param.getOutputFile());
					}
				}
				else{
					if(param.getErrorPrint()){
						printErrors(param.getErrorPrint(), param.getErrorFile());
					}
				}
				if(param.getVerbose()){
					System.out.println("[TRACE ENABLED]\n"+ASTUtil.createPrintableASTString(root));
				}
				else{
					System.out.println("[TRACE DISABLED]");
				}
				break;
			}
			
			String code = myGen.generateCode(root);

			System.out.println(code);
			
			if(param.getOutputPrint()){
				if(param.getErrorPrint()){
					FileUtil.printToFile(code+"\n\n"+errorsToString(), param.getOutputFile());
					printErrors(param.getErrorPrint(), param.getErrorFile());
				}
				else{
					FileUtil.printToFile(code+"\n\n"+errorsToString(), param.getOutputFile());
				}
			}
			else{
				if(param.getErrorPrint()){
					printErrors(param.getErrorPrint(), param.getErrorFile());
				}
			}
		}
		
		if(!errorList.isEmpty()){
			System.out.println(errorsToString());
		}		
		
		if(errorList.isEmpty()){
			System.out.println("PASS");
		}
		else{
			System.out.println("FAIL");
		}
				
		try {
			myReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printErrors(boolean print, String filePath){
		String errorString = errorsToString();
		if(print){
			FileUtil.printToFile(errorString, filePath);
		}
//		System.out.println(errorString);
	}
	
	private String errorsToString(){
		String errorString = "";
		if(!errorList.isEmpty()){
			errorString += "\nErrors:\n";
			for(int j = 0; j < errorList.size(); j++){
				errorString+= "\n============================================================";
				errorString+="\nError "+(j+1)+": "+errorList.get(j).toString();
			}
		}
		else{
			errorString += "\nErrors: No errors encountered.\n";
		}
		return errorString;
	}
	
	/**
	 * Method used by CompScanner in order to pull characters from the file given to Admin to compile.  Will return
	 * any character(as an int) and records the characters in an ArrayList for use in verbose printing as well as error
	 * reporting.  Also records the current line number for error reporting.
	 * @return integer corresponding to next character in file
	 */
	public int getCh(){
		int i = 0;
		if(builtInAdded){
			try {
				i = myReader.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			try{
				i = builtInReader.read();
				if(i==-1){
					i = myReader.read();
					builtInAdded = true;
				}
			} catch (IOException e){
				e.printStackTrace();
			}			
		}
		return i;
	}
	
	/**
	 * Used by components of the compiler to report an error.  An error is added to the error list along with the
	 * line number and character number. 
	 * @param e the type of error being reported
	 */
	public void errorM(CompError e) 
	{
		if(errorList.size()<maxErrors){
			errorList.add(e);
		}
	}
	
	public String strID(int x)
	{
		return cScanner.strID(x);
	}
}