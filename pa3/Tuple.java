package pa3;

public class Tuple implements Comparable<Object>{
	
	private final String element;
	private final double weight;
	private int time;
	
	public Tuple() {
			element = null;
			weight = 0.0;
			time = -1;
		}
	
	public Tuple(String element, double weight){
		this.element = element;
		this.weight = weight;
		time = -1;
	}
	

	public String getElement() {
		return element;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int t) {
		this.time = t;
	}
	
	@Override
	public int compareTo(Object o) {
		Tuple t = (Tuple) o;
		 if (this.weight != t.getWeight()) {  
		        return  Double.compare(t.getWeight(), this.weight);  
		    }  
		    else {  
		        return this.time - t.getTime();  
		    }  
		
	} 
	
	@Override
	public boolean equals(Object obj) {
		Tuple t = (Tuple) obj;
	    return this.element == t.getElement();
	}
	
	@Override
	public String toString() {
		return "<" + this.element + "," + this.weight + ">";
	}
}
