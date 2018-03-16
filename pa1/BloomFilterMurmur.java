package pa1;

import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * 
 * @author Tyler Bybee
 * @author Erin Elsbernd
 *
 */
public class BloomFilterMurmur
{
	private int filterSize;
	private int numElements;
	private BitSet filter;
	private int numHashFunctions;
	private int bitsPerElement;

	// private
	/**
	 * Creates a Bloom lter that can store a set S of cardinality setSize. The
	 * size of the lter should approximately be setSize * bitsPerElement. The
	 * number of hash functions should be the optimal choice which is ln
	 * 2filterSize/setSize.
	 * 
	 * @param setSize
	 * @param bitsPerElement
	 */
	public BloomFilterMurmur(int setSize, int bitsPerElement)
	{
		numElements = 0;
		filter = new BitSet(setSize * bitsPerElement);
		filterSize = setSize * bitsPerElement;
		numHashFunctions = (int)(Math.log(2) * filterSize / setSize);
		this.bitsPerElement = bitsPerElement;
	}

	/**
	 * Adds the string s to the lter. Type of this method is void. This method
	 * should be case-insensitive. For example, it should not distinguish
	 * between \Galaxy" and \galaxy".
	 * 
	 * @param s
	 */
	public void add(String s)
	{
		s = s.toLowerCase();
		if (!this.appears(s))
		{
			long hashed = hash64(s) % filterSize;
			numElements += 1;
			filter.set((int) (hashed));
			for (int i = 1; i < numHashFunctions; i++)
			{
				hashed = nextHash(s, hashed) % filterSize;
				filter.set((int) (hashed));
			}
		}
	}

	/**
	 * Returns true if s appears in the lter; otherwise returns false. This
	 * method must also be case-insensitive.
	 * 
	 * @param s
	 * @return
	 */
	public boolean appears(String s)
	{
		s = s.toLowerCase();
		long hashed = hash64(s) % filterSize;
		if (!filter.get((int) hashed)){
			return false;
		}
		for (int i = 1; i < numHashFunctions; i++)
			hashed = nextHash(s, hashed) % filterSize;
		if (!filter.get((int) hashed)){
			return false;
		}

		return true;
	}

	/**
	 * Returns the size of the lter (the size of the table).
	 * 
	 * @return
	 */
	public int filterSize()
	{
		return filterSize;
	}

	/**
	 * Returns the number of elements added to the filter.
	 * 
	 * @return
	 */
	public int dataSize()
	{
		return numElements;
	}

	/**
	 * Returns the number of hash function used.
	 * 
	 * @return
	 */
	public int numHashes()
	{
		return numHashFunctions;
	}

	private static long hash64(final byte[] data, int length, int seed)
	{
		final long m = 0xc6a4a7935bd1e995L;
		final int r = 47;

		long h = (seed & 0xffffffffl) ^ (length * m);

		int length8 = length / 8;

		for (int i = 0; i < length8; i++)
		{
			final int i8 = i * 8;
			long k = ((long) data[i8 + 0] & 0xff)
					+ (((long) data[i8 + 1] & 0xff) << 8)
					+ (((long) data[i8 + 2] & 0xff) << 16)
					+ (((long) data[i8 + 3] & 0xff) << 24)
					+ (((long) data[i8 + 4] & 0xff) << 32)
					+ (((long) data[i8 + 5] & 0xff) << 40)
					+ (((long) data[i8 + 6] & 0xff) << 48)
					+ (((long) data[i8 + 7] & 0xff) << 56);

			k *= m;
			k ^= k >>> r;
			k *= m;

			h ^= k;
			h *= m;
		}

		switch (length % 8)
		{
		case 7:
			h ^= (long) (data[(length & ~7) + 6] & 0xff) << 48;
		case 6:
			h ^= (long) (data[(length & ~7) + 5] & 0xff) << 40;
		case 5:
			h ^= (long) (data[(length & ~7) + 4] & 0xff) << 32;
		case 4:
			h ^= (long) (data[(length & ~7) + 3] & 0xff) << 24;
		case 3:
			h ^= (long) (data[(length & ~7) + 2] & 0xff) << 16;
		case 2:
			h ^= (long) (data[(length & ~7) + 1] & 0xff) << 8;
		case 1:
			h ^= (long) (data[length & ~7] & 0xff);
			h *= m;
		}
		;

		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;

		return Math.abs(h);
	}

	/**
	 * Generates 64 bit hash from byte array with default seed value.
	 * 
	 * @param data
	 *            byte array to hash
	 * @param length
	 *            length of the array to hash
	 * @return 64 bit hash of the given string
	 */
	private static long hash64(final byte[] data, int length)
	{
		return hash64(data, length, 0xe17a1465);
	}

	/**
	 * Generates 64 bit hash from a string.
	 * 
	 * @param text
	 *            string to hash
	 * @return 64 bit hash of the given string
	 */
	private static long hash64(final String text)
	{
		final byte[] bytes = text.getBytes();
		return hash64(bytes, bytes.length);
	}

	/**
	 * returns the value of the next hash by converting the original string to a
	 * byte array and the previous hash value to a byte array, then
	 * concatenating them, then returning their hash value from the murmur hash
	 * function
	 */
	private static long nextHash(String s, long previous)
	{
		// int i = s.hashCode();
		byte[] bytes2 = s.getBytes();
		byte[] bytes1 = longToBytes(previous);
		byte[] bytes3 = new byte[bytes1.length + bytes2.length];
//		for (int i = 0; i < bytes3.length;)
//		{
//			int j = i;
//			bytes3[i] = bytes1[j];
//			j += bytes1.length;
//			bytes3[i] = bytes2[j];
//		}
		int i = 0;
		for (; i < bytes1.length; i++){
			bytes3[i] = bytes1[i];
		}
		for (int j = 0; j < bytes2.length; j++){
			bytes3[i]=bytes2[j];
		}
		return hash64(bytes3, bytes3.length);
	}

	/**
	 * converts long to byte array. taken from
	 * https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
	 * 
	 * @param x
	 * @return
	 */
	private static byte[] longToBytes(long x)
	{
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}
}
