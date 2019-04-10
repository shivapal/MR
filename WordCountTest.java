
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

public class WordCountTest {
	private static ArrayList<String> stopwords;
	private static Stemmer stemmer;
	
	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
		
		//init output key and val
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
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
					word.set(s3);
					context.write(word, one);
				}
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
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(WordCountTest.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		//set up stopwords
		stopwords = new ArrayList<String>();
		URL path = WordCountTest.class.getResource("stopWordOutput.txt");
		File f = new File(path.getFile());
		/*BufferedReader br = new BufferedReader(new FileReader("src/stopWordOutput.txt"))*/
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
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
