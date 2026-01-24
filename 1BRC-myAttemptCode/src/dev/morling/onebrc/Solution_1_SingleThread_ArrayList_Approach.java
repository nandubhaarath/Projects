package dev.morling.onebrc;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Solution_1_SingleThread_ArrayList_Approach
{

//    private static final String FILE = "weather_stations_small.csv";
    private static final String FILE = "weather_stations.csv";

    public static void main(String[] args) throws IOException {

        long startTime = System.nanoTime();

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Double> min = new ArrayList<>();
        ArrayList<Double> max = new ArrayList<>();
        ArrayList<Double> sum = new ArrayList<>();
        ArrayList<Integer> count = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Path.of(FILE))) {
            String line;

            while ((line = br.readLine()) != null) {

                int sep = line.indexOf(';');
                String name = line.substring(0, sep);
                double temp = Double.parseDouble(line.substring(sep + 1));

                int index = findIndex(names, name);

                if (index == -1) {
                    // new city
                    names.add(name);
                    min.add(temp);
                    max.add(temp);
                    sum.add(temp);
                    count.add(1);
                }
                else {
                    // update stats
                    if (temp < min.get(index)) {
                        min.set(index, temp);
                    }

                    if (temp > max.get(index)) {
                        max.set(index, temp);
                    }

                    sum.set(index, sum.get(index) + temp);
                    count.set(index, count.get(index) + 1);
                }
            }
        }

        // Sorted output
        Map<String, String> output = new TreeMap<>();
        for (int i = 0; i < names.size(); i++) {
            double mean = sum.get(i) / count.get(i);
            output.put(
                    names.get(i),
                    round1(min.get(i)) + "/" + round1(mean) + "/" + round1(max.get(i))
            );
        }

        long endTime = System.nanoTime();
        double seconds = (endTime - startTime) / 1_000_000_000.0;

        System.out.println(output);
        System.out.printf("Time taken: %.3f s%n", seconds);
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
