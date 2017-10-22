package admin;

/**
 * Parameter object used by Admin to contain all information needed to compile a file.
 * @author Bryan Storie
 *
 */
public class AdminParam {
	
	private CompilerPhase phase;
	private boolean verbose;
	private String inputFile;
	private String outputFile;
	private String errorFile;
	private boolean errorPrint;
	private boolean outputPrint = false;
	
	public void setErrorPrint(boolean t){
		this.errorPrint = t;
	}
	public void setOutputPrint(boolean t){
		this.outputPrint = t;
	}
	
	public void setPhase(CompilerPhase p){
		this.phase = p;
	}
	
	public void setVerbose(boolean b){
		this.verbose = b;
	}
	
	public void setInputFile(String s){
		this.inputFile = s;
	}
	
	public void setOutputFile(String s){
		this.outputFile = s;
	}
	
	public void setErrorFile(String s){
		this.errorFile = s;
	}
	public boolean getErrorPrint(){
		return this.errorPrint;
	}
	public boolean getOutputPrint(){
		return this.outputPrint;
	}
	
	public CompilerPhase getPhase(){
		return this.phase;
	}
	
	public boolean getVerbose(){
		return verbose;
	}
	
	public String getInputFile(){
		return inputFile;
	}
	
	public String getOutputFile(){
		return outputFile;
	}
	
	public String getErrorFile(){
		return errorFile;
	}

}
