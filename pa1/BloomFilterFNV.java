package pa1;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * 
 * @author Tyler Bybee
 * @author Erin Elsbernd
 *
 */
public class BloomFilterFNV {
	
	
	private int filterSize; // expected (maximum) number of elements to be added
    private int numCurElements; // number of elements actually added to the Bloom filter
    private int numHashFunctions; // number of hash functions
    private int setSize; //Bloom Filter can store a set S of cardinality setSize
    private int bitsPerElement; 
    private BitSet filter;
    private long hash;
    private final long INIT;
    private final long PRIME;
    
   
//    public static void main(String[] args) {
//    	BloomFilterFNV bf = new BloomFilterFNV(10,3);
//    	System.out.println("size of filter: " + bf.filterSize());
//    	System.out.println("num elements: " + bf.dataSize());
//    	bf.add("Charlie");
//    	System.out.println("num elements: " + bf.dataSize());
//    	bf.add("Sarah");
//    	System.out.println("num elements: " + bf.dataSize());
//    	bf.add("Sarah");
//    	System.out.println("num elements: " + bf.dataSize());
//    	if (bf.appears("Bobby")) {
//    		System.out.println("Bobby appears");
//    	} else {
//    		System.out.println("Bobby doesn't appear");
//    	}
//    	if (bf.appears("Alejandro")) {
//    		System.out.println("Alejandro appears");
//    	} else {
//    		System.out.println("Alejandro doesn't appear");
//    	}
//    	bf.add("Lady gaga");
//    	System.out.println("num elements: " + bf.dataSize());
//    	
//    	
//    }
	
	/**
	 * Creates a Bloom filter that can store a set S of cardinality setSize. The size of the filter 
	 * should approximately be setSize * bitsPerElement.
		The number of hash functions should be the optimal choice which is ln 2×filterSize/setSize.
	 * @param setSize
	 * @param bitsPerElement
	 */
	public BloomFilterFNV(int setSize, int bitsPerElement) {
		this.bitsPerElement = bitsPerElement;
		this.setSize = setSize;
		this.filterSize = setSize * bitsPerElement;
		this.numHashFunctions =  (int) (Math.log(2.0) * filterSize/setSize);
		this.filter = new BitSet(filterSize);
		this.numCurElements = 0;
		this.PRIME = 109951168211L;
		this.INIT = 0xCBF29CE484222325L;
		this.hash = 0xCBF29CE484222325L;
		//printBitSet();
	}
	
	
	/**
	 * Adds the string s to the filter. Type of this method is void. This method
should be case-insensitive. For example, it should not distinguish between “Galaxy” and “galaxy”.
	 * @param s
	 */
	public void add(String s) {
		if (s == null || s.isEmpty()) {
			  return;
			}
		if (appears(s)) {
			return;
		}
		if (numCurElements + 1 > setSize) {
			return;
		}
		String tmp = s.toLowerCase();
		int length_of_string = tmp.length();
		for (int i = 0; i < numHashFunctions; i++) {
			tmp = s.toLowerCase();
			hash = INIT;
			long hashed = Math.abs(getNextHash((tmp + Integer.toString(i)  +  399 *i)) % filterSize);
			filter.set((int)hashed);	
		}
		numCurElements++;
		
	}
	/**
	 * Prints out the current values of the bits in the filter. Used for testing.
	 */
	private void printBitSet() {
		for (int i = 0; i < filterSize; i++) {
			if (filter.get(i)) {
				System.out.print("1");
			} else {
				System.out.print("0");
			}
			
		}
	}
	
	/**
	 * returns the hash of the input string using the FNV64 formula 
	 * @param s
	 * @return hash
	 */
	private long getNextHash(String s) {
		for (int j = 0; j < s.length(); j++) {
			hash ^= s.charAt(j);
			hash *= (long) (PRIME);
		}
		return hash;
	}
	
	/**
	 * Returns true if s appears in the filter; otherwise returns false. This method
must also be case-insensitive.
	 * @param s
	 * @return
	 */
	public boolean appears(String s) {
		if (s == null || s.isEmpty()) {
			  return false;
			}
		String tmp = s.toLowerCase();
		for (int i = 0; i < numHashFunctions; i++) {
			tmp = s.toLowerCase();
			hash = INIT;
			long hashed = Math.abs(getNextHash((tmp + Integer.toString(i)  +  399 *i)) % filterSize);
			//System.out.println("hashed: " + hashed);
			if(!filter.get((int)hashed)) {
				return false;
			}	
		}
		return true;
	}
	
	/**
	 * Returns the size of the filter (the size of the table).
	 * @return
	 */
	public int filterSize() {
		return filterSize;
	}
	
	/**
	 * dataSize() 
	 * @return
	 */
	public int dataSize() {
		return numCurElements;
	}
	
	/**
	 * numHashes() 
	 * @return
	 */
	public int numHashes() {
		return numHashFunctions;
	}

}
