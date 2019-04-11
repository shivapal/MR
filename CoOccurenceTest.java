
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
	private static Stemmer stemmer;
	
	public static class TokenizerMapper extends Mapper<Object, Text, CCPair, IntWritable> {
		
		//init output key and val
		private Text word = new Text();
		

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			ArrayList<String> coWords = new ArrayList<String>();
			StringTokenizer itr = new StringTokenizer(value.toString()); 
			while (itr.hasMoreTokens()) {
				String s = itr.nextToken();
				String s1 = s.toLowerCase();
				int ss = s1.length();
				char[] s2 = s1.toCharArray();
				stemmer.add(s2,ss);
				stemmer.stem();
				String s3 = stemmer.toString();
				if(!stopwords.contains(s3)) {
					for(int i=0; i<topwords.size(); i++) {
						if(topwords.get(i).word==s3) {
							topwords.get(i).count++;
						}else {
							coWords.add(s3);
						}
					}
				}
			}
			for(int i=0; i< topwords.size(); i++) {
				if(topwords.get(i).count>0) {
					for(int j=0; j<coWords.size(); j++) {
						context.write(new CCPair(topwords.get(i).word,coWords.get(j)), new IntWritable(topwords.get(i).count));
					}
					topwords.get(i).count=0;
				}
			}
		}
	}

	public static class IntSumReducer extends Reducer<CCPair, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(CCPair key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key.toText(), result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "co occurence");
		job.setJarByClass(CoOccurenceTest.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(CCPair.class);
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
		
		//set up stemmer
		stemmer = new Stemmer();
		
		//determine how many top words there are
		int tl=0;
		URL path0 = CoOccurenceTest.class.getResource("top10Out.txt");
		File f0 = new File(path0.getFile());
		
		try(BufferedReader br0 = new BufferedReader(new FileReader(f0))){
			String curLine;
			while((curLine=br0.readLine())!=null && tl<10) {
				topwords.add(new WCPair(curLine,0));
				tl++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
