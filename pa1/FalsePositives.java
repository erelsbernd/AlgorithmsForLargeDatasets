package pa1;

/**
 * @author Tyler Bybee
 * @author Erin Elsbernd
 */
import static org.junit.Assert.assertEquals;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

/**
 * 
 * @author Tyler Bybee
 * @author Erin Elsbernd
 */
public class FalsePositives {
	
	
	private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static SecureRandom rnd = new SecureRandom();
	public static BloomFilterRan bfran;
	public static BloomFilterFNV bfnv;
	public static DynamicFilter bfdynamic;
	public static BloomFilterMurmur bfmur;
	
	public static void main(String[] args) {
		
		double setSize = 1000.0;
		int bitsPerElement = 160; //20*16=320, 10*16 = 160
		bfran = new BloomFilterRan((int)setSize,bitsPerElement);
		bfnv = new BloomFilterFNV((int)setSize, bitsPerElement);
		bfdynamic = new DynamicFilter(bitsPerElement);
		bfmur = new BloomFilterMurmur((int)setSize, bitsPerElement);
		double fpran = 0.0;
		double fpfnv = 0.0;
		double fpdyn = 0.0;
		double fpmur = 0.0;
		
		for (int i = 0; i < 30; i++) {
			fpran += testFalsePositiveRAN(setSize, bitsPerElement);
			fpfnv += testFalsePositiveFNV(setSize, bitsPerElement);
			fpdyn += testFalsePositiveDynamic(setSize, bitsPerElement);
			fpmur += testFalsePositiveMurmur(setSize, bitsPerElement);
		}
		System.out.printf("mean ran fp: " + "%.8f", fpran/30.);
		System.out.println("");
		System.out.printf("mean fnv fp: " + "%.8f", fpfnv/30.);
		System.out.println("");
		System.out.printf("mean dynamic fp: " + "%.8f", fpdyn/30.);
		System.out.println("");
		System.out.printf("mean murmur fp: " + "%.8f", fpmur/30.);
		
//		double val = 54580.0;
//		double val2 = 200000.0;
//		System.out.println(val/val2);
		
	}
	
	
	
	
	public static String randomString( int len ){
		   StringBuilder sb = new StringBuilder( len );
		   for( int i = 0; i < len; i++ ) 
		      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		   return sb.toString();
		}
	
	
	public static double testFalsePositiveRAN(double setSize, int bitsPerElement) {
		HashMap<String, Boolean> list = new HashMap<String, Boolean>();
		double countFalsePositive = 0.;
		for (int i = 0; i < setSize; i++) {
			String s = randomString(bitsPerElement/16);
			list.put(s, true);
			bfran.add(s);
		}
		
		for (int i = 0; i < setSize; i++) {
			String s = randomString(bitsPerElement/16);
			while (list.containsKey(s)) {
				s = randomString(bitsPerElement/16);
			}
			if (bfran.appears(s)) {
				countFalsePositive++;
			}
		}
		//System.out.println("RAN false positive for "+setSize+": " + countFalsePositive);
		return countFalsePositive/setSize;
	}
	
	public static double testFalsePositiveFNV(double setSize, int bitsPerElement) {
		HashMap<String, Boolean> list = new HashMap<String, Boolean>();
		double countFalsePositive = 0.;
		for (int i = 0; i < setSize; i++) {
			String s = randomString(bitsPerElement/16);
			list.put(s, true);
			bfnv.add(s);
		}
		
		for (int i = 0; i < setSize; i++) {
			String s = randomString(bitsPerElement/16);
			while (list.containsKey(s)) {
				s = randomString(bitsPerElement/16);
			}
			if (bfnv.appears(s)) {
				countFalsePositive++;
			}
		}
		
		//System.out.println("FNV false positive for "+setSize+": " + countFalsePositive);
		
		return countFalsePositive/setSize;
	}
	
	public static double testFalsePositiveDynamic(double setSize, int bitsPerElement) {
		HashMap<String, Boolean> list = new HashMap<String, Boolean>();
		double countFalsePositive = 0.;
		for (int i = 0; i < setSize; i++) {
			String s = randomString(bitsPerElement/16);
			list.put(s, true);
			bfdynamic.add(s);
		}
		
		for (int i = 0; i < setSize; i++) {
			String s = randomString(bitsPerElement/16);
			while (list.containsKey(s)) {
				s = randomString(bitsPerElement/16);
			}
			if (bfdynamic.appears(s)) {
				countFalsePositive++;
			}
		}
		//System.out.println("Dynamic false positive for "+setSize+": " + countFalsePositive);
		return countFalsePositive/setSize;
	}
	
	public static double testFalsePositiveMurmur(double setSize, int bitsPerElement) {
		HashMap<String, Boolean> list = new HashMap<String, Boolean>();
		double countFalsePositive = 0.;
		for (int i = 0; i < setSize; i++) {
			String s = randomString(bitsPerElement/16);
			list.put(s, true);
			bfmur.add(s);
		}
		
		for (int i = 0; i < setSize; i++) {
			String s = randomString(bitsPerElement/16);
			while (list.containsKey(s)) {
				s = randomString(bitsPerElement/16);
			}
			if (bfmur.appears(s)) {
				countFalsePositive++;
			}
		}
		//System.out.println("Murmur false positive for "+setSize+": " + countFalsePositive);
		return countFalsePositive/setSize;		
	}

}
