package pa2;

import java.io.IOException;

/**
 * @author Riley Bird
 * @author Erin Elsbernd
 */

public class MinHashAccuracy {

	public static void main(String[] args) throws IOException {
		accuracy("/Users/erelsbernd/Desktop/space", 800, .09);
	}

	/**
	 * Tests the accuracy of computing exact Jaccard v.s. approximate Jaccard for
	 * every pair of files in a document collection within a given error between 0
	 * and 1 and number of permutations to be used in the minHashMatrix.
	 * 
	 * @param folder
	 * @param numPermutations
	 * @param error
	 * @throws IOException
	 */
	public static void accuracy(String folder, int numPermutations, double error) throws IOException {
		MinHash mh = new MinHash(folder, numPermutations);
		String[] docs = mh.allDocs();
		int count = 0;
		int numPairs = 0;
		for (int i = 0; i < docs.length - 1; i++) {
			for (int j = i + 1; j < docs.length; j++) {
				numPairs++;
				double exactJac = mh.exactJaccard(docs[i], docs[j]);
				double approxJac = mh.approximateJaccard(docs[i], docs[j]);
				if (Math.abs(exactJac - approxJac) > error) {
					count++;
				}
			}
		}
		System.out.println("number of pairs that differ by an error of " + error + " is: " + count);
		System.out.println("number of pairs: " + numPairs);
		System.out.println("Accuracy: " + (1 - 1.0 * count / numPairs) * 100);

	}
}
