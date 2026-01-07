package com.nandu.marketsim;

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

    public ConcurrentExchange(int queueCapacity) {
        this.inbound = new ArrayBlockingQueue<>(queueCapacity);
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

    private static void updateMax(AtomicLong max, long value) {
        long prev;
        do {
            prev = max.get();
            if (value <= prev) return;
        } while (!max.compareAndSet(prev, value));
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
}
