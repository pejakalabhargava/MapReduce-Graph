package com.application.hadoop.graph;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * 
 * @author bkakran
 * 
 */
public class DegreeMapper extends
		Mapper<LongWritable, Text, Text, LongWritable> {
	private final LongWritable one = new LongWritable(1);
	private Text word = new Text();
	private static String DELIMETER = " ";

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line, DELIMETER);
		while (tokenizer.hasMoreTokens()) {
			word.set(tokenizer.nextToken());
			context.write(word, one);
		}
	}
}
