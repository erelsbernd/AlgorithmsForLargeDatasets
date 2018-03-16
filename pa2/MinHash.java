package pa2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Riley Bird
 * @author Erin Elsbernd
 */

public class MinHash {

	// public static void main(String[] args) throws IOException {
	// MinHash mh = new MinHash("/Users/rileybird/Desktop/F17PA2", 40);
	// String[] docs = mh.allDocs();
	//
	// for(String s: docs) {
	// int[] sig = mh.allMinSigs.get(s);
	// for(int i = 0; i< sig.length; i++) {
	// System.out.print(sig[i]);
	// }
	// System.out.println();
	// }
	// System.out.println(docs.length);
	// System.out.println(mh.numTerms());
	// System.out.println(mh.numPermutations());
	// System.out.println(removeTerms("/Users/rileybird/eclipse-workspace/Project2/src/test.txt").getAbsolutePath());
	// System.out.println(mh.exactJaccard(docs[0], docs[300]));
	// mh.minHashSig(docs[0]);
	// System.out.println(mh.approximateJaccard(docs[0], docs[300]));
	// int[][] m = new int[40][2];
	// m = mh.minHashMatrix();
	// for(int i = 0; i < 40; i ++) {
	// for(int j = 0; j < 2; j++) {
	// System.out.print(m[i][j]);
	// }
	// System.out.println();
	// }
	// System.out.println(mh.numTerms());
	// }

	private int numPermutations;
	private File docs;
	private int numTerms;
	private String[] allDocs;
	private HashMap<String, Set<String>> allSets;
	private HashMap<String, int[]> allMinSigs;
	private int numDocs;
	private Set<String> allTerms;
	private FilenameFilter Filter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			String lowercaseName = name.toLowerCase();
			if (lowercaseName.contains(".txt")) {
				return true;
			} else {
				return false;
			}
		}
	};
	private int[] a;
	private int[] b;
	private int p;

	/**
	 * Constucts an instance of minHash where folder is the name of a folder
	 * containing our document collection for which we wish to construct MinHash
	 * matrix and numPermutations denotes the number of permutations to be used in
	 * creating the MinHash matrix.
	 * 
	 * @param folder
	 * @param numPermutations
	 * @throws IOException
	 */
	MinHash(String folder, int numPermutations) throws IOException {

		this.numPermutations = numPermutations;
		docs = new File(folder);
		allDocs = docs.list(Filter);
		numDocs = allDocs.length;
		allSets = getAllSets(allDocs, folder);
		allTerms = getAllTerms(allSets, allDocs);
		numTerms = allTerms.size();
		SecureRandom rand = new SecureRandom();
		p = getPrime(numTerms);
		a = new int[numPermutations];
		b = new int[numPermutations];
		for (int i = 0; i < numPermutations; i++) {
			a[i] = rand.nextInt(p);
			b[i] = rand.nextInt(p);
		}

		allMinSigs = new HashMap<String, int[]>();
		for (String s : allDocs) {
			allMinSigs.put(s, minHashSig(s));
		}
	}

	/**
	 * 
	 * @return an array of String consisting of all the names of files in the
	 *         document collection
	 */
	public String[] allDocs() {

		return allDocs;
	}

	/**
	 * Get names of two files (in the document collection) file1 and file2 as
	 * parameters and returns the exact Jaccard Similarity of the files
	 * 
	 * @param file1
	 * @param file2
	 * @return exactJaccardSim(file1, file2)
	 * @throws IOException
	 */
	public double exactJaccard(String file1, String file2) throws IOException {

		Set<String> termsf1 = allSets.get(file1);
		Set<String> termsf2 = allSets.get(file2);

		Set<String> union = new HashSet<String>();
		union.addAll(termsf1);
		union.addAll(termsf2);

		int intersection = termsf1.size() + termsf2.size() - union.size();

		return 1.0 * intersection / union.size();
	}

	/**
	 * 
	 * @param fileName
	 * @return Returns the MinHash the minhash signature of the document named
	 *         fileName, which is an array of ints.
	 * @throws IOException
	 */
	public int[] minHashSig(String fileName) throws IOException {
		int[] sig = new int[numPermutations];
		int minH = Integer.MAX_VALUE;
		int hash;
		Set<String> fileTerms = allSets.get(fileName);
		for (int i = 0; i < numPermutations; i++) {
			for (String s : fileTerms) {
				if (allTerms.contains(s)) {
					hash = (a[i] * s.hashCode() + b[i]) % p;
					if (hash < minH) {
						minH = hash;
					}
				}
			}
			sig[i] = minH;
			minH = Integer.MAX_VALUE;
		}

		return sig;
	}

	/**
	 * 
	 * @param file1
	 * @param file2
	 * @return Estimates and returns the Jaccard similarity of documents file1 and
	 *         file2 by comparing the MinHash signatures of file1 and file2.
	 * @throws IOException
	 */
	public double approximateJaccard(String file1, String file2) throws IOException {
		int f1sig[] = allMinSigs.get(file1);
		int f2sig[] = allMinSigs.get(file2);

		int count = 0;
		for (int i = 0; i < numPermutations; i++) {
			if (f1sig[i] == f2sig[i]) {
				count++;
			}
		}

		return 1.0 * count / numPermutations;

	}

	/**
	 * 
	 * @return Returns the MinHash Matrix of the collection.
	 * @throws IOException
	 */
	public int[][] minHashMatrix() throws IOException {
		int[][] minHashMat = new int[numPermutations][numDocs];
		int[] sig = new int[numPermutations];

		for (int i = 0; i < numDocs; i++) {
			sig = minHashSig(allDocs[i]);
			for (int j = 0; j < numPermutations; j++) {
				minHashMat[j][i] = sig[j];
			}
		}

		return minHashMat;
	}

	/**
	 * 
	 * @return Returns the number of terms in the document collection
	 * @throws IOException
	 */
	public int numTerms() throws IOException {

		return numTerms;
	}

	/**
	 * 
	 * @return Returns the number of permutations used to construct the MinHash
	 *         matrix
	 */
	public int numPermutations() {

		return numPermutations;
	}

	/**
	 * 
	 * 
	 * @param allSets
	 * @param allDocs
	 * @return set of all terms in our document set with stop words removed
	 * @throws IOException
	 */
	private static Set<String> getAllTerms(HashMap<String, Set<String>> allSets, String[] allDocs) throws IOException {
		Set<String> union = new HashSet<String>();
		for (String s : allDocs) {
			union.addAll(allSets.get(s));
		}
		return union;

	}

	/**
	 * 
	 * @param allDocs
	 * @param folder
	 * @return a Hashmap with key: docname and value: a set containing all the terms
	 *         in a file with Stop words removed
	 * @throws IOException
	 */
	private static HashMap<String, Set<String>> getAllSets(String[] allDocs, String folder) throws IOException {
		HashMap<String, Set<String>> allSets = new HashMap<String, Set<String>>();
		for (String s : allDocs) {
			allSets.put(s, getFileTerms(folder + "/" + s));
		}
		return allSets;
	}

	/**
	 * 
	 * @param fileName
	 * @return The set containing the terms from a file with stop words removed
	 * @throws IOException
	 */
	private static Set<String> getFileTerms(String fileName) throws IOException {
		Set<String> termsOfFile = new HashSet<String>();
		File file_clean = removeTerms(fileName);

		Scanner read = new Scanner(file_clean);

		while (read.hasNext()) {
			String s = read.next();
			if (!termsOfFile.contains(s)) {
				termsOfFile.add(s);
			}
		}
		read.close();
		file_clean.delete();
		return termsOfFile;
	}

	/**
	 * 
	 * @param doc
	 * @return a file with all the stop words removed
	 * @throws IOException
	 */
	private static File removeTerms(String doc) throws IOException {
		File file = new File(doc);
		File ret = File.createTempFile("file", ".txt", file.getParentFile());

		String charset = "UTF-8";

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(ret), charset));

		for (String line; (line = reader.readLine()) != null;) {
			line = line.toLowerCase();
			line = line.replace(".", "").replace(",", "").replace(":", "").replace(";", "").replace("'", "");
			line = line.replaceAll("(?i)\\bthe\\b", "");
			line = line.replaceAll("\\b\\w{1,2}\\b", "");
			writer.println(line);
		}

		reader.close();
		writer.close();

		return ret;
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

}
