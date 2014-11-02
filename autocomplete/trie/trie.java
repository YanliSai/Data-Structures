// Refer to http://algs4.cs.princeton.edu/52trie/TrieST.java.html and
// http://www.cs.princeton.edu/courses/archive/fall13/cos226/checklist/autocomplete.html

//class Word implements Comparable<Word> {
//	private String word;
//	private double weight;
//	public Word() {}
//	public Word(String word, double weight) {
//		this.word = word;
//		this.weight = weight;
//	}
//
//	public int compareTo(Word other) {
//		return (int) Math.ceil(other.weight - weight);
//	}
//
//	public String toString() {
//		return word;
//	}	
//}

public class Trie {
	private Node root; // root of a tire
	private static class Node {
		private double value; // can store the weight
		private Node[] next = new Node[26];
	}
	
	// Returns first k keys in the set that start with "prefix"
	public Collection<String> topMatches(String prefix) {
		Queue<String> queue = new LinkedList<>(); // Return the bigger weight
		Node node = get(root, prefix, 0);
		collect(node, prefix, queue);
		return queue;
	}
	
	private Node get(Node node, String key, int index) {
		if (node == null) {
			return null;
		}
		if (index == key.length()) {
			return node;
		}
		return get(node.next[key.charAt(index)], key, index + 1);
	}
	
	// Backtracking: collect all words start with "prefix" from node "start"
	private void collect(Node start, String prefix, Collection<String> res) {
		if (start == null) {
			return;
		}
		if (start.value >= 0) { // Assume non-word with a negative value
			res.add(prefix);
		}
		for (int i = 0; i < 26; i++) {
			collect(start.next[i], prefix + ('a' + i), res);
		}
	}
	
	// Insert key-value pair into the trie.
	public void put(String key, double value) {
		root = put(root, key, value, 0);
	}
	
	private Node put(Node node, String key, double val, int index) {
		if (node == null) {
			node = new Node();
		}
		if (index == key.length()) {
			node.value = val;
			return node;
		}
		char c = key.charAt(index);
		node.next[c] = put(node.next[c], key, val, index + 1);
		return node;
	}
}
