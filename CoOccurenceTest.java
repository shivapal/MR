
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CoOccurenceTest {
	private static ArrayList<String> stopwords;
	private static ArrayList<WCPair> topwords;
	//private static Stemmer stemmer;
	
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		
		//init output key and val
		private Text word = new Text();
		private IntWritable result = new IntWritable(1);

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

			
			
			ArrayList<String> coWords = new ArrayList<String>(); //hold co-ocurring words
			String[] lines = value.toString().split("\n");
			for (int k = 0; k < lines.length; k++) {
				
				StringTokenizer itr = new StringTokenizer(value.toString());
				while (itr.hasMoreTokens()) {
					String s = itr.nextToken(); // get next word in tweet
					String s1 = s.toLowerCase();
					String s2 = s1.replaceAll("[^a-zA-Z]+", "");
					if (!stopwords.contains(s2)  && s2.length()>0) {
						for (int i = 0; i < topwords.size(); i++) { // for all words in top 10
							if (topwords.get(i).word.contentEquals(s2)) { // check if its a top 10 word
								topwords.get(i).count++;
							}
						}
						coWords.add(s2);

					}
					
				}
				
				for(int i=0; i< topwords.size(); i++) {
					if(topwords.get(i).count>0) { //for every occurence of a top 10 word, emit the co-occuring word pair with the count of the top 10 word
						for(int j=0; j<coWords.size(); j++) {
							if(!topwords.get(i).word.contentEquals(coWords.get(j))) {
								word.set(topwords.get(i).word + " " + coWords.get(j));
								result.set(topwords.get(i).count);
								context.write(word, result);
							}
						}
						topwords.get(i).count=0; //reset count
					}
				}
				coWords.clear();
			}
			
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}

			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "co occurence");
		job.setJarByClass(CoOccurenceTest.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		//set up stopwords
		stopwords = new ArrayList<String>();
		URL path = CoOccurenceTest.class.getResource("stopWordOutput.txt");
		File f = new File(path.getFile());

		try(BufferedReader br = new BufferedReader(new FileReader(f))){
			String curLine;
			while((curLine=br.readLine())!=null) {
				stopwords.add(curLine);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//determine how many top words there are
		int tl=0;
		topwords= new ArrayList<WCPair>();
		URL path0 = CoOccurenceTest.class.getResource("top10Out.txt");
		File f0 = new File(path0.getFile());
		
		try(BufferedReader br0 = new BufferedReader(new FileReader(f0))){
			String curLine;
			while((curLine=br0.readLine())!=null && tl<10) {
				StringTokenizer itr = new StringTokenizer(curLine, "\",\"");
				topwords.add(new WCPair(itr.nextToken(),0));
				tl++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
