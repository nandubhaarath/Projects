/**
 * Solution 3 – Multithreaded (N Threads, Line-Based Split)
 *
 * This implementation builds on the single-threaded baseline by introducing
 * parallel processing using multiple worker threads.
 *
 * The input file is first analysed to determine the total number of lines,
 * and the work is divided evenly across N threads, where N is the number of
 * available processor cores.
 *
 * Each thread processes a specific range of lines from the file and maintains
 * its own local aggregation of statistics (min, max, sum, count) per city.
 *
 * Once all threads complete, the main thread waits for them using Thread.join
 * and then merges the partial results into a single final result.
 *
 * This approach avoids shared mutable state during parallel execution and
 * ensures correctness by performing the merge step sequentially.
 */

package dev.morling.onebrc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Solution3_UseAllAvailableThreads_Approach
{

    private static final String FILE = "weather_stations.csv";

    static class PartialResult {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Double> min = new ArrayList<>();
        ArrayList<Double> max = new ArrayList<>();
        ArrayList<Double> sum = new ArrayList<>();
        ArrayList<Integer> count = new ArrayList<>();
    }

    public static void main(String[] args) throws Exception {

        int workers = Runtime.getRuntime().availableProcessors();
        System.out.println("Workers: " + workers);

        int totalLines = countLines(FILE);
        int linesPerWorker = totalLines / workers;

        long startTime = System.nanoTime();

        Thread[] threads = new Thread[workers];
        PartialResult[] results = new PartialResult[workers];

        for (int i = 0; i < workers; i++) {

            int startLine = i * linesPerWorker;

            int endLine;
            if (i == workers - 1) {
                endLine = totalLines; // last thread takes the remainder
            }
            else {
                endLine = (i + 1) * linesPerWorker;
            }

            final int index = i;
            final int s = startLine;
            final int e = endLine;

            threads[i] = new Thread(() -> {
                try {
                    results[index] = processLineRange(FILE, s, e);
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }, "worker-" + (i + 1));

            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        PartialResult merged = mergeAll(results);

        Map<String, String> output = new TreeMap<>();
        for (int i = 0; i < merged.names.size(); i++) {
            double mean = merged.sum.get(i) / merged.count.get(i);
            output.put(
                    merged.names.get(i),
                    round1(merged.min.get(i)) + "/" + round1(mean) + "/" + round1(merged.max.get(i))
            );
        }

        long endTime = System.nanoTime();
        double seconds = (endTime - startTime) / 1_000_000_000.0;

        System.out.println(output);
        System.out.printf("Time taken (Solution 3 - %d threads): %.3f s%n", workers, seconds);
    }

    private static int countLines(String file) throws IOException {
        int lines = 0;
        try (BufferedReader br = Files.newBufferedReader(Path.of(file))) {
            while (br.readLine() != null) {
                lines++;
            }
        }
        return lines;
    }

    private static PartialResult processLineRange(String file, int startLine, int endLine) throws IOException {

        PartialResult r = new PartialResult();

        try (BufferedReader br = Files.newBufferedReader(Path.of(file))) {
            String line;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {

                if (lineNo < startLine) {
                    lineNo++;
                    continue;
                }

                if (lineNo >= endLine) {
                    break;
                }

                int sep = line.indexOf(';');
                String name = line.substring(0, sep);
                double temp = Double.parseDouble(line.substring(sep + 1));

                int index = findIndex(r.names, name);

                if (index == -1) {
                    r.names.add(name);
                    r.min.add(temp);
                    r.max.add(temp);
                    r.sum.add(temp);
                    r.count.add(1);
                }
                else {
                    if (temp < r.min.get(index)) {
                        r.min.set(index, temp);
                    }
                    if (temp > r.max.get(index)) {
                        r.max.set(index, temp);
                    }
                    r.sum.set(index, r.sum.get(index) + temp);
                    r.count.set(index, r.count.get(index) + 1);
                }

                lineNo++;
            }
        }

        return r;
    }

    private static PartialResult mergeAll(PartialResult[] parts) {

        PartialResult merged = new PartialResult();

        for (PartialResult p : parts) {
            if (p == null) {
                continue;
            }

            for (int i = 0; i < p.names.size(); i++) {

                String name = p.names.get(i);
                int idx = findIndex(merged.names, name);

                if (idx == -1) {
                    merged.names.add(name);
                    merged.min.add(p.min.get(i));
                    merged.max.add(p.max.get(i));
                    merged.sum.add(p.sum.get(i));
                    merged.count.add(p.count.get(i));
                }
                else {
                    if (p.min.get(i) < merged.min.get(idx)) {
                        merged.min.set(idx, p.min.get(i));
                    }

                    if (p.max.get(i) > merged.max.get(idx)) {
                        merged.max.set(idx, p.max.get(i));
                    }

                    merged.sum.set(idx, merged.sum.get(idx) + p.sum.get(i));
                    merged.count.set(idx, merged.count.get(idx) + p.count.get(i));
                }
            }
        }

        return merged;
    }

    private static int findIndex(ArrayList<String> names, String name) {
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}

