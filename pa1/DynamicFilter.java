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

public class DynamicFilter {
	
	
	private int filterSize; // expected (maximum) number of elements to be added
    private int numCurElements; // number of elements actually added to the Bloom filter
    private int numHashFunctions; // number of hash functions
    private int setSize; //Bloom Filter can store a set S of cardinality setSize
    private int bitsPerElement; 
    private BitSet filter;
    private long hash;
    private long PRIME;
    private HashMap<Integer, HashMap<Integer, ArrayList<Long>>> filterRandMap;
    private HashMap<Integer, ArrayList<Long>> randMap;
    private ArrayList<BitSet> filterArray;
    private int filterID;
    private int maxSize;
    private HashMap<Integer, Long> primeMap;
    private HashMap<Integer, Integer> numHashesMap;
    private HashMap<Integer, Integer> filterSizeMap;
    private int numFilters;
	
/**
 * Creates a Dynamic Bloom filter. The size of the filter should approximately be setSize * bitsPerElement, 
 * where the initial setSize is 1000, and 2*setSize once the filter reaches it's capacity and a new bloom
 * filter needs to be created.
	The number of hash functions should be the optimal choice which is ln 2×filterSize/setSize.This will have
	to be set with each newly created bloom filter.
 * @param bitsPerElement
 */
public DynamicFilter(int bitsPerElement) {
	this.bitsPerElement = bitsPerElement;
	this.setSize = 1000;
	this.filterSize = setSize * bitsPerElement;
	this.numHashFunctions =  (int) (Math.log(2.0) * filterSize/setSize);
	this.filter = new BitSet(filterSize);
	this.filterID = 0;
	this.numCurElements = 0;
	this.maxSize = 1000;
	this.PRIME = getNextPrime(filterSize);
	generateRandomAB(PRIME);
	filterRandMap = new HashMap<Integer, HashMap<Integer, ArrayList<Long>>>();
	filterRandMap.put(filterID, randMap);
	filterArray = new ArrayList<BitSet>();
	primeMap = new HashMap<Integer, Long>();
	primeMap.put(filterID, PRIME);
	numHashesMap = new HashMap<Integer, Integer>();
	numHashesMap.put(filterID, numHashFunctions);
	filterSizeMap = new HashMap<Integer, Integer>();
	filterSizeMap.put(filterID, filterSize);
	this.numFilters = 1;
	//printBitSet();
}
/**
 * 
 * @param prime
 */
private void generateRandomAB(long prime) {
	
	randMap = new HashMap<Integer, ArrayList<Long>>();
	
	for (int i = 0; i < numHashFunctions; i++) {
		
		long a = ThreadLocalRandom.current().nextLong(prime);
		long b = ThreadLocalRandom.current().nextLong(prime);
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
	String tmp = s.toLowerCase();
	byte[] b_arr = tmp.getBytes();
	hash = 0;
	//System.out.println("numHashFunctions: "+numHashFunctions);
	for (int i = 0; i < numHashFunctions; i++) {
		//System.out.println("add_i_val: "+i);
		hash = getNextHash(tmp, tmp.length(), i);
		long hashed = Math.abs(hash % filterSize);
		//System.out.println("hashed: " + hashed);
		filter.set((int)hashed);	
	}
	numCurElements++;
	if (numCurElements >= maxSize) {
		//System.out.println("creating new filter oldID: "+filterID );
		filterArray.add(filterID, filter);
		createNewFilter();
	}
}

private void createNewFilter() {
	setSize = (maxSize*2);
	filterSize = setSize * bitsPerElement;
	numHashFunctions =  (int) (Math.log(2.0) * filterSize/setSize);
	filter = new BitSet(filterSize);
	filterID++;
	numCurElements = 0;
	maxSize = maxSize*2;
	long newPRIME = getNextPrime(filterSize);
	primeMap.put(filterID, newPRIME);
	generateRandomAB(newPRIME);
	filterRandMap.put(filterID, randMap);
	numHashesMap.put(filterID, numHashFunctions);
	filterSizeMap.put(filterID, filterSize);
	numFilters++;
	
}

private long getNextHash(String s, int length, int salt) {
	ArrayList<Long> list = randMap.get(salt);
	long a = list.get(0);
	long b = list.get(1);
	long hval = 0;
	
	hval = (long) (((a*s.hashCode()) + b) % primeMap.get(filterID));
	
	return hval;
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




private int getNextPrime(int n) {
    int flag = 0;
    for (int i = n+1; i < 2 * n; ++i) {
    	for( int j = 2; j < i; j++) {
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


private long getNextHash(String s, int length, int salt, int fID) {
	ArrayList<Long> list = filterRandMap.get(fID).get(salt);
	long a = list.get(0);
	long b = list.get(1);
	long hval = 0;
	hval = (long) (((a*s.hashCode()) + b) % primeMap.get(fID));
	return hval;
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
	//search previous filters first for the element
	if (numFilters > 1) {
		for (int k = 0; k < filterArray.size(); k++) {
			BitSet f = filterArray.get(k);
			int numHfunctions = numHashesMap.get(k);
			hash = 0;
			for (int h = 0; h < numHfunctions; h++) {
				hash = getNextHash(tmp, tmp.length(), h, k);
				long hashed = Math.abs(hash % filterSizeMap.get(k));
				//System.out.println("hashed: " + hashed);
				if(!f.get((int)hashed)) {
					return false;
				}
			}
			
		}
	}
	
	//search the current filter for the element
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
 * Returns the size of the current filter (the size of the table).
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
