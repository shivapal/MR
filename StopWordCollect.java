import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class StopWordCollect {
	
	public static void readFromFile(String filepath, ArrayList<String> stop) {
		
		try(BufferedReader br = new BufferedReader(new FileReader(filepath))){
			String curLine;
			
			
			while((curLine=br.readLine())!=null) {
				if(!stop.contains(curLine)) {
					System.out.println(curLine);
					stop.add(curLine);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		ArrayList<String> stop = new ArrayList<String>();
		ArrayList<String> fileps = new ArrayList<String>();
		fileps.add("C:/Users/thear/eclipse-workspace/MR/src/stop-words/stop-words-english1.txt");
		fileps.add("C:/Users/thear/eclipse-workspace/MR/src/stop-words/stop-words-english2.txt");
		fileps.add("C:/Users/thear/eclipse-workspace/MR/src/stop-words/stop-words-english3-google.txt");
		fileps.add("C:/Users/thear/eclipse-workspace/MR/src/stop-words/stop-words-english4.txt");
		fileps.add("C:/Users/thear/eclipse-workspace/MR/src/stop-words/stop-words-english5.txt");
		fileps.add("C:/Users/thear/eclipse-workspace/MR/src/stop-words/mywords.txt");
		File file = new File("stopWordOutput.txt");
		FileOutputStream fos = new FileOutputStream(file);
		PrintStream ps = new PrintStream(fos);
		System.setOut(ps);
		for(int i=0; i<fileps.size(); i++) {
			readFromFile(fileps.get(i), stop);
		}
		
	}

}
