package pa3;
/**
 * 
 * @author Riley Bird, Erin Elsbernd
 *
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyWikiRanker {
public static List<String> nodeList;	
public static List<String> top10Pages;	

	public static void main(String[] args)  throws IOException, InterruptedException {
//		 String[] topics = {"science", "theory","research"};
//		 WikiCrawler w = new WikiCrawler("/wiki/Science", topics, 100, "WikiScienceGraph.txt", true);
//		 System.out.println("Retrieving all the science...");
//		 w.crawl();
//
//		 PageRank pr = new PageRank("WikiScienceGraph.txt", 0.01);
//		 nodeList = new ArrayList<String>(pr.nodeList);
//		 for (String s: nodeList) {
//			 System.out.println("page: " + s+ " rank: " + pr.pageRankOf(s));
//		 }
//		 System.out.println("top 10 Science pages: " + pr.topKPageRank(10));
		 
//		 String[] topics = {"tennis", "grand slam"};
//		 WikiCrawler w = new WikiCrawler("/wiki/Science", topics, 100, "WikiScienceGraph.txt", true);
//		 w.crawl();

		 PageRank pr = new PageRank("MyWikiGraph epsilon0.005.txt", 0.01);
		 nodeList = new ArrayList<String>(pr.nodeList);
//		 for (String s: nodeList) {
//			 System.out.println("page: " + s+ " rank: " + pr.pageRankOf(s));
//		 }
		 System.out.println("top 10 Tennis pages: " + pr.topKPageRank(10));
		 System.out.println("topKIndegree e = 0.01: "+ pr.topKInDegree(10));
		 System.out.println("topKOutdegree e = 0.01: "+ pr.topKOutDegree(10));
		 System.out.println("Iterations e = 0.01: "+ pr.count);
	}

}
