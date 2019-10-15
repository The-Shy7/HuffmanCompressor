// Makes use of the Huffman algorithm to compress files (mainly .txt files) 
// into binary representations. Also, the client can decompress 
// the file, so the binary file will be converted back into the 
// original text file. To create the Huffman code, the client 
// can just pass in a .txt file which will be converted 
// into a .code file. To compress a file, the client can 
// just pass in the .code file and it will be converted 
// into a .short file. To decompress a file, the client 
// can pass the .short file and it will recreate the original 
// text into a .new file.

package huffman;

import java.util.*;
import java.io.*;

public class HuffmanCode {
	
	// the overall Huffman tree
	private HuffmanNode overallRoot;

	// post: constructs a Huffman tree from the given  
	//		 frequencies of the ASCII values 
	public HuffmanCode(int[] frequencies) {
		Queue<HuffmanNode> huffQueue = new PriorityQueue<HuffmanNode>();
		
		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] != 0) {
				HuffmanNode character = new HuffmanNode(i, frequencies[i]);
				huffQueue.add(character);
			}
		}
		
		// construct a tree from the nodes stored in the queue
		overallRoot = constructTree(huffQueue);
	}
	
	// pre: there must be more than one node in the queue
	// post: returns a Huffman tree constructed 
	//		 from a queue of Huffman nodes and sorts 
	//		 the nodes by their frequencies
	private HuffmanNode constructTree(Queue<HuffmanNode> q) {
		HuffmanNode temp = q.peek();
		
		while (q.size() > 1) {
			HuffmanNode left = q.remove();
			HuffmanNode right = q.remove();
			int combinedFreq = left.freq + right.freq;
			// -1 is outside the ASCII values (0-255)
			temp = new HuffmanNode(-1, combinedFreq, left, right);
			q.add(temp);
		}
		
		return temp;
	}
	
	// pre: assumes the input is not empty and is in the standard format
	// post: constructs a Huffman tree from an
	//		 already constructed Huffman tree (an already compressed file)
	//		 and uses the symbols and binary sequences contained in the tree
	public HuffmanCode(Scanner input) {
		while (input.hasNextLine()) {
			String symbol = input.nextLine();
			String code = input.nextLine();
			// construct a tree from character and binary sequence
			overallRoot = constructTree(overallRoot, symbol, code);
		}
	}
	
	// post: returns and constructs a Huffman node with the given Huffman node,
	//		 character, and binary sequence
	private HuffmanNode constructTree(HuffmanNode node, String symbol, String code) {
		if (code.length() == 0) {
			int asciiVal = Integer.parseInt(symbol);
			HuffmanNode newNode = new HuffmanNode(asciiVal, 0);
			return newNode;
		}
		
		if (node == null) {
			// -1 is outside the ASCII value range
			node = new HuffmanNode(-1, 0);
		}
		
		if (code.charAt(0) == '0') {
			node.leftNode = constructTree(node.leftNode, symbol, code.substring(1));
		} else {
			node.rightNode = constructTree(node.rightNode, symbol, code.substring(1));
		}
		
		return node;
	}
	
	// post: outputs a file containing the ASCII value
	//		 of the character(s) and the following 
	//		 binary sequence representing the character(s)
	public void save(PrintStream output) {
		String code = "";
		save(overallRoot, output, code);
	}
	
	// pre: the Huffman tree must not be empty
	// post: creates an output file by writing the ASCII value
	//		 of the character on one line and its corresponding
	//		 binary sequence on the next line
	private void save(HuffmanNode node, PrintStream output, String code) {
		if (node != null) {
			if (isLeafNode(node)) {
				output.println(node.symbol);
				output.println(code);
			}
			
			save(node.leftNode, output, code + "0");
			save(node.rightNode, output, code + "1");
		}
	}
	
	// pre: assumes the input contains a legal 
	//		format of the characters 
	// post: reads individual bits from the input and converts
	//		 it into the corresponding characters, so it takes 
	//		 an already compressed file and outputs the original
	//		 decompressed file
	public void translate(BitInputStream input, PrintStream output) {
		HuffmanNode node = overallRoot;
		
		while (input.hasNextBit() || isLeafNode(node)) {
			if (isLeafNode(node)) {
				output.write(node.symbol);
				node = overallRoot;
			} else {
				int bit = input.nextBit();
				if (bit == 0) {
					node = node.leftNode;
				} else {
					node = node.rightNode;
				}
			}
		}
	}
	
	// post: returns true if the Huffman node is a leaf node
	//		 with no child nodes, otherwise returns false
	private boolean isLeafNode(HuffmanNode node) {
		return node.leftNode == null && node.rightNode == null;
	}
	
	// constructs Huffman nodes
	private static class HuffmanNode implements Comparable<HuffmanNode> {
		
		// the number of times the character/symbol appears
		public int freq;
		
		// the ASCII integer representation of the symbol
		// the range is 0 to 255
		public int symbol;
		
		// the left node which represents the binary 0 
		public HuffmanNode leftNode;
		
		// the right node which represents the binary 1
		public HuffmanNode rightNode;
		
		// post: constructs a new Huffman node with just the 
		// 		 ASCII value (0-255) and frequency of the character/symbol
		public HuffmanNode(int symbol, int freq) {
			this(symbol, freq, null, null);
		}
		
		// post: constructs a new Huffman node with the given
		//		 ASCII value (0-255) and frequency of the 
		//		 character/symbol along with the left 
		//       and right nodes
		public HuffmanNode(int symbol, int freq, HuffmanNode left, HuffmanNode right) {
			this.symbol = symbol;
			this.freq = freq;
			this.leftNode = left;
			this.rightNode = right;
		}
		
		// post: compares the current Huffman node
		//       with another Huffman node by their
		//		 frequencies
		//		 returns 0, if the frequencies are equal
		//		 returns a positive integer, if the
		//		 current node has a greater frequency
		//		 returns a negative integer, if the
		//		 the current node has a smaller frequency
		public int compareTo(HuffmanNode other) {
			return freq - other.freq;
		}
	}
	
}