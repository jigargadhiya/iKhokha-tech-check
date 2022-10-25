package com.ikhokha.techcheck;

import com.ikhokha.techcheck.util.Matrix;
import com.ikhokha.techcheck.util.Operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentAnalyzer implements Callable<Map<String, Integer>> {
	private final Logger logger = Logger.getLogger(CommentAnalyzer.class.getName());

	private final File file;
	
	public CommentAnalyzer(File file) {
		this.file = file;
	}

	@Override
	public Map<String, Integer> call() {
		logger.info("Current Thread"+Thread.currentThread().getName());
		return analyze();
	}

	
	public Map<String, Integer> analyze() {
		Map<String, Integer> resultsMap = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				checkAllMatrix(line,resultsMap);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.info("File not found:"+file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("IO Error processing file:"+file.getAbsolutePath());
		}
		return resultsMap;
	}

	/**
	 * Check all defined matrix for each comment.
	 */
	private void checkAllMatrix(String comment,Map<String, Integer> resultsMap){
		for(Matrix matrix:Matrix.values()){
			Operation operation =matrix.getOperation();
			switch (operation) {
				case LESS_THEN ->{
					if(comment.length() < Integer.parseInt(matrix.getValue())){
						incOccurrence(resultsMap, matrix.name());
					} ;
				}
				case CONTAINS -> {
					if(comment.contains(matrix.getValue())){
						incOccurrence(resultsMap, matrix.name());
					} ;
				}case REGEX -> {
					Pattern pattern = Pattern.compile(matrix.getValue(), Pattern.CASE_INSENSITIVE);
					Matcher matcher =pattern.matcher(comment);
					if(matcher.find()){
						incOccurrence(resultsMap, matrix.name());
					}
				}
				//We can add more cases from operation ENUM as increase.
				default -> {
					return;
				}
			}
		}
	}
	
	/**
	 * This method increments a counter by 1 for a match type on the countMap. Uninitialized keys will be set to 1
	 * @param countMap the map that keeps track of counts
	 * @param key the key for the value to increment
	 */
	private void incOccurrence(Map<String, Integer> countMap, String key) {
		countMap.merge(key, 1, Integer::sum);
	}

}
