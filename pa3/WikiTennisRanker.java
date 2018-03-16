package pa3;
/**
 * 
 * @author Riley Bird, Erin Elsbernd
 *
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WikiTennisRanker {
public static ArrayList<String> top10rank1;
public static ArrayList<String> top10In1;
public static ArrayList<String> top10Out1;
public static ArrayList<String> top100rank5;
public static ArrayList<String> top100In5;
public static ArrayList<String> top100Out5;



	public static void main(String[] args) throws IOException {
		//for epsilon = 0.01
		String filename1 = "MyWikiGraph epsilon0.005.txt";
		PageRank pr = new PageRank(filename1, 0.01);
		System.out.println("topKIndegree e = 0.01: "+ pr.topKInDegree(10));
		System.out.println("topKOutdegree e = 0.01: "+ pr.topKOutDegree(10));
		System.out.println("topKRank e = 0.01: "+ pr.topKPageRank(10));
		System.out.println("Iterations e = 0.01: "+ pr.count);
		
		//Jaccard Similarity
		top10rank1 = new ArrayList<String>(pr.topKPageRank(10));
		top10In1 = new ArrayList<String>(pr.topKInDegree(10));
		top10Out1 = new ArrayList<String>(pr.topKOutDegree(10));
		double jacRankIn1 = exactJaccard(top10rank1, top10In1);
		double jacRankOut1 = exactJaccard(top10rank1, top10Out1);
		double jacInOut1 = exactJaccard(top10In1, top10Out1);
		System.out.println("Jaccard of top 10 rank and in degree e = 0.01: "+ jacRankIn1);
		System.out.println("Jaccard of top 10 rank and out degree e = 0.01: "+ jacRankOut1);
		System.out.println("Jaccard of top 10 in degree and out degree e = 0.01: "+ jacInOut1);
		
		//for epsilon = 0.005
//		PageRank pr2 = new PageRank(filename1, 0.005);
//		System.out.println("topKIndegree e = 0.005: "+ pr2.topKInDegree(10));
//		System.out.println("topKOutdegree e = 0.005: "+ pr2.topKOutDegree(10));
//		System.out.println("topKRank e = 0.005: "+ pr2.topKPageRank(10));
//		System.out.println("Iterations e = 0.005: "+ pr2.count);
//		
//		//Jaccard Similarity
//		top100rank5 = new ArrayList<String>(pr2.topKPageRank(100));
//		top100In5 = new ArrayList<String>(pr2.topKInDegree(100));
//		top100Out5 = new ArrayList<String>(pr2.topKOutDegree(100));
//		double jacRankIn5 = exactJaccard(top100rank5, top100In5);
//		double jacRankOut5 = exactJaccard(top100rank5, top100Out5);
//		double jacInOut5 = exactJaccard(top100In5, top100Out5);
//		System.out.println("Jaccard of top 100 rank and in degree e = 0.005: "+ jacRankIn5);
//		System.out.println("Jaccard of top 100 rank and out degree e = 0.005: "+ jacRankOut5);
//		System.out.println("Jaccard of top 100 in degree and out degree e = 0.005: "+ jacInOut5);
		
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
	public static double exactJaccard(ArrayList<String> s1, ArrayList<String> s2) throws IOException {

		Set<String> termsf1 = new HashSet<String>(s1);
		Set<String> termsf2 = new HashSet<String>(s2);
		System.out.println("termsf1 size: "+termsf1.size());
		System.out.println("termsf2 size: "+termsf2.size());
		Set<String> union = new HashSet<String>();
		union.addAll(termsf1);
		union.addAll(termsf2);

		int intersection = termsf1.size() + termsf2.size() - union.size();
//		System.out.println("interesection: "+intersection);
		System.out.println("union size: "+union.size());
		return (1.0 * intersection) / (1.0 *union.size());
	}
}
