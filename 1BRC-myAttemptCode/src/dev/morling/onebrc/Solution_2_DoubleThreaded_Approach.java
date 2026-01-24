package dev.morling.onebrc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Solution_2_DoubleThreaded_Approach {

//    private static final String FILE = "weather_stations_small.csv";
    private static final String FILE = "weather_stations.csv";


    // ---------- Data holder (same idea as Solution 1 but packaged) ----------
    static class PartialResult {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Double> min = new ArrayList<>();
        ArrayList<Double> max = new ArrayList<>();
        ArrayList<Double> sum = new ArrayList<>();
        ArrayList<Integer> count = new ArrayList<>();
    }

    public static void main(String[] args) throws Exception {

        // 1) Count lines (simple learning approach)
        int totalLines = countLines(FILE);
        int mid = totalLines / 2;

        long startTime = System.nanoTime();

        // 2) Two threads each process a range of lines
        Thread t1;
        Thread t2;

        final PartialResult[] r1Holder = new PartialResult[1];
        final PartialResult[] r2Holder = new PartialResult[1];

        t1 = new Thread(() -> {
            try {
                r1Holder[0] = processLineRange(FILE, 0, mid);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "worker-1");

        t2 = new Thread(() -> {
            try {
                r2Holder[0] = processLineRange(FILE, mid, totalLines);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "worker-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // 3) Merge results (single-threaded merge at the end)
        PartialResult merged = merge(r1Holder[0], r2Holder[0]);

        // 4) Format output sorted
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
        System.out.printf("Time taken (Solution 2 - two threads): %.3f s%n", seconds);
    }

    // ---------- Step A: Count lines ----------
    private static int countLines(String file) throws IOException {
        int lines = 0;
        try (BufferedReader br = Files.newBufferedReader(Path.of(file))) {
            while (br.readLine() != null) {
                lines++;
            }
        }
        return lines;
    }

    // ---------- Step B: Process only a line range [start, end) ----------
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

    // ---------- Step C: Merge two partial results ----------
    private static PartialResult merge(PartialResult a, PartialResult b) {
        PartialResult merged = new PartialResult();

        // Start with all from a
        for (int i = 0; i < a.names.size(); i++) {
            merged.names.add(a.names.get(i));
            merged.min.add(a.min.get(i));
            merged.max.add(a.max.get(i));
            merged.sum.add(a.sum.get(i));
            merged.count.add(a.count.get(i));
        }

        // Fold in b
        for (int i = 0; i < b.names.size(); i++) {
            String name = b.names.get(i);
            int idx = findIndex(merged.names, name);

            if (idx == -1) {
                merged.names.add(name);
                merged.min.add(b.min.get(i));
                merged.max.add(b.max.get(i));
                merged.sum.add(b.sum.get(i));
                merged.count.add(b.count.get(i));
            }
            else {
                // merge stats
                if (b.min.get(i) < merged.min.get(idx)) {
                    merged.min.set(idx, b.min.get(i));
                }

                if (b.max.get(i) > merged.max.get(idx)) {
                    merged.max.set(idx, b.max.get(i));
                }

                merged.sum.set(idx, merged.sum.get(idx) + b.sum.get(i));
                merged.count.set(idx, merged.count.get(idx) + b.count.get(i));
            }
        }

        return merged;
    }

    // ---------- Utility ----------
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
