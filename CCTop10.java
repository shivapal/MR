import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class CCTop10 {
	


	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<WCPair> words = new ArrayList <WCPair>();
		URL path = CCTop10.class.getResource("part-r-00000.txt");
		File f = new File(path.getFile());
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String curLine;
			String w="";
			String w1="";
			String inter="";
			int c=0;
			while ((curLine = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(curLine);
				w=st.nextToken();
				w1=st.nextToken();
				w=w+" " +w1;
				inter=st.nextToken();
				c=Integer.parseInt(inter);
				words.add(new WCPair(w,c));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(words, new WCSort());
		
		
		File file = new File("top10Out.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setOut(ps);
		
		String prev="";
		int i=0;
		int j=0;
		while(i<words.size()&&j<10) {
			StringTokenizer st = new StringTokenizer(words.get(i).toString());
			String s1=st.nextToken();
			String s2=st.nextToken();
			String n = st.nextToken();
			if(!n.contentEquals(prev)) {
				System.out.println(s1+ " " + s2+ "," + n);
				j++;
			}
			prev=n;
			i++;
		}

	}
}
