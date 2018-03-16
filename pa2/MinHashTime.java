package pa2;
import java.io.IOException;

public class MinHashTime {
	public static void main(String[] args) throws IOException {
		timer("/Users/rileybird/Desktop/F17PA2", 40);
	}

	public static void timer(String folder, int numPermutations) throws IOException {
		
		MinHash mh = new MinHash(folder, numPermutations);
		long startTime = System.nanoTime();
		String[] docs = mh.allDocs();
		for(int i = 0; i < docs.length - 1; i++) {
			for(int j = i + 1; j < docs.length; j++) {
				@SuppressWarnings("unused")
				double exactJac = mh.exactJaccard(docs[i], docs[j]);
			}
		}	
		long endTime = System.nanoTime();
		long duration = (endTime - startTime)/1000000;
		System.out.println("Time taken to compute exact Jacard (ms): " + duration);
		
		startTime = System.nanoTime();
		int[][] minHashMatrix = mh.minHashMatrix();
		endTime = System.nanoTime();
		duration = (endTime - startTime)/1000000;	
		System.out.println("Time taken to compute minHash Matrix (ms): " + duration);
		
		startTime = System.nanoTime();
		for(int i = 0; i < docs.length - 1; i++) {
			for(int j = i + 1; j < docs.length; j++) {				
				int count = 0;	
				for(int k = 0; k < numPermutations; k++) {		
					if(minHashMatrix[k][i] == minHashMatrix[k][j]) {
						count ++;
					}
				}
				@SuppressWarnings("unused")
				double approxJac = 1.0 * count/numPermutations;
			}
		}
		endTime = System.nanoTime();
		duration = (endTime - startTime)/1000000;
		System.out.println("Time taken to compute approx. Jacard (ms): " + duration);
	}
	
}

