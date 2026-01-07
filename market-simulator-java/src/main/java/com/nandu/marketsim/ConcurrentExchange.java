package com.nandu.marketsim;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentExchange implements AutoCloseable {

    private final BlockingQueue<QueuedOrder> inbound;
    private final OrderBook book = new OrderBook();
    private final MatchingEngine engine = new MatchingEngine(book);

    private final ExecutorService consumerExec = Executors.newSingleThreadExecutor();
    private final AtomicBoolean running = new AtomicBoolean(false);

    // Metrics
    private final AtomicLong processedCount = new AtomicLong(0);
    private final AtomicLong tradesCount = new AtomicLong(0);

    private final AtomicLong totalQueueWaitNs = new AtomicLong(0);
    private final AtomicLong maxQueueWaitNs = new AtomicLong(0);

    private final AtomicLong startNs = new AtomicLong(0);
    private final AtomicLong endNs = new AtomicLong(0);

    // Trades stored (simple demo). In real systems you'd stream these.
    private final CopyOnWriteArrayList<Trade> trades = new CopyOnWriteArrayList<>();

    // ---- Percentile sampling (single consumer thread writes; safe) ----
    // Dynamic array to store wait times. We compute percentiles at the end.
    private long[] waitSamplesNs = new long[16_384];
    private int waitSampleCount = 0;
    private final int maxSamples; // cap memory

    public ConcurrentExchange(int queueCapacity) {
        this(queueCapacity, 1_000_000); // default: up to 1M samples
    }

    public ConcurrentExchange(int queueCapacity, int maxSamples) {
        this.inbound = new ArrayBlockingQueue<>(queueCapacity);
        this.maxSamples = Math.max(10_000, maxSamples);
    }

    public OrderBook book() {
        return book;
    }

    public List<Trade> trades() {
        return trades;
    }

    public long processedCount() {
        return processedCount.get();
    }

    public long tradesCount() {
        return tradesCount.get();
    }

    public double avgQueueWaitMicros() {
        long count = processedCount.get();
        if (count == 0) return 0.0;
        return (totalQueueWaitNs.get() / 1_000.0) / count;
    }

    public double maxQueueWaitMicros() {
        return maxQueueWaitNs.get() / 1_000.0;
    }

    public double throughputOrdersPerSec() {
        long end = endNs.get();
        long start = startNs.get();
        long count = processedCount.get();

        if (start == 0 || end == 0 || end <= start) return 0.0;

        double seconds = (end - start) / 1_000_000_000.0;
        return seconds == 0.0 ? 0.0 : count / seconds;
    }

    /**
     * Percentiles over recorded queue-wait samples.
     * Call after stop/drain for stable results.
     */
    public Percentiles queueWaitPercentilesMicros() {
        // We sort a copy so the internal array remains as-is
        int n = waitSampleCount;
        if (n == 0) return new Percentiles(0.0, 0.0, 0.0, 0);

        long[] copy = Arrays.copyOf(waitSamplesNs, n);
        Arrays.sort(copy);

        double p50 = percentile(copy, 50) / 1_000.0;
        double p95 = percentile(copy, 95) / 1_000.0;
        double p99 = percentile(copy, 99) / 1_000.0;

        return new Percentiles(p50, p95, p99, n);
    }

    /**
     * Backpressure variant: blocks if queue is full.
     */
    public void submit(Order order) throws InterruptedException {
        inbound.put(new QueuedOrder(System.nanoTime(), order));
    }

    /**
     * Backpressure variant: fails fast if queue is full.
     * Useful to simulate HTTP 429 style behaviour.
     */
    public boolean trySubmit(Order order, long timeout, TimeUnit unit) throws InterruptedException {
        return inbound.offer(new QueuedOrder(System.nanoTime(), order), timeout, unit);
    }

    public int queueSize() {
        return inbound.size();
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            startNs.compareAndSet(0, System.nanoTime());
            consumerExec.submit(this::consumeLoop);
        }
    }

    private void consumeLoop() {
        while (running.get() || !inbound.isEmpty()) {
            try {
                QueuedOrder qo = inbound.poll(50, TimeUnit.MILLISECONDS);
                if (qo == null) continue;

                long now = System.nanoTime();
                long waitNs = now - qo.enqueuedAtNs();

                totalQueueWaitNs.addAndGet(waitNs);
                updateMax(maxQueueWaitNs, waitNs);
                recordWaitSample(waitNs);

                var ts = engine.submit(qo.order());
                if (!ts.isEmpty()) {
                    trades.addAll(ts);
                    tradesCount.addAndGet(ts.size());
                }

                processedCount.incrementAndGet();

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        endNs.set(System.nanoTime());
    }

    private void recordWaitSample(long waitNs) {
        // Cap total samples to bound memory
        if (waitSampleCount >= maxSamples) return;

        // Grow array if needed (single consumer thread => safe)
        if (waitSampleCount == waitSamplesNs.length) {
            int newSize = Math.min(waitSamplesNs.length * 2, maxSamples);
            waitSamplesNs = Arrays.copyOf(waitSamplesNs, newSize);
        }

        waitSamplesNs[waitSampleCount++] = waitNs;
    }

    private static void updateMax(AtomicLong max, long value) {
        long prev;
        do {
            prev = max.get();
            if (value <= prev) return;
        } while (!max.compareAndSet(prev, value));
    }

    /**
     * Percentile calculation with "nearest-rank" style indexing.
     * For n samples sorted ascending, percentile p uses index:
     * idx = ceil(p/100 * n) - 1 (clamped)
     */
    private static long percentile(long[] sorted, int p) {
        int n = sorted.length;
        if (n == 0) return 0;

        double rank = (p / 100.0) * n;
        int idx = (int) Math.ceil(rank) - 1;

        if (idx < 0) idx = 0;
        if (idx >= n) idx = n - 1;

        return sorted[idx];
    }

    /**
     * Stop accepting new work; drains remaining queued orders.
     */
    public void stop() {
        running.set(false);
    }

    @Override
    public void close() {
        stop();
        consumerExec.shutdown();
        try {
            consumerExec.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            consumerExec.shutdownNow();
        }
    }

    public record Percentiles(double p50Micros, double p95Micros, double p99Micros, int samples) {}
}
