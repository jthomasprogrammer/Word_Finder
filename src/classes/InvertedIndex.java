package classes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class InvertedIndex {
	private final HashMap<String, Set<TextLocation>> invertedIndex;
	
	public InvertedIndex(){
		this.invertedIndex = new HashMap<String, Set<TextLocation>>();
	}
	
	/*
	 * Adds a word and the location of that word in a file to the index.
	 */
	public void add(String word, TextLocation textLocation){
		//Make sure to remove the word of periods, question marks, etc.
		word = word.replaceAll("\\W", "");
		//If the index doesn't have the word, add it. Otherwise update the set attached to the word.
		if(invertedIndex.containsKey(word)){
			Set<TextLocation> set = invertedIndex.get(word);
			set.add(textLocation);
			invertedIndex.put(word, set);
		}else{
			//Since its a set, there should never be any duplicate text locations.
			Set<TextLocation> set = new HashSet<TextLocation>();
			set.add(textLocation);
			invertedIndex.put(word, set);
		}
	}
	
	/*
	 * Retrieves a set of text locations that contain the given word.
	 * If the word is not in the inverted index, returns null.
	 * Note: These text locations are not in sorted order.
	 */
	public Set<TextLocation> get(String word){
		return invertedIndex.get(word);
	}
	
	/*
	 * Prints out every word followed by a group of its text locations. Example: "is":    Line 1: This is not a test., Line 3: Just kidding it is not. 
	 */
	@Override public String toString(){
		String printedString = "";
		Set<String> set = invertedIndex.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String word = it.next();
			Set<TextLocation> textLocations = invertedIndex.get(word);
			Iterator<TextLocation> it2 = textLocations.iterator();
			String locationText = "";
			while(it2.hasNext()){
				TextLocation textLocation = it2.next();
				if(it2.hasNext()){
					locationText += textLocation.toString()+ ", ";
				}else{
					locationText += textLocation.toString();
				}
			}
			
			printedString += String.format("\"%s\":    %s\n", word, locationText);
		}
		return printedString;
	}
	
	/*
	 * Clears the inverted index of all entries. The index will be empty after this call.
	 */
	public void clear(){
		invertedIndex.clear();
	}
}
