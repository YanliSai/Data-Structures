public class Autocomplete {
	// Initializes an autocomplete data structure from the given parallel arrays of terms and weights.
	public Autocomplete(String[] terms, double[] weights) {
		
	}
	
	// Returns the weight of the term, or 0.0 if no such term.
	public double weightOf(String term) {
		
		return 0.0;
	}
	
	// Returns a top matching term, or null if no matching term.
	public String topMatch(String prefix) {
	
		return "";
	}
	
	// Returns the top k matching terms (in descending order of weight), as an iterable.
    // If fewer than k matches, return all matching terms (in descending order of weight).
	public Iterable<String> topMatches(String prefix, int k) {
		
		return null;
	}
}
