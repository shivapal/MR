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

public class Top10Words {
	


	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<WCPair> words = new ArrayList <WCPair>();
		URL path = Top10Words.class.getResource("part-r-00000.txt");
		File f = new File(path.getFile());
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String curLine;
			String w="";
			String inter="";
			int c=0;
			while ((curLine = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(curLine);
				w=st.nextToken();
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
		

		for(int i=0; i<words.size(); i++) {
			System.out.println(words.get(i).toString());
		}
	}
}
