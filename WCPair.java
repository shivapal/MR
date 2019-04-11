
public class WCPair {
	public String word;
	public int count;
	
	public WCPair(String word, int count) {
		this.word=word;
		this.count=count;
	}
	
	public String toString() {
		return word + " " +Integer.toString(count);
		
	}
}