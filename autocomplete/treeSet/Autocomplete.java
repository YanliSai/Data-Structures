package treeSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

//refer to http://sujitpal.blogspot.com/2007/02/three-autocomplete-implementations.html
// in-memory set: without considering weight of each word
public class Autocomplete {
	private TreeSet<String> words;
	
	// Initializes an autocomplete data structure from the given terms array
	public Autocomplete(String[] words) {
		this.words = new TreeSet<String>();
		Collections.addAll(this.words, words);
	}

	// Returns the top k matching terms as a list
	public Iterable<String> topMatches(String prefix) {
		List<String> res = new ArrayList<String>();
		Set<String> tailSet = words.tailSet(prefix);
		for (String tail : tailSet) {
			if (tail.startsWith(prefix)) {
				res.add(tail);
			} else {
				break;
			}
		}
		return res;
	}
	
	public static void main(String[] args) {
		String[] terms = {"china", "calafornia", "japan"};
		Autocomplete auto = new Autocomplete(terms);
		System.out.println(auto.topMatches("c"));
	}
}
