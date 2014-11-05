package com.application.hadoop.graph;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 
 * @author bkakran
 * 
 */

public class FrequencyMapper extends
		Mapper<LongWritable, Text, Text, LongWritable> {
	private final LongWritable one = new LongWritable(1);
	private Text word = new Text();
	private static String DELIMETER = " ";

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);

		if (tokenizer.hasMoreTokens()) {
			String node = tokenizer.nextToken();
		}

		String degreeValue = null;
		if (tokenizer.hasMoreTokens()) {
			degreeValue = tokenizer.nextToken();
		}
		if (degreeValue != null) {
			word.set(degreeValue);
			context.write(word, one);
		}
	}
}