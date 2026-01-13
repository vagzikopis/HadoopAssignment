package numeronym;

import java.io.IOException;
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
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.conf.Configured;

public class Numeronym extends Configured implements Tool {
    private final static int k = 10;

    // Map
    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable>{

        private final static IntWritable ONE = new IntWritable(1);
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            int len = line.length();
            if (len == 0) return;
            
            // Split on everything
            String[] words = line.split("\\W+");
            for (String w: words) {
                String token = w.replaceAll("[^a-zA-Z0-9]", "");
                token = token.toLowerCase();
                if (token.length() > 3) {
                    // Logic: Convert token to numeronym
                    String result = toNumeronym(token);

                    // Only write if we have a valid result (optional check)
                    if (!result.isEmpty()) {
                        word.set(result);
                        context.write(word, ONE);
                    }
                }
            }
        }

        // Helper function to convert to numeronyms
        private String toNumeronym(String input) {
            char first = input.charAt(0);
            char last = input.charAt(input.length() - 1);
            int middleCount = input.length() - 2;
            return first + String.valueOf(middleCount) + last;
        }
    }

    // Reduce
    public static class IntSumReducer
            extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            if (sum >= k) {
                result.set(sum);
                context.write(key, result);
            }
        }
    }

    // Run
    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();
        Job job = Job.getInstance(conf, "numeronym count");
        job.setJarByClass(Numeronym.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        
        // HDFS paths.
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        return job.waitForCompletion(true) ? 0 : 1;
    }

    // Main
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new Numeronym(), args);
        System.exit(res);
    }
}