package pa1;

/**
 * @author Tyler Bybee
 * @author Erin Elsbernd
 */
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BloomFilterRan {
	
	
	private int filterSize; // expected (maximum) number of elements to be added
    private int numCurElements; // number of elements actually added to the Bloom filter
    private int numHashFunctions; // number of hash functions
    private int setSize; //Bloom Filter can store a set S of cardinality setSize
    private int bitsPerElement; 
    private BitSet filter;
    private long hash;
    private final long PRIME;
    private HashMap<Integer, ArrayList<Long>> randMap;
    
//    public static void main(String[] args) {
//    	BloomFilterRan bf = new BloomFilterRan(10,3);
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
//    }
	
/**
 * Creates a Bloom filter that can store a set S of cardinality setSize. The size of the filter should approximately be setSize * bitsPerElement.
	The number of hash functions should be the optimal choice which is ln 2×filterSize/setSize.
 * @param setSize
 * @param bitsPerElement
 */
public BloomFilterRan(int setSize, int bitsPerElement) {
	this.bitsPerElement = bitsPerElement;
	this.setSize = setSize;
	this.filterSize = setSize * bitsPerElement;
	this.numHashFunctions =  (int) (Math.log(2.0) * filterSize/setSize);
	this.filter = new BitSet(filterSize);
	this.numCurElements = 0;
	this.PRIME = getNextPrime((long)filterSize);
	generateRandomAB();
	//printBitSet();
}

/**
 * Generates random a,b value pairs for each of the k hash functions and stores them in the randMap.The key
 * is the index of k hashfunction.
 */
private void generateRandomAB() {
	
	randMap = new HashMap<Integer, ArrayList<Long>>();
	
	for (int i = 0; i < numHashFunctions; i++) {
		
		long a = ThreadLocalRandom.current().nextLong(PRIME);
		long b = ThreadLocalRandom.current().nextLong(PRIME);
		ArrayList<Long> list = new ArrayList<Long>();
		list.add(0, a);
		list.add(1, b);

		randMap.put(i, list);
	}
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
		//System.out.println(s+" already appears");
		//printBitSet();
		return;
	}
	if (numCurElements + 1 > setSize) {
		return;
	}
	String tmp = s.toLowerCase();
	byte[] b_arr = tmp.getBytes();
	hash = 0;
	for (int i = 0; i < numHashFunctions; i++) {
		hash = getNextHash(tmp, tmp.length(), i);
		long hashed = Math.abs(hash % filterSize);
		//System.out.println("hashed: " + hashed);
		filter.set((int)hashed);	
	}
	numCurElements++;
}

private long getNextHash(String s, int length, int salt) {
	ArrayList<Long> list = randMap.get(salt);
	long a = list.get(0);
	long b = list.get(1);
	hash = (long) (((a*s.hashCode()) + b) % PRIME);
	
	return hash;
}

private void printBitSet() {
	System.out.print("Bit set: ");
	for (int i = 0; i < filterSize; i++) {
		if (filter.get(i)) {
			System.out.print("1");
		} else {
			System.out.print("0");
		}
		
	}
}



/**
 * Takes in the filtersize and gets the next smallest prime after the filter size.
 * @param n
 * @return i, which is the next prime, or 0 if nothing is found
 */
private long getNextPrime(long n) {
    int flag = 0;
    for (long i = n+1; i < 2 * n; ++i) {
    	for( long j = 2; j < i; j++) {
            if(i % j == 0) {
                flag = 0;
                break;
            }
            else {
                flag = 1;
            }
        }
    	if(flag == 1) {
            return i;
        }
    }
    return 0;
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
	byte[] b_arr = tmp.getBytes();
	hash = 0;
	for (int i = 0; i < numHashFunctions; i++) {
		//hash = 0;
		hash = getNextHash(tmp, tmp.length(), i);
		long hashed = Math.abs(hash % filterSize);
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
