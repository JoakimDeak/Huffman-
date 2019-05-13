package huffman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Scanner;

public class Encoder {

	private File inputFile;

	public Encoder(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		if (file.exists()) { // makes sure a file with the given name exists
			this.inputFile = file;
		} else {
			throw new FileNotFoundException();
		}

	}

	public void encode() {
		Tree tree = null;
		TreeMaker tm = new TreeMaker();
		try {
			tree = tm.makeTree(this.inputFile);
			encode(tree.getCharCodes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void encode(ArrayList<String> charCodes) throws IOException {

		Scanner sc = new Scanner(this.inputFile);
		String outputFileName = "Encoded-"
				+ this.inputFile.getName().substring(0, this.inputFile.getName().lastIndexOf('.')) + ".bin";
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFileName)));

		while (sc.hasNextLine()) { // goes through every line
			String cLine = sc.nextLine();
			for (int i = 0; i < cLine.length(); i++) { // goes through every character
				char cChar = cLine.charAt(i);
				for (String code : charCodes) {
					if (code.charAt(0) == cChar) { // find the code for current character
						writer.write(code.substring(1)); // excluding first character which is the character to be
															// encoded
					}
				}
			}
		}

		sc.close();
		writer.close();
	}
// writes the encoded text as binary to actually compress original text
	private void testEncode(ArrayList<String> charCodes) throws IOException {
		Scanner sc = new Scanner(this.inputFile);
		FileOutputStream fos = new FileOutputStream(new File("output.bin"));

		StringBuilder encoded = new StringBuilder();
		int encodedLengthBeforeWrite = 256; // how many bits will be written at a time

		while (sc.hasNextLine()) { // go through every line
			String cLine = sc.nextLine();
			for (int i = 0; i < cLine.length(); i++) { // go through every character
				char cChar = cLine.charAt(i);
				for (String code : charCodes) { // find entry for current character in
					if (code.charAt(0) == cChar) {
						encoded.append(code.substring(1)); // add character code to stringbuilder
					}
				}
				if (encoded.length() >= encodedLengthBeforeWrite) {
					encoded = write(encodedLengthBeforeWrite, encoded, fos);
				}
			}
		}
		if(encoded.length() > 0) { // if number of characters wasnt multiple of the given number
			int bitsToBeAdded = 8 - encoded.length() % 8; // make it multiple of eight by adding extra 0s
			for(int i = 0; i < bitsToBeAdded; i++) {
				encoded.append('0');
			}
			write(encoded.length(), encoded, fos); // write the remaining characters
		}
		sc.close();
		fos.close();
	}
	
	private StringBuilder write(int charsToWrite, StringBuilder input, FileOutputStream fos) throws IOException {
		
		BitSet set = new BitSet();
		for (int j = 0; j < charsToWrite; j++) { // takes string of 0s and 1s and converts to bitset
			if (input.charAt(j) == '1') {
				set.set(j);
			}
		}
		fos.write(set.toByteArray()); // writes the bitset to output file as byte array
		input.delete(0, charsToWrite); // deletes characters once they have been written
		
		return input;
	}
}
