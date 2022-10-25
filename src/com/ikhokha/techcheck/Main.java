package com.ikhokha.techcheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Main {

    /**
     * This method will scan files and analyze each file with each separate thread parallel.
     *
     */
    private static void concurrentExecution(){
        ExecutorService executor = null;
        try {
            File docPath = new File("docs");
            File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));

            if (commentFiles != null && commentFiles.length > 0) {
                int maxThread = Math.min(commentFiles.length, 25);// we can make this 25 as configurable param form property file
                executor = Executors.newFixedThreadPool(maxThread);

                List<Future<Map<String, Integer>>> fc = new ArrayList<>();
                for (File file : commentFiles) {
                    CommentAnalyzer worker = new CommentAnalyzer(file);
                    fc.add(executor.submit(worker));
                }

                Map<String, Integer> finalResultMap = new HashMap<>();
                for (Future<Map<String, Integer>> future : fc) {
                    Map<String, Integer> threadMap = future.get();
                    threadMap.forEach((key, value) -> finalResultMap.merge(key, value, Integer::sum));
                }
                executor.shutdown();
                System.out.println(finalResultMap);
            }
        } catch (InterruptedException | ExecutionException ie) {
            ie.printStackTrace();
        } finally {
            if (executor != null && !executor.isShutdown()) {
                executor.shutdown();
            }
        }
    }

    public static void main(String[] args)  {
        concurrentExecution();
        //sequenceExecution();
    }

    /**
     * This method is going to analyze file in sequential order.
     */
    private static void sequenceExecution() {
        Map<String, Integer> totalResults = new HashMap<>();
        File docPath = new File("docs");
        File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));

        if (commentFiles != null) {
            for (File commentFile : commentFiles) {

                CommentAnalyzer commentAnalyzer = new CommentAnalyzer(commentFile);
                Map<String, Integer> fileResults = commentAnalyzer.analyze();
                System.out.println(fileResults);
                addReportResults(fileResults, totalResults);

            }
            totalResults.forEach((k, v) -> System.out.println(k + " : " + v));
        }
    }

    /**
     * This method adds the result counts from a source map to the target map
     *
     * @param source the source map
     * @param target the target map
     */
    private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {

        for (Map.Entry<String, Integer> entry : source.entrySet()) {
            target.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

    }

}
