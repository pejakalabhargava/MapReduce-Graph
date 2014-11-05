package com.application.hadoop.graph;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MapReduceDegreeRunner {
	public static void main(String[] args) {
		try {
			Configuration conf = new Configuration();

			Job job = new Job(conf, "DegreeCount");
			job.setJarByClass(MapReduceDegreeRunner.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(LongWritable.class);

			job.setMapperClass(DegreeMapper.class);
			job.setCombinerClass(DegreeReducer.class);
			job.setReducerClass(DegreeReducer.class);

			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			
			FileInputFormat.addInputPath(job, new Path(args[0]));
			Path outputPath = new Path(args[1]);
	        FileSystem  fs = FileSystem.get(new URI(outputPath.toString()), conf);
	        fs.delete(outputPath);
			FileOutputFormat.setOutputPath(job, new Path(args[1]));
			job.waitForCompletion(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
