import java.util.Comparator;

public class WCSort implements Comparator<WCPair> {

	@Override
	public int compare(WCPair a, WCPair b) {
		// TODO Auto-generated method stub
		return b.count - a.count;
	}

}
