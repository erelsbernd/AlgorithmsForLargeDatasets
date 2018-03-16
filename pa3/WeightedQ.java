package pa3;
import java.util.HashSet;
import java.util.PriorityQueue;

public class WeightedQ{
	public static void main(String[] args) {

		WeightedQ Q = new WeightedQ();
		 long startTime = System.nanoTime();
		for(int i =0; i <= 1000; i++)
			Q.add(new Tuple(i+"", i));
		 long endTime = System.nanoTime();
		 long duration = (endTime - startTime) / 1000000;
		 System.out.println("Time: "+ duration);
//		Tuple t1 = Q.extract();
//		Tuple t2 = Q.extract();
//		Tuple t3 = Q.extract();
//		
//		System.out.println(t1.getTime());
//		System.out.println(t1);
//		System.out.println(t2.getTime());
//		System.out.println(t2);
//		System.out.println(t3.getTime());
//		System.out.println(t3);
	}

	private PriorityQueue<Tuple> Q = new PriorityQueue<Tuple>();
	private int time = 0;
	private HashSet<Tuple> contains = new HashSet<Tuple>();
	
	public void add(Tuple t) {
		if (!contains.contains(t)){
			t.setTime(time);
			time++;
			Q.add(t);
			contains.add(t);
		}
	}
	
	public Tuple extract() {
		Tuple t = Q.poll();
		contains.remove(t);
		return t;
	}

	public boolean isEmpty() {
		
		return Q.isEmpty();
	}

	public int size() {
		
		return Q.size();
	}
}
