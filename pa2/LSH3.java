package pa2;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * @author Riley Bird
 * @author Erin Elsbernd
 */

public class LSH3 {

	public static void main(String[] args) throws IOException {
		MinHash mh = new MinHash("/Users/rileybird/Desktop/F17PA2", 400);
		String[] docs = mh.allDocs();
		LSH3 lsh = new LSH3(mh.minHashMatrix(), docs, 10);
		ArrayList<String> near = lsh.nearDuplicatesOf(docs[301]);
		for (String s : near) {
			System.out.println(s);
		}
	}

	private int numRows;
	private int numRowsInEachBand;
	private int numCols;
	private ArrayList<HashSet<String>> table;
	private int[] a;
	private int[] b;
	private SecureRandom rand;
	private int p;
	private int numBands;
	private ArrayList<String> docList;
	private ArrayList<ArrayList<Integer>> docHashCodeList;

	/**
	 * Constructs an instance of LSH, where docNames is an array of Strings
	 * consisting of names of documents/files in the document collection, and
	 * minHashMatrix is the MinHash matrix of the document collection and bands is
	 * the number of bands to be used to perform locality sensitive hashing
	 * 
	 * @param minHashMatrix
	 * @param docNames
	 * @param bands
	 */
	public LSH3(int[][] minHashMatrix, String[] docNames, int bands) {
		if (bands == 0)
			return;

		numBands = bands;
		numRows = minHashMatrix.length;
		numRowsInEachBand = numRows / bands;
		numCols = minHashMatrix[0].length;
		docHashCodeList = new ArrayList<ArrayList<Integer>>();

		table = new ArrayList<HashSet<String>>();
		for (int i = 0; i < numBands; i++) {
			HashSet<String> set = new HashSet<String>();
			table.add(set);
		}

		docList = new ArrayList<String>();
		for (int i = 0; i < docNames.length; i++) {
			docList.add(i, docNames[i]);
		}

		a = new int[bands];
		b = new int[bands];
		rand = new SecureRandom();
		p = getPrime(numCols);
		populateAandB();

		for (int col = 0; col < numCols; col++) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (int b = 0; b < numBands; b++) {

				int[] arr = new int[numRowsInEachBand];
				int index = 0;
				for (int k = 0; k < numRowsInEachBand; k++) {
					arr[index] = minHashMatrix[(b * numRowsInEachBand) + k][col];
					index++;
				}

				int hashVal = Math.abs(getHashCode(arr, b));
				table.get(hashVal).add(docNames[col]);
				list.add(b, hashVal);
			}
			docHashCodeList.add(col, list);
		}

	}

	/**
	 * 
	 * @param docName
	 * @return list
	 */
	public ArrayList<String> nearDuplicatesOf(String docName) {
		int col = docList.indexOf(docName);
		ArrayList<Integer> hashList = new ArrayList<Integer>(docHashCodeList.get(col));
		Set<String> intersection = new HashSet<String>(table.get(hashList.get(0)));
		for (int i = 1; i < hashList.size(); i++) {
			intersection.retainAll(table.get(hashList.get(i)));
		}
		ArrayList<String> list = new ArrayList<String>(intersection);

		return list;
	}

	/**
	 * get next largest prime after M
	 * 
	 * @param M
	 * @return p
	 */
	private static int getPrime(int M) {
		int p = M;
		while (!isPrime(p)) {
			p++;
		}
		return p;
	}

	/**
	 * says if n is prime or not
	 * 
	 * @param n
	 * @return true or false
	 */
	private static boolean isPrime(int n) {
		if (n < 2)
			return false;
		if (n == 2)
			return true;
		if (n % 2 == 0)
			return false;
		for (int i = 3; i <= Math.sqrt(n); i += 2) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * populate arrays a and b with random values
	 */
	private void populateAandB() {
		for (int i = 0; i < numBands; i++) {
			a[i] = rand.nextInt(p);
			b[i] = rand.nextInt(p);
		}
	}

	/**
	 * gets the hash value of an array of ints
	 * 
	 * @param column
	 * @param bandID
	 * @return hash % numBands
	 */
	private int getHashCode(int[] column, int bandID) {
		int hash = 0;
		for (int i = 0; i < column.length; i++) {
			hash += (a[bandID] * column[i] + b[bandID]) % p;
		}
		return hash % numBands;
	}

}
