package fileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
/**
 * Utility class providing methods to get a reader for a specified file, print to a file, and compare the strings
 * within two files.
 * @author Bryan Storie
 *
 */
public class FileUtil {

	/**
	 * Prints output into file given by fileName.
	 * @param output string to print
	 * @param fileName file to print output to
	 */
	public static void printToFile(String output, String fileName){
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName,"US-ASCII");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		writer.print(output);
		writer.close();
	}
	
	/**
	 * Utility method that compares the strings within two files line by line.
	 * @param file1
	 * @param file2
	 * @return true if strings within file1 and file2 are identical
	 */
	public static boolean compareFile(String file1, String file2){
		boolean result = true;
		
		File f1 = new File(file1);
		File f2 = new File(file2);
		
		Scanner s1 = null;
		Scanner s2 = null;
		
		try {
			s1 = new Scanner(f1);
			s2 = new Scanner(f2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(s1.hasNextLine()&&s2.hasNextLine()&&result == true){
			if(!s1.nextLine().equals(s2.nextLine())){
				result = false;
			}
		}			
		
		if(s1.hasNextLine()!=s2.hasNextLine()){
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Creates and returns an InputStreamReader for the given fileName.
	 * @param fileName file to create InputStreamReader for
	 * @return created InputStreamReader
	 */
	public static InputStreamReader getReader(String fileName){
		File myFile = new File(fileName);
		FileInputStream fIS = null;
		try{
			fIS = new FileInputStream(myFile);
		}
		catch(FileNotFoundException e){
			
		}
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(fIS, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return reader;
	}
	
	public static StringReader getStringReader(String in){
		return new StringReader(in);
	}
}
