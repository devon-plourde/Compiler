package main;

import java.util.ArrayList;

import admin.Admin;
import admin.AdminParam;
import admin.CompilerPhase;

/**
 * Main method for Compiler.  Handles all command line flags and creates AdminParam based on inputs then
 * passes parameters to Admin to be compiled.
 * @author Devon Plourde
 *
 */
public class Main {
	
	
	public static void main(String args[])
	{	
		AdminParam param = new AdminParam();
		
		//Extensions of files
		String inputExtension = ".txt";
		String defaultOutputExtension = ".cs16";
		String errorExtension = ".err";
		
		//Storage for names of files to be used by the compiler
		ArrayList<String> inputFiles = new ArrayList<String>();
		ArrayList<String> outputFiles = new ArrayList<String>();
		ArrayList<String> errorFiles = new ArrayList<String>();
		
		Admin myAdmin = new Admin();
		
		
		//This loop detects the various command line options for the compiler.
		for(int i = 0; i < args.length; ++i)
		{
			switch(args[i])
			{
			case "-h":
			case "-help":
				System.out.println("-h | -help     -- Displays this help menu");
				System.out.println("-l | -lex      -- Process up to the Lexer phase");
				System.out.println("-p | -parse    -- Process up to the Parser phase");
				System.out.println("-s | -sem      -- Process up to the Semantic Analysis phase");
				System.out.println("-t | -tup      -- Process up to the Tuple phase");
				System.out.println("-c | -compile  -- Process all phases and compile (default behavior)");
				System.out.println("-q | -quiet    -- Only display error messages (default behavior)");
				System.out.println("-v | -verbose  -- Display all trace messages");
				System.out.println("-o | -out      -- Output file");
				System.out.println("-e | -err      -- Error file");	
				break;
				
			case "-l":
			case "-lex":
				param.setPhase(CompilerPhase.LEXICAL);
				break;
				
			case "-p":
			case "-parse":
				param.setPhase(CompilerPhase.PARSER);
				break;
			case "-s":
			case "-sem":
				param.setPhase(CompilerPhase.SEMANTIC);
				break;
			case "-t":
			case "-tup":
				param.setPhase(CompilerPhase.TUPLE);
				break;
			
			case "-c":
			case "-compile":
				param.setPhase(CompilerPhase.COMPILE);
				break;				
			case "-q":
			case "-quiet":
				param.setVerbose(false);
				break;
				
			case "-v":
			case "-verbose":
				param.setVerbose(true);
				break;
				
			case "-o":
			case "-output":
				if(i+1 == args.length){
					System.out.println("Insuficient arguments");
					System.exit(0);
				}
				else if(args[i+1].matches("-(.*)")){
					System.out.println("Invalid argument: "+args[i+1]);
					System.exit(0);
				}
				else{
					++i;
					outputFiles.add(args[i]);
					param.setOutputPrint(true);
				}
				break;
				
			case "-e":
			case "-err":
				if(i+1 == args.length){
					System.out.println("Insuficient arguments");
					System.exit(0);
				}
				else if(args[i+1].matches("-(.*)")){
					System.out.println("Invalid argument: "+args[i+1]);
					System.exit(0);
				}
				else{
					++i;
					errorFiles.add(args[i]);
					param.setErrorPrint(true);
				}
				break;
			default:
				if(args[i].endsWith(inputExtension))
					inputFiles.add(args[i]);
			
				else{
					System.out.println("Invalid argument: "+ args[i]);
					System.exit(0);
				}
			}
		}
		
		if(inputFiles.size() == 0){
			System.out.println("No file selected for compiling.");
			System.exit(0);
		}
		
		
//This loop creates output file names that correspond to the input files that don't
//have explicit output files declared for them
		if(inputFiles.size() > outputFiles.size())
		{
			for(int i = inputFiles.size() - (inputFiles.size() - outputFiles.size()); i < inputFiles.size(); ++i)
			{				
				String temp = inputFiles.get(i);
				temp = temp.replaceFirst(inputExtension, defaultOutputExtension);
				
				outputFiles.add(temp);			
			}
		}
		
		if(inputFiles.size() > errorFiles.size())
		{
			for(int i = inputFiles.size() - (inputFiles.size() - errorFiles.size()); i < inputFiles.size(); ++i)
			{				
				String temp = inputFiles.get(i);
				temp = temp.replaceFirst(inputExtension, errorExtension);
				
				errorFiles.add(temp);			
			}
		}
		
		for(int i = 0; i < inputFiles.size();i++){
			param.setInputFile(inputFiles.get(i));
			param.setOutputFile(outputFiles.get(i));
			param.setErrorFile(errorFiles.get(i));
			myAdmin.compile(param);
		}
	}
}