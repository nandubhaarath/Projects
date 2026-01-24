/*
================================================================================
Solution 6 – Worker-Local HashMap Index (Eliminating O(k) Lookup in Hot Path)
================================================================================

Context
-------
In Solution 5, the merge phase was optimised using a HashMap, reducing the
overall complexity of merging partial results from O(n²) to O(n).

However, inside each worker thread, city lookup was still implemented as:

    findIndex(r.names, name)

This performs a linear scan over the worker’s local city list.
As the number of unique cities per worker grows, this becomes:

    O(k) per line   (k = unique cities in that worker)

This means the worker hot path was still algorithmically inefficient.


Key Improvement in Solution 6
-----------------------------
Each worker now maintains its own:

    HashMap<String, Integer> localIndexByName

This maps:
    city name -> index in that worker’s PartialResult arrays

So city lookup becomes:

    O(1) average case

instead of:
    O(k)


Why This Matters (Performance Theory)
-------------------------------------
This change removes a hidden "inner loop inside the inner loop".

Before (Solution 5):
    For every line:
        scan list of cities to find index  → O(k)

Now (Solution 6):
    For every line:
        hash lookup → O(1)

This reduces the worker phase from:
    O(totalLines × uniqueCitiesPerWorker)

to:
    O(totalLines)


Key Learning Outcome
--------------------
Multithreading alone is not enough.

Even with perfect parallelism, if the hot path is algorithmically poor,
performance will still collapse at scale.

This solution demonstrates a core systems principle:

    "Fix algorithmic complexity before micro-optimising."

We applied exactly the same optimisation pattern twice:
    1. Merge phase (Solution 5)
    2. Worker hot path (Solution 6)


What Is Still Slow After This
-----------------------------
After Solution 6, the dominant cost is now:

• Per-line String allocation
• substring()
• Double.parseDouble()
• Byte-by-byte file reading (RandomAccessFile)

These are *CPU and allocation heavy*, not algorithmic.

This sets up the next step:
    → Solution 7: Memory-mapped file + manual parsing
      (no Strings, no substrings, no Double.parseDouble)


Mental Model (Very Important)
-----------------------------
Solution progression so far:

1. Solution 1: Single thread, naive everything
2. Solution 2: Two threads
3. Solution 3: All threads
4. Solution 4: Byte range splitting
5. Solution 5: O(1) merge using HashMap
6. Solution 6: O(1) worker lookup using HashMap   ← YOU ARE HERE

Each step removed:
    either a scalability wall
    or an algorithmic bottleneck

Only after this do low-level tricks (Unsafe, mmap, bit parsing)
actually make sense.

================================================================================
*/

package dev.morling.onebrc;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Solution6_AllThreads_WorkerLocalIndexHashMap_Approach {

    private static final String FILE = "weather_stations.csv";

    static class PartialResult {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Double> min = new ArrayList<>();
        ArrayList<Double> max = new ArrayList<>();
        ArrayList<Double> sum = new ArrayList<>();
        ArrayList<Integer> count = new ArrayList<>();
    }

    static class Range {
        long start;
        long end; // inclusive end boundary
        Range(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

    // -------- TRACE DATA PER THREAD --------
    static class ThreadTrace {
        String threadName;
        long rangeStart;
        long rangeEnd;

        long startNs;
        long endNs;

        long linesProcessed;
        long bytesReadApprox;
    }

    public static void main(String[] args) throws Exception {

        int workers = Runtime.getRuntime().availableProcessors();
        System.out.println("Workers: " + workers);

        Path path = Path.of(FILE);
        long fileSize = Files.size(path);

        long programStart = System.nanoTime();

        // -------------------- Phase 1: range splitting --------------------
        long p1Start = System.nanoTime();
        Range[] ranges = splitIntoAlignedRanges(path, fileSize, workers);
        long p1End = System.nanoTime();

        // -------------------- Phase 2: worker processing --------------------
        Thread[] threads = new Thread[workers];
        PartialResult[] results = new PartialResult[workers];
        ThreadTrace[] traces = new ThreadTrace[workers];

        long workersStart = System.nanoTime();

        for (int i = 0; i < workers; i++) {
            final int index = i;
            final long start = ranges[i].start;
            final long end = ranges[i].end;

            ThreadTrace trace = new ThreadTrace();
            trace.threadName = "worker-" + (i + 1);
            trace.rangeStart = start;
            trace.rangeEnd = end;
            traces[index] = trace;

            threads[i] = new Thread(() -> {
                try {
                    trace.startNs = System.nanoTime();
                    results[index] = processByteRange(path, start, end, trace);
                    trace.endNs = System.nanoTime();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, trace.threadName);

            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        long workersEnd = System.nanoTime();

        // Sum of per-thread unique city counts (diagnostic only)
        long totalUniqueAcrossWorkers = 0;
        for (int i = 0; i < workers; i++) {
            int unique = (results[i] == null) ? 0 : results[i].names.size();
            totalUniqueAcrossWorkers += unique;
        }

        // -------------------- Phase 3: merge (HashMap optimised) --------------------
        long mergeStart = System.nanoTime();
        PartialResult merged = mergeAllHashMap(results);
        long mergeEnd = System.nanoTime();

        // -------------------- Phase 4: sort/build output --------------------
        long buildStart = System.nanoTime();
        Map<String, String> output = new TreeMap<>();
        for (int i = 0; i < merged.names.size(); i++) {
            double mean = merged.sum.get(i) / merged.count.get(i);
            output.put(
                    merged.names.get(i),
                    round1(merged.min.get(i)) + "/" + round1(mean) + "/" + round1(merged.max.get(i))
            );
        }
        long buildEnd = System.nanoTime();

        // -------------------- Phase 5: printing --------------------
        long printStart = System.nanoTime();
        System.out.println(output);
        long printEnd = System.nanoTime();

        long programEnd = System.nanoTime();

        double p1 = (p1End - p1Start) / 1_000_000_000.0;
        double p2 = (workersEnd - workersStart) / 1_000_000_000.0;
        double p3 = (mergeEnd - mergeStart) / 1_000_000_000.0;
        double p4 = (buildEnd - buildStart) / 1_000_000_000.0;
        double p5 = (printEnd - printStart) / 1_000_000_000.0;
        double total = (programEnd - programStart) / 1_000_000_000.0;

        System.out.printf("%n--- Phase Timing (Solution 6) ---%n");
        System.out.printf("Range split:        %.3f s%n", p1);
        System.out.printf("Workers (parallel): %.3f s%n", p2);
        System.out.printf("Merge (HashMap):    %.3f s%n", p3);
        System.out.printf("Build TreeMap:      %.3f s%n", p4);
        System.out.printf("Printing output:    %.3f s%n", p5);
        System.out.printf("TOTAL:              %.3f s%n", total);

        System.out.printf("%n--- Merge Load ---%n");
        System.out.printf("Merged unique cities: %d%n", merged.names.size());
        System.out.printf("Sum of per-thread unique city counts: %d%n", totalUniqueAcrossWorkers);

        // -------- Thread trace summary --------
        System.out.println("\n--- Thread Trace Summary ---");
        long earliest = Long.MAX_VALUE;
        long latest = Long.MIN_VALUE;

        for (ThreadTrace tr : traces) {
            if (tr.startNs < earliest) {
                earliest = tr.startNs;
            }
            if (tr.endNs > latest) {
                latest = tr.endNs;
            }
        }

        for (ThreadTrace tr : traces) {
            double startOffsetMs = (tr.startNs - earliest) / 1_000_000.0;
            double durationMs = (tr.endNs - tr.startNs) / 1_000_000.0;

            System.out.printf(
                    "%s range=[%d..%d] start=+%.1fms duration=%.1fms lines=%d approxBytes=%d%n",
                    tr.threadName,
                    tr.rangeStart,
                    tr.rangeEnd,
                    startOffsetMs,
                    durationMs,
                    tr.linesProcessed,
                    tr.bytesReadApprox
            );
        }

        double overallMs = (latest - earliest) / 1_000_000.0;
        System.out.printf("Overall worker span: %.1fms%n", overallMs);
    }

    private static Range[] splitIntoAlignedRanges(Path path, long fileSize, int workers) throws IOException {

        Range[] ranges = new Range[workers];
        long chunkSize = fileSize / workers;

        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {

            long start = 0;

            for (int i = 0; i < workers; i++) {

                long end;

                if (i == workers - 1) {
                    end = fileSize - 1; // last chunk goes to EOF
                }
                else {
                    end = start + chunkSize - 1;
                }

                if (i != 0) {
                    start = alignStartToNextLine(raf, start, fileSize);
                }

                if (i != workers - 1) {
                    end = alignEndToLineBreak(raf, end, fileSize);
                }

                ranges[i] = new Range(start, end);
                start = end + 1;
            }
        }

        return ranges;
    }

    private static long alignStartToNextLine(RandomAccessFile raf, long pos, long fileSize) throws IOException {
        if (pos <= 0) {
            return 0;
        }
        raf.seek(pos);
        int b;
        while (pos < fileSize && (b = raf.read()) != -1) {
            if (b == '\n') {
                return pos + 1;
            }
            pos++;
        }
        return fileSize;
    }

    private static long alignEndToLineBreak(RandomAccessFile raf, long pos, long fileSize) throws IOException {
        if (pos >= fileSize - 1) {
            return fileSize - 1;
        }
        raf.seek(pos);
        int b;
        while (pos < fileSize && (b = raf.read()) != -1) {
            if (b == '\n') {
                return pos;
            }
            pos++;
        }
        return fileSize - 1;
    }

    private static PartialResult processByteRange(Path path, long start, long end, ThreadTrace trace) throws IOException {

        PartialResult r = new PartialResult();

        // -------------------- KEY CHANGE (Solution 6):
        // Worker-local name -> index map for O(1) city lookup, removing findIndex() from hot path.
        // --------------------
        HashMap<String, Integer> localIndexByName = new HashMap<>(16_384);

        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {

            raf.seek(start);

            while (raf.getFilePointer() <= end) {

                long beforeLinePtr = raf.getFilePointer();
                String line = readLineUtf8(raf);
                long afterLinePtr = raf.getFilePointer();

                if (line == null) {
                    break;
                }

                trace.bytesReadApprox += (afterLinePtr - beforeLinePtr);

                // If we crossed the boundary, stop. (boundary is aligned to '\n')
                if (afterLinePtr > end + 1) {
                    break;
                }

                trace.linesProcessed++;

                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                int sep = line.indexOf(';');
                if (sep == -1) {
                    continue;
                }

                String name = line.substring(0, sep);
                double temp = Double.parseDouble(line.substring(sep + 1));

                Integer idxObj = localIndexByName.get(name);

                if (idxObj == null) {
                    int newIndex = r.names.size();
                    localIndexByName.put(name, newIndex);

                    r.names.add(name);
                    r.min.add(temp);
                    r.max.add(temp);
                    r.sum.add(temp);
                    r.count.add(1);
                }
                else {
                    int index = idxObj;

                    if (temp < r.min.get(index)) {
                        r.min.set(index, temp);
                    }

                    if (temp > r.max.get(index)) {
                        r.max.set(index, temp);
                    }

                    r.sum.set(index, r.sum.get(index) + temp);
                    r.count.set(index, r.count.get(index) + 1);
                }
            }
        }

        return r;
    }

    private static String readLineUtf8(RandomAccessFile raf) throws IOException {
        ByteArrayBuilder buf = new ByteArrayBuilder(128);

        int b;
        boolean sawAny = false;

        while ((b = raf.read()) != -1) {
            sawAny = true;
            if (b == '\n') {
                break;
            }
            if (b != '\r') {
                buf.append((byte) b);
            }
        }

        if (!sawAny && buf.length() == 0) {
            return null;
        }

        return new String(buf.toArray(), StandardCharsets.UTF_8);
    }

    static class ByteArrayBuilder {
        private byte[] data;
        private int len;

        ByteArrayBuilder(int initialCapacity) {
            data = new byte[initialCapacity];
            len = 0;
        }

        void append(byte b) {
            if (len == data.length) {
                byte[] bigger = new byte[data.length * 2];
                System.arraycopy(data, 0, bigger, 0, data.length);
                data = bigger;
            }
            data[len++] = b;
        }

        int length() {
            return len;
        }

        byte[] toArray() {
            byte[] out = new byte[len];
            System.arraycopy(data, 0, out, 0, len);
            return out;
        }
    }

    // -------------------- O(1) MERGE USING HASHMAP --------------------
    private static PartialResult mergeAllHashMap(PartialResult[] parts) {
        PartialResult merged = new PartialResult();

        // name -> index in merged arrays
        HashMap<String, Integer> indexByName = new HashMap<>();

        for (PartialResult p : parts) {
            if (p == null) {
                continue;
            }

            for (int i = 0; i < p.names.size(); i++) {

                String name = p.names.get(i);
                Integer idxObj = indexByName.get(name);

                if (idxObj == null) {
                    int newIndex = merged.names.size();
                    indexByName.put(name, newIndex);

                    merged.names.add(name);
                    merged.min.add(p.min.get(i));
                    merged.max.add(p.max.get(i));
                    merged.sum.add(p.sum.get(i));
                    merged.count.add(p.count.get(i));
                }
                else {
                    int idx = idxObj;

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

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
