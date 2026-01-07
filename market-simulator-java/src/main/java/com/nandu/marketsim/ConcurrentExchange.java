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
    private final AtomicLong totalQueueWaitNs = new AtomicLong(0);

    // Store trades (simple approach for now)
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

    public double avgQueueWaitMicros() {
        long count = processedCount.get();
        if (count == 0) return 0.0;
        return (totalQueueWaitNs.get() / 1_000.0) / count;
    }

    /**
     * Producers call this to submit orders.
     * If the queue is full, this call BLOCKS (this is backpressure).
     */
    public void submit(Order order) throws InterruptedException {
        inbound.put(new QueuedOrder(System.nanoTime(), order));
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            consumerExec.submit(this::consumeLoop);
        }
    }

    private void consumeLoop() {
        while (running.get() || !inbound.isEmpty()) {
            try {
                QueuedOrder qo = inbound.poll(50, TimeUnit.MILLISECONDS);
                if (qo == null) continue;

                long waitNs = System.nanoTime() - qo.enqueuedAtNs();
                totalQueueWaitNs.addAndGet(waitNs);

                var ts = engine.submit(qo.order());
                trades.addAll(ts);
                processedCount.incrementAndGet();

            } catch (InterruptedException ie) {
                // allow graceful stop
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                // In a real system you'd log and decide how to handle
                ex.printStackTrace();
            }
        }
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
            consumerExec.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            consumerExec.shutdownNow();
        }
    }
}
