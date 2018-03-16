package pa3;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WikiCrawler {

	public static void main(String[] args) throws IOException, InterruptedException {

		 long startTime = System.nanoTime();
		 String[] topics = {"wiki"};
		 WikiCrawler w = new WikiCrawler("/wiki/Seed4.html", topics, 10, "complete.txt", true);
		 w.crawl();
		 long endTime = System.nanoTime();
		 long duration = (endTime - startTime) / 1000000;
		 System.out.println("Time: "+ duration);
		// String s = "/wiki/tennis </a>2 1 2 3 4 7 8 9 10 11 12 13 14 15 16 17 18 19 20
		// 21 22 23 24 25 ball";
		// String[] terms = {"ball"};
		// Tuple weights = setWeights(s, "/wiki/tennis", terms, true);
		//
		// System.out.println(weights);
	}

	private static final String BASE_URL = "https://en.wikipedia.org";
	private String seedURL;
	private String[] keywords;
	private int max;
	private String fileName;
	private boolean isWeighted;

	/**
	 * 
	 * @param seedURL
	 * @param max
	 * @param keywords
	 * @param fileName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public WikiCrawler(String seedURL, String[] keywords, int max, String fileName, boolean isWeighted)
			throws IOException, InterruptedException {
		this.seedURL = seedURL;
		this.keywords = keywords;
		this.max = max;
		this.fileName = fileName;
		this.isWeighted = isWeighted;

	}

	/**
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void crawl() throws IOException, InterruptedException {
		Set<String> disallowed = getDisallowed();
		Set<String> vertices = new HashSet<String>();
		Set<String> edges = new HashSet<String>();
		int requests = 1;

		PrintWriter print = new PrintWriter(fileName);
		WeightedQ Q = new WeightedQ();
		print.println(this.max + "");
		HashSet<String> visited = new HashSet<>();

		visited.add(seedURL);
		Q.add(new Tuple(seedURL, 0));
		vertices.add(seedURL);

		while (!Q.isEmpty()) {
			try {
				String currentPage = Q.extract().getElement();
				if (!disallowed.contains(currentPage)) {
					URL url = new URL(BASE_URL + currentPage);
					InputStream is = url.openStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(is));

					String sCurrentLine;
					StringBuilder build = new StringBuilder();

					while ((sCurrentLine = br.readLine()) != null) {
						build.append(sCurrentLine);
					}

					String extracted = build.toString();

					is.close();
					br.close();

					ArrayList<String> links = extractLinks(extracted);

					for (String u : links) {
						// not to crawl: robots.txt
						Tuple t = new Tuple();

						if (!edges.contains(currentPage + " " + u) && !currentPage.equals(u)) {
							if (vertices.size() < this.max && !vertices.contains(u))
								vertices.add(u);

							if (vertices.contains(u)) {
								print.println(currentPage + " " + u);
								edges.add(currentPage + " " + u);
								t = setWeights(extracted, u, keywords, isWeighted);
							}
						}
						if (!visited.contains(u)) {

							if (vertices.contains(u)) {
								Q.add(t);
								visited.add(u);
							}

						}

					}
					requests++;
					if (requests % 10 == 0) {
						TimeUnit.SECONDS.sleep(1);
					}
				}
			} catch (Exception e) {

			}

		}
		print.close();
	}

	private static HashSet<String> getDisallowed() throws IOException {
		HashSet<String> disallowed = new HashSet<String>();
		URL url = new URL(BASE_URL + "/robots.txt");
		InputStream is = url.openStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String sCurrentLine;
		boolean start = false;
		while ((sCurrentLine = br.readLine()) != null) {
			if (sCurrentLine.contains("User-agent: *")) {
				start = true;
			}
			if (start == true && sCurrentLine.contains("Disallow:")) {
				disallowed.add(sCurrentLine.substring(9, sCurrentLine.length()).trim());
			}
		}

		is.close();
		br.close();

		return disallowed;
	}

	private static Tuple setWeights(String docs, String link, String[] topics, boolean isWeighted) {

		if (isWeighted == true) {
			ArrayList<String> topiclist = new ArrayList<String>(Arrays.asList(topics));

			double weight = 0;
			for (String topic : topiclist) {
				if (link.toLowerCase().contains(topic)) {
					weight = 1;
					return new Tuple(link, weight);

				}
			}

			int lastSeenTopic = Integer.MAX_VALUE / 2;
			int lastSeenLink = Integer.MAX_VALUE;
			int minDist = Integer.MAX_VALUE;
			int i = 0;
			int anchor = 0;
			Scanner scan = new Scanner(docs);
			while (scan.hasNext()) {
				String term = scan.next();
				if (term.contains("<a"))
					anchor = 1;
				if (term.contains("/a>"))
					anchor = 0;
				if (term.toLowerCase().contains(link.toLowerCase())) {
					lastSeenLink = i;
				} else {
					for (String topic : topics) {
						if (term.toLowerCase().contains(topic.toLowerCase()))
							lastSeenTopic = i;
					}
				}
				int dist = Math.abs(lastSeenTopic - lastSeenLink);
				if (dist == 0) {
					scan.close();
					return new Tuple(link, 1);
				}
				if (dist <= 20) {
					minDist = Math.min(dist, minDist);
					weight = 1.0 * 1 / (minDist + 2);
				}
				if (anchor == 0)
					i++;
			}
			scan.close();
			return new Tuple(link, weight);

		} else {
			return new Tuple(link, 0);
		}
	}

	/**
	 * 
	 * @param doc
	 * @return
	 */
	public static ArrayList<String> extractLinks(String doc) {

		ArrayList<String> links = new ArrayList<String>();
		Scanner scan = new Scanner(doc);
		String toAdd = "";
		boolean foundTag = false;

		while (scan.hasNext()) {

			String link = "";
			toAdd = scan.next();

			int startindex = toAdd.indexOf("\"");
			int endindex = toAdd.indexOf("\"", startindex + 1);
			if (startindex != -1 && endindex != -1)
				link = toAdd.substring(startindex + 1, endindex);
			if (toAdd.contains("<p>") || toAdd.contains("<P>"))
				foundTag = true;

			if (validLink(link) && foundTag)
				links.add(link);

		}
		scan.close();

		return links;
	}

	/**
	 * 
	 * @param link
	 * @return
	 */
	private static boolean validLink(String link) {

		if (link.startsWith("/wiki/")) {
			String sub = (String) link.subSequence(6, link.length());
			if (!(sub.contains("#") || (sub.contains(":"))))
				return true;

		}

		return false;

	}

}
