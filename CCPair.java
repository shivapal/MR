import org.apache.hadoop.io.Text;

public class CCPair {
	public Text  mWord;
	public Text cWord;
	
	public CCPair(String mWord, String cWord) {
		this.mWord=new Text(mWord);
		this.cWord=new Text(cWord);
	}
	
	public Text toText() {
		return new Text(mWord + " " + cWord);	
	}
}
