package com.application.hadoop.graph;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MapReduceFrquencyRunner extends Configured implements Tool {
	public static void main(String[] args) throws Exception {
		System.out.println(args.length);
		if (args.length != 3) {
			System.out
					.println("Usage: MapReduceFrquencyRunner <input dir> <output dir> \n");
			System.exit(-1);
		}
		System.out.println(Arrays.toString(args));
		try {
			Configuration conf = new Configuration();

			Job job = new Job(conf, "DegreeCount");
			job.setJarByClass(MapReduceFrquencyRunner.class);
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
		try {
			Configuration conf = new Configuration();

			Job job = new Job(conf, "FrequencyCount");
			job.setJarByClass(MapReduceFrquencyRunner.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(LongWritable.class);

			job.setMapperClass(FrequencyMapper.class);
			job.setCombinerClass(FrequencyReducer.class);
			job.setReducerClass(FrequencyReducer.class);

			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			
			FileInputFormat.addInputPath(job, new Path(args[1]));
			FileOutputFormat.setOutputPath(job, new Path(args[2]));
			job.waitForCompletion(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//int res = ToolRunner.run(new Configuration(),new MapReduceFrquencyRunner(), args);
		//System.exit(res);
	}

	public int run(String[] paramArrayOfString) throws Exception {
		System.out.println(Arrays.toString(paramArrayOfString));
		String intermediateFileDir = "tmp";
		JobControl control = new JobControl("ChainMapReduce");
		ControlledJob step1 = new ControlledJob(degreeJob(paramArrayOfString[0], intermediateFileDir), null);
		ControlledJob step2 = new ControlledJob(frequencyJob(intermediateFileDir, paramArrayOfString[1]),Arrays.asList(step1));
		control.addJob(step1);
		control.addJob(step2);
		Thread workFlowThread = new Thread(control, "workflowthread");
		workFlowThread.setDaemon(true);
		workFlowThread.start();
		return 0;
	}

	private Job frequencyJob(String inputPath, String outputPath) throws IOException, ClassNotFoundException, InterruptedException {
		Job job = new Job();
		job.setJarByClass(MapReduceFrquencyRunner.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		job.setMapperClass(FrequencyMapper.class);
		job.setReducerClass(FrequencyReducer.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		job.waitForCompletion(true);

		return job;
	}

	private Job degreeJob(String inputPath, String outputPath) throws IOException, ClassNotFoundException, InterruptedException {
		Job job = new Job();
		job.setJarByClass(MapReduceFrquencyRunner.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		job.setMapperClass(DegreeMapper.class);
		job.setReducerClass(DegreeReducer.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(inputPath));
		FileOutputFormat.setOutputPath(job, new Path(outputPath));

		job.waitForCompletion(true);

		return job;
	}
}