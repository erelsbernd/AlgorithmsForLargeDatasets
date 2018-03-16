package pa3;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;
/**
 * 
 * @author Riley Bird, Erin Elsbernd
 *
 */
public class PageRank {
private HashMap<String, HashSet<String>> outList;
private HashMap<String, HashSet<String>> inList;
private HashMap<String, Integer> outSize;
private HashMap<String, Integer> inSize;
public double numVertices;
public int numEdges;
private double beta;
private double epsilon;
public static List<String> nodeList;
private HashMap<Integer, Double> map;
private double[] arr;
private ArrayList<Integer> rankList;
public int count;




public static void main(String[] args) throws IOException {
	long startTime = System.nanoTime();
	//wikiTennis.txt
	String filename = "complete.txt";
	PageRank pr = new PageRank(filename, 0.01);
	long endTime = System.nanoTime();
	long duration = (endTime - startTime) / 1000000;
	System.out.println("Time: "+ duration);
//	System.out.println("inDegreeOf /wiki/Node_6.html: "+ pr.inDegreeOf("/wiki/Node_6.html"));
//	System.out.println("inDegreeOf /wiki/Node_87.html: "+ pr.inDegreeOf("/wiki/Node_87.html"));
//	System.out.println("inDegreeOf /wiki/Node_26.html: "+ pr.inDegreeOf("/wiki/Node_26.html"));
//	System.out.println("inDegreeOf D: "+ pr.inDegreeOf("D"));
//	System.out.println("inDegreeOf E: "+ pr.inDegreeOf("E"));
//	System.out.println("inDegreeOf F: "+ pr.inDegreeOf("F"));
//	System.out.println("inDegreeOf G: "+ pr.inDegreeOf("G"));
//	System.out.println("inDegreeOf H: "+ pr.inDegreeOf("H"));
//	System.out.println("outDegreeOf /wiki/Node_6.html: "+ pr.outDegreeOf("/wiki/Node_6.html"));
//	System.out.println("outDegreeOf /wiki/Node_87.html: "+ pr.outDegreeOf("/wiki/Node_87.html"));
//	System.out.println("outDegreeOf /wiki/Node_26.html: "+ pr.outDegreeOf("/wiki/Node_26.html"));
//	System.out.println("outDegreeOf D: "+ pr.outDegreeOf("D"));
//	System.out.println("outDegreeOf E: "+ pr.outDegreeOf("E"));
//	System.out.println("outDegreeOf F: "+ pr.outDegreeOf("F"));
//	System.out.println("outDegreeOf G: "+ pr.outDegreeOf("G"));
//	System.out.println("outDegreeOf H: "+ pr.outDegreeOf("H"));
//	System.out.println("pageRankOf /wiki/Node_6.html: "+ pr.pageRankOf("/wiki/Node_6.html"));
//	System.out.println("pageRankOf /wiki/Node_87.html: "+ pr.pageRankOf("/wiki/Node_87.html"));
//	System.out.println("pageRankOf /wiki/Node_26.html: "+ pr.pageRankOf("/wiki/Node_26.html"));
//	System.out.println("pageRankOf D: "+ pr.pageRankOf("D"));
//	System.out.println("pageRankOf E: "+ pr.pageRankOf("E"));
//	System.out.println("pageRankOf F: "+ pr.pageRankOf("F"));
//	System.out.println("pageRankOf G: "+ pr.pageRankOf("G"));
//	System.out.println("pageRankOf H: "+ pr.pageRankOf("H"));
//	System.out.println("topKIndegree: "+ pr.topKInDegree(5));
//	System.out.println("topKOutdegree: "+ pr.topKOutDegree(5));
//	System.out.println("topKRank: "+ pr.topKPageRank(5));
//    System.out.println("edges: "+ pr.numEdges());
    System.out.println("Iterations e = 0.01: "+ pr.count);
}

	public PageRank(String fileName, double epsilon) {
		beta = 0.85;
		this.epsilon = epsilon;
		
		File file = new File(fileName);
		outList = new HashMap<String, HashSet<String>>();
		inList = new HashMap<String, HashSet<String>>();
		numEdges = 0;
		LinkedHashSet<String> nodes = new LinkedHashSet<String>();
		HashMap<String, Integer> out = new HashMap<String, Integer>((int)numVertices);
		HashMap<String, Integer> in = new HashMap<String, Integer>((int)numVertices);
		
		
	    try {

	        Scanner sc = new Scanner(file);
	        numVertices = Double.valueOf((double)sc.nextInt());
	        System.out.println("num vertices: "+ numVertices);
	        if (numVertices == 0) {
	        	sc.close();
	        	return;
	        }

	        while (sc.hasNext()) {
	            String a = sc.next();
	            String b = sc.next();
	            if (!outList.containsKey(a)) {
	            	HashSet<String> set = new HashSet<String>();
	            	set.add(b);
	            	outList.put(a, set);
	            	out.put(a, 1);
	            } else {
	            	HashSet<String> set1 = new HashSet<String>(outList.get(a));
		            set1.add(b);
		            outList.put(a, set1);
		            int val = out.get(a);
		            out.put(a, ++val);
		            //System.out.println("adding b " +b+ " to outlist of a " + a);
	            }
	            
	            if (!outList.containsKey(b)) {
	            	outList.put(b, new HashSet<String>());
	            	out.put(b,0);
	            }
	            
	            
	            if (!inList.containsKey(b)) {
	            	HashSet<String> set = new HashSet<String>();
	            	set.add(a);
	            	inList.put(b, set);
	            	in.put(b, 1);
	            	//System.out.println("adding a " +a+ " to inlist of b " + b);
	            } else {
	            	HashSet<String> set2 = new HashSet<String>(inList.get(b));
		            set2.add(a);
		            inList.put(b, set2);
		            int val = in.get(b);
		            in.put(b, ++val);
		            //System.out.println("adding a " +a+ " to inlist of b " + b);
	            }
	            if (!inList.containsKey(a)) {
	            	inList.put(a, new HashSet<String>());
	            	in.put(a, 0);
	            }
	            nodes.add(a);
	            nodes.add(b);
	            numEdges++;
	            
	        }
	        sc.close();
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		nodeList = new ArrayList<String>(nodes);
//		for (int i = 0; i < nodeList.size(); i++) {
//		System.out.println("node: " + nodeList.get(i));
//	}
		if (numVertices != nodeList.size()) {
			System.out.println("nodeList size and numVertices don't match!");
		}
//		for (int i = 0; i < nodeList.size(); i++){
//        	String node = nodeList.get(i);
//        	System.out.println("node: " + node);
//        	HashSet<String> set = new HashSet<String>(inList.get(node));
//    		Iterator<String> setItr = set.iterator();
//    		while(setItr.hasNext())  {
//    			System.out.println("node in set: " + setItr.next());
//    		}
//		}
		arr = computePageRank();
//		for (int i = 0; i < arr.length; i++) {
//			System.out.println("arr i: " + arr[i]);
//		}
		HashMap<Integer, Double> rank = new HashMap<Integer, Double>(arr.length);
		for (int i = 0; i < arr.length; i++) {
			rank.put(i, arr[i]);
		}
		map = (HashMap<Integer, Double>) sortByValue(rank);
		rankList = new ArrayList<Integer>(map.keySet()); 
		outSize = (HashMap<String, Integer>) sortByValue(out);
		inSize = (HashMap<String, Integer>) sortByValue(in);
		
	}
	
	/**
	 *  gets name of vertex of the graph as parameter and returns
its page rank.
	 * @param graphEdge
	 * @return rank if it's a valid vertex, else -1
	 */
	public int pageRankOf(String graphEdge) {
		if (nodeList.contains(graphEdge)) {
			int index = nodeList.indexOf(graphEdge);
			return rankList.indexOf(index)+1;
		}
		return -1;
	}
	
	
	/**
	 *  gets name of vertex of the graph as parameter and returns
its out degree.
	 * @param graphEdge
	 * @return
	 */
	public int outDegreeOf(String graphEdge) {
		return outList.get(graphEdge).size();
	}
	
	/**
	 *  gets name of vertex of the graph as parameter and returns
its in degree.
	 * @param graphEdge
	 * @return
	 */
	public int inDegreeOf(String graphEdge) {
		return inList.get(graphEdge).size();
	}
	
	/**
	 * returns number of edges of the graph.
	 * @return
	 */
	public int numEdges() {
		return numEdges;
	}
	

	/**
	 *  gets an integer k as parameter and returns an array (of
strings) of pages with top k page ranks.
	 * @return
	 */
	public ArrayList<String> topKPageRank(int k) {
		if ((numVertices <= 0) || (k > numVertices)) return null;
		
		ArrayList<String> list = new ArrayList<String>(k);
		for (int i = 0; i < k; i++) {
			list.add(i, nodeList.get(rankList.get(i)));
		}
		
		return list;
	}
	
	/**
	 * gets an integer k as parameter and returns an array (of
strings) of pages with top k in degree.
	 * @param k
	 * @return
	 */
	public ArrayList<String> topKInDegree(int k) {
		if ((numVertices <= 0) || (k > numVertices) || inSize.isEmpty()) return null;
		
		ArrayList<String> list = new ArrayList<String>(k);
		int count = 0;
		for (HashMap.Entry<String, Integer> entry : inSize.entrySet()) {
			list.add(entry.getKey());
			if (count+1 == k) {
				break;
			}
			count++;
		}
		return list;
	}
	
	/**
	 * gets an integer k as parameter and returns an array (of
strings) of pages with top k out degree.
	 * @param k
	 * @return
	 */
	public ArrayList<String> topKOutDegree(int k) {
		if ((numVertices <= 0) || (k > numVertices) || outSize.isEmpty()) return null;
		
		ArrayList<String> list = new ArrayList<String>(k);
		int count = 0;
		for (HashMap.Entry<String, Integer> entry : outSize.entrySet()) {
			list.add(entry.getKey());
			if (count+1 == k) {
				break;
			}
			count++;
		}
		return list;
	}
	
	private double[] computePageRank() {
		//initialize p vector with numVertices+1 spots for 0 -> n p vectors
		double[] pn = new double[nodeList.size()];
		double initial = 1.0/numVertices;
		double[] pn1 = null;
		//p0 vector should be initialized to 1/numVertices
		for (int i = 0; i < numVertices; i++) {
			pn[i] = initial;
		}
			
		int n = 0;
		boolean converged = false;
		count = 0;
		while (!converged) {
			pn1 = randWalk(pn);
			if (computeNorm(pn1, pn) <= epsilon) {
				converged = true;
			}
			//make sure this copy is ok
			pn = pn1;
			n++;
			count++;
			
		}
//		for (int i = 0; i < pn.length; i++) {
//			System.out.println("node i: " +i+ " val: "+ pn[i]);
//		}
		return pn;
		
	}
	
	private double[] randWalk(double[] pn) {
//		System.out.println("enter randwalk: ");
		double[] pn1 = new double[nodeList.size()];
		double initial = ((1.0-beta)/(1.0*numVertices));
		Arrays.fill(pn1, initial);
        for (int i = 0; i < nodeList.size(); i++){
        	String node = nodeList.get(i);
//        	System.out.println("node: " + node);
        	//if Number of outgoing links in p is not equal to zero then iterate through the links
        	if (outList.get(node).size() > 0) {
        		HashSet<String> set = new HashSet<String>(outList.get(node));
        		Iterator<String> setItr = set.iterator();
//        		System.out.println("node: " + node + " set size: " + set.size());
        		while(setItr.hasNext())  {
        			double val = 0.0;
        			int qIndex = nodeList.indexOf(setItr.next());
//        			System.out.println("i index: " + i + " .q index: "+qIndex);
        			val = pn1[qIndex] + (beta * (pn[i] / (set.size()*1.0)));
//        			System.out.println("edges val: " + val);
        			pn1[qIndex] = val;
//        			for (int mm = 0; mm < pn1.length; mm++) {
//        				System.out.print(" " + pn1[mm]+" ");
//        			}
//        			System.out.println("\n");
        		}
        	// no outgoing edges so do some random shiz
        	} else {
        		for (int k = 0; k < numVertices; k++) {
    				double val = 0.0;
//        			System.out.println("i index: " + i + " .k index: "+k);
        			val = pn1[k] + (beta * (pn[i] / (numVertices*1.0)));
//        			System.out.println("no edges val: " + val);
        			pn1[k] = val;
        		}
        		
        	}	
            
        }
        return pn1;
		
	}
	
	private double computeNorm(double[] pn1, double[] pn) {
		double norm = 0;
		for (int i = 0; i < numVertices; i++) {
			norm += Math.abs(pn1[i] - pn[i]);
		}
//		System.out.println("norm: " + norm);
		return norm;
	}
	
	private static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
	    Comparator<K> valueComparator =  new Comparator<K>() {
	        public int compare(K k1, K k2) {
	            int compare = map.get(k2).compareTo(map.get(k1));
	            if (compare == 0) return 1;
	            else return compare;
	        }
	    };
	    Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
	    sortedByValues.putAll(map);
	    return sortedByValues;
	}
	
	
	private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
	
}
