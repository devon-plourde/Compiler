package compParser;

import java.util.Hashtable;
import compScanner.token.TokenEnum;

/**
 * Uses a Hashtable to store all of the possible synch symbols at any point in the Parser's
 * execution and the number of times that the symbol has been added in order to ensure that
 * a symbol remains as long as a method that added it is still being executed.  The symbols act
 * as the key and the insert count as the value.
 * @author Bryan Storie
 *
 */
public class SyncList {
	
	private Hashtable<TokenEnum,Integer> list;
	
	public SyncList(){
		list =new Hashtable<TokenEnum,Integer>();
	}
	
	public void resetLocalVariables(){
		list.clear();
	}
	
	public boolean contains(TokenEnum key){
		return list.containsKey(key);
	}
	
	/**
	 * Adds the given TokensTypes to the HashTable and increments the number of times it has been added.
	 * @param keys The TokenTypes to add
	 */
	public void add(TokenEnum[] keys){
		for(TokenEnum key: keys){
			if(list.containsKey(key)){
				list.put(key, list.get(key)+1);
			}
			else{
				list.put(key, 1);
			}
		}
	}
	
	/**
	 * Removes the given set of TokenTypes from the HashTable.  If the insert count is greater than 1 then the 
	 * count is decremented, otherwise the symbol is completely removed.
	 * @param keys TokenTypes to remove
	 */
	public void remove(TokenEnum[] keys){
		for(TokenEnum key: keys){
			int i = list.remove(key);
			if(--i>0){
				list.put(key, i);
			}
		}
	}

}
