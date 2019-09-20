package huffman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
<<<<<<< HEAD
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;
=======
import java.util.ArrayList;
>>>>>>> c17ce2eef43a1950c52e172c220ebe3800a972a0

public class Decoder {

	private Tree tree;

	/**
	 * @param treeFileName
	 * @param inputFileName
	 * @throws FileNotFoundException
	 */
	public void decode(String inputFileName) throws FileNotFoundException {
		File inputFile = new File(inputFileName);

		if (inputFile.exists()) { // makes sure both files exists before continuing
			initStreams(inputFileName);
		} else {
			throw new FileNotFoundException();
		}
	}

	/**
	 * @param treeFileName
	 * @param inputFileName
	 */
	private void initStreams(String inputFileName) { // creates stream pointers
		BufferedWriter writer = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(inputFileName);
			readHeader(fis);

			String outputFileName = "uncompressed-" + inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".txt";
			writer = new BufferedWriter(new FileWriter(outputFileName));

			decode(fis, writer); // method call to decode text
		} catch (IOException e) {
			e.printStackTrace();
		} finally { // closes streams
			try {
				fis.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param fis
	 * @param writer
	 * @throws IOException
	 */
	private void decode(FileInputStream fis, BufferedWriter writer) throws IOException {

		StringBuilder littleE = new StringBuilder();

		while (fis.available() > 0) { // while there are bytes to read
			int av = fis.available();
			byte[] bytes = null;

			if (av > 256) { // create byte array with size 256
				bytes = new byte[256];
			} else { // if there are less than 256 bytes left to read create byte array with smaller
						// size
				bytes = new byte[av]; // last byte of file is not character data
			}

			fis.read(bytes); // read bytes into byte array
			for (byte b : bytes) { // add binary representation of byte to string
				littleE.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
			}
		}

		Node cNode = this.tree.getRoot(); // start at root of tree

		for (int i = 0; i < littleE.length(); i++) {
			int ai = bigE(i);

			if (cNode.isLeaf()) { // if leaf has been reached
				writer.write(cNode.getCharacter()); // write the character
				cNode = this.tree.getRoot(); // start over from the root
			}

			char cChar = littleE.charAt(ai); // read character

			if (cChar == '0') { // if character is 0 go left
				cNode = cNode.left();
			} else if (cChar == '1') { // if character is 1 go right
				cNode = cNode.right();
			}
		}
	}
	
	public void readHeader(FileInputStream fis) throws IOException {
		int bytesToRead = fis.read();
		int bytesOfCharCodeData = 0;
		for(int i = 0; i < bytesToRead; i++) {
			bytesOfCharCodeData += fis.read();
		}
		
		byte[] charCodeData = new byte[bytesOfCharCodeData];
		fis.read(charCodeData);
		StringBuilder sb = new StringBuilder();
		for(byte b : charCodeData) {
			sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
		}
		
		StringBuilder fixed = new StringBuilder();
		
		for(int i = 0; i < sb.length(); i++) {
			int ai = bigE(i);
			fixed.append(sb.charAt(ai));
		}
		
		ArrayList<String> charCodes = new ArrayList<String>();
		
		for(int i = 0; i < fixed.length(); i *= 1) {
			String binCharacter = fixed.substring(i, i + 8);
			char character = (char) Integer.parseInt(binCharacter, 2);
			i += 8;
			String stringInt = fixed.substring(i, i + 8);
			i += 8;
			int codeLength = Integer.parseInt(stringInt, 2);
			String code = fixed.substring(i, i + codeLength);
			i += codeLength;
			
			String charCode = character + code;
			charCodes.add(charCode);
		}
		
		TreeMaker tm = new TreeMaker();
		this.tree = tm.treeFromCodes(charCodes);
		
	}

<<<<<<< HEAD
	private void createTree(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		this.tree = (Tree) ois.readObject(); // creates tree from .data file
		
		
		ArrayList<String> c = this.tree.getCharCodes();
		for(String s : c) {
			System.out.println(s);
		}
	}
	
	public static void testDecode() throws IOException {
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = new FileInputStream(new File("output.bin"));
		File f = new File("output.bin");
//		while(f.length() > 0) {
			byte[] b = new byte[2];
			fis.read(b); // it does work
			for(byte by : b) {
				for(int n = 7; n >= 0; n--) {
					sb.append(((by >> (n)) & 1));
				}
			}
			
//		}
			
			
	}
	
	public static void main(String[] args) throws IOException {
		testDecode();
=======
	/**
	 * order for reading little endian as big endian
	 * 
	 * @param littleE
	 * @return
	 */
	private int bigE(int littleE) {
		int bigE = littleE / 8;
		bigE *= 8;
		bigE += 8 - 1;
		bigE -= littleE % 8;

		return bigE;
>>>>>>> c17ce2eef43a1950c52e172c220ebe3800a972a0
	}
}