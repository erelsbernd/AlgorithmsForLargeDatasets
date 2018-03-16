package pa2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;
import java.security.SecureRandom;

public class LSH {
	private int numRows;
	private int numRowsInEachBand;
	private int numCols;
	//HashMap<DocID Integer, ArrayList<Map<Int BandID, Int HashCode of Tuple>>>
	private ArrayList<ArrayList<HashSet<String>>> table;
	private int[] a;
	private int[] b;
	private SecureRandom rand;
	private int p;
	private int numBands;
	private ArrayList<String> docList;
	private ArrayList<ArrayList<Integer>> docHashCodeList;
	private int T;
	
	
	public static void main(String[] args) throws IOException {
		MinHash mh = new MinHash("/Users/erelsbernd/Desktop/test435pa2", 300);  //was 400
		String[] docs = mh.allDocs();
		//docs size 3001
		LSH lsh = new LSH(mh.minHashMatrix(), docs,10);  //25 gets 2
		//55, gets all
		//25, ges 2
		//50, 1
		//
		
		ArrayList<String> near = lsh.nearDuplicatesOf(docs[10]);
		for (String s : near) {
			System.out.println(s);
		}
//		
//		ArrayList<String> near2 = lsh.nearDuplicatesOf2(docs[200],mh.minHashMatrix());
//		for (String s : near2) {
//			System.out.println(s);
//		}
	}
	
	
	
	/**
	 * Constructs an instance
of LSH, where docNames is an array of Strings consisting of names of documents/files in the
document collection, and minHashMatrix is the MinHash matrix of the document collection and
bands is the number of bands to be used to perform locality sensitive hashing
	 * @param minHashMatrix
	 * @param docNames
	 * @param bands
	 */
	public LSH(int[][] minHashMatrix, String[] docNames, int bands) {
		if (bands == 0)
			return;

		numBands = bands;
		numRows = minHashMatrix.length;
		numRowsInEachBand = numRows / bands;
		numCols = minHashMatrix[0].length;
		docHashCodeList = new ArrayList<ArrayList<Integer>>();
		T = 10*numCols;
		// initialize a and b with random primes
		a = new int[bands];
		b = new int[bands];
		rand = new SecureRandom();
		p = getPrime(T);
		populateAandB();
		
		table = new ArrayList<ArrayList<HashSet<String>>>(numBands);
		for (int i = 0; i < numBands; i++) {
			ArrayList<HashSet<String>> list = new ArrayList<HashSet<String>>(T);
			table.add(list);
			for (int j = 0; j < T; j++) {
				HashSet<String> set = new HashSet<String>(numCols);
				table.get(i).add(j, set);
			}
		}
		
		
		docList = new ArrayList<String>(numCols);
		for (int i = 0; i < docNames.length; i++) {
			docList.add(i,docNames[i]);
		}
		
		
		// loop through each column and band of the minHashMatrix and hash the tuples
		for (int col = 0; col < numCols; col++) {

			for (int b = 0; b < numBands; b++) {
				int[] arr = new int[numRowsInEachBand];
				int index = 0;
				//get the tuples for a particular band and store in arr
				for (int k = 0; k < numRowsInEachBand; k++) {
					arr[index] = minHashMatrix[(b * numRowsInEachBand) + k][col] +b + 399*b;
					index++;
				}
				int hashVal = Math.abs(getHashCode(arr,b));
				table.get(b).get(hashVal).add(docNames[col]);
			}
			
			
		}
		
		
	}
	
	
	/**
	 * 
	 * @param docName
	 * @return list
	 */
	public ArrayList<String> nearDuplicatesOf(String docName) {
		int col = docList.indexOf(docName);
		
		//each index of hashList will store the corresponding hashCode for that band for docName
		ArrayList<Integer> hashList = new ArrayList<Integer>(docHashCodeList.get(col));
		
	//	System.out.println("T: " +T);
		//we will get the set docName was hashed to stored in at band0
		Set<String> intersection = new HashSet<String>(table.get(0).get(hashList.get(0)));
//		Iterator<String> itr1 = intersection.iterator();
//		System.out.println("nextHashCode: " + docHashCodeList.get(col).get(0));
//        while(itr1.hasNext()){
//            System.out.println(" Iterating over HashSet in Java current object: " + itr1.next().toString());
//        }
		for (int i = 1; i < numBands; i++) {
			int nextHashCode = hashList.get(i);
//			System.out.println("nextHashCode: " + nextHashCode);
			HashSet<String> set = new HashSet<String>( table.get(i).get(nextHashCode));
//			Iterator<String> itr = set.iterator();
//            while(itr.hasNext()){
//                System.out.println(" Iterating over HashSet in Java current object: " + itr.next().toString());
//            }
			intersection.retainAll(table.get(i).get(nextHashCode));
		}
		ArrayList<String> list = new ArrayList<String>(intersection);
		
		return list;
	}
	
	
	
	/**
	 * 
	 * @param docName
	 * @return list
	 */
	public ArrayList<String> nearDuplicatesOf2(String docName, int[][] minHashMatrix) {
		int col = docList.indexOf(docName);
		int[] hashCodearr = new int[numBands];
		
		int[] arr = new int[numRowsInEachBand];
		for (int b = 0; b < numBands; b++) {
			
			int index = 0;
			//get the tuples for a particular band and store in arr
			for (int k = 0; k < numRowsInEachBand; k++) {
				arr[index] = minHashMatrix[(b * numRowsInEachBand) + k][col] +b +399*b;
				index++;
			}
			int hashVal = Math.abs(getHashCode(arr,b));
			hashCodearr[b] = hashVal;
		}
		
		//we will get the set docName was hashed to stored at band0
		Set<String> intersection = new HashSet<String>(table.get(0).get(hashCodearr[0]));
		
		for (int i = 1; i < numBands; i++) {
			int nextHashCode = hashCodearr[i];
			intersection.retainAll(table.get(i).get(nextHashCode));
		}
		ArrayList<String> list = new ArrayList<String>(intersection);
		return list;
	}
	
	/**
	 * 
	 * @param docName
	 * @return list
	 */
	public ArrayList<String> nearDuplicatesOf3(String docName, int[][] minHashMatrix) {
		int col = docList.indexOf(docName);
		int[] hashCodearr = new int[numBands];
		
		int[] arr = new int[numRowsInEachBand];
		for (int b = 0; b < numBands; b++) {
			
			int index = 0;
			//get the tuples for a particular band and store in arr
			for (int k = 0; k < numRowsInEachBand; k++) {
				arr[index] = minHashMatrix[(b * numRowsInEachBand) + k][col] +b +399*b;
				index++;
			}
			int hashVal = Math.abs(getHashCode(arr,b));
			hashCodearr[b] = hashVal;
		}
		
		//we will get the set docName was hashed to stored at band0
		HashMap<String,Integer> map = new HashMap<String, Integer>();
		Set<String> intersection = new HashSet<String>(table.get(0).get(hashCodearr[0]));
		
		
		for (int i = 1; i < numBands; i++) {
			int nextHashCode = hashCodearr[i];
			intersection.retainAll(table.get(i).get(nextHashCode));
		}
		ArrayList<String> list = new ArrayList<String>(intersection);
		return list;
	}
	
	/**
	 * get next largest prime after M
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
		for(int i = 0; i < numBands; i++) {
			a[i] = rand.nextInt(p);
			b[i] = rand.nextInt(p);
		}
	}
	

	
	/**
	 * gets the hash value of an array of ints
	 * @param column
	 * @param bandID
	 * @return hash % T
	 */
	private int getHashCode(int[] arr, int bandID) {
		int hash = 0;
		for (int i = 0; i < arr.length; i++) {
			hash += (a[bandID]*arr[i] + b[bandID])%p;
		}
		return hash % T;
	}
	


}
