package com.application.hadoop.graph;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * 
 * @author bkakran
 * 
 */
public class DegreeReducer extends
		Reducer<Text, LongWritable, Text, LongWritable> {

	public void reduce(Text key, Iterable<LongWritable> values, Context context)
			throws IOException, InterruptedException {
		long sum = 0;
		for (LongWritable val : values) {
			sum += val.get();
		}
		//while (values.hasNext()) {
		//	sum += values.next().get();
		//}
		context.write(key, new LongWritable(sum));
	}
}
