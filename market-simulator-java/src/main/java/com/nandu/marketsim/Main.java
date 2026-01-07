package com.nandu.marketsim;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws Exception {

        // Tiny queue to force backpressure
        try (ConcurrentExchange ex = new ConcurrentExchange(20)) {
            ex.start();

            // Seed liquidity
            ex.submit(Order.limit("S1", Side.SELL, 10, 101, 1));
            ex.submit(Order.limit("S2", Side.SELL,  6, 102, 2));

            int producerThreads = 8;
            int ordersPerProducer = 200;

            ExecutorService producers = Executors.newFixedThreadPool(producerThreads);

            AtomicInteger accepted = new AtomicInteger(0);
            AtomicInteger rejected = new AtomicInteger(0);

            for (int p = 0; p < producerThreads; p++) {
                int producerId = p;
                producers.submit(() -> {
                    for (int i = 0; i < ordersPerProducer; i++) {
                        String id = "B" + producerId + "_" + i;
                        Order o = Order.limit(id, Side.BUY, 1, 101, System.nanoTime());

                        try {
                            // Fail-fast style: if queue is full, count rejection (like HTTP 429)
                            boolean ok = ex.trySubmit(o, 1, TimeUnit.MILLISECONDS);
                            if (ok) accepted.incrementAndGet();
                            else rejected.incrementAndGet();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                });
            }

            producers.shutdown();
            producers.awaitTermination(5, TimeUnit.SECONDS);

            // Let consumer drain
            Thread.sleep(1000);
            ex.stop();
            Thread.sleep(300);

            System.out.println("Accepted: " + accepted.get());
            System.out.println("Rejected (backpressure): " + rejected.get());
            System.out.println("Processed orders: " + ex.processedCount());
            System.out.println("Trades executed: " + ex.tradesCount());
            System.out.printf("Avg queue wait: %.2f microseconds%n", ex.avgQueueWaitMicros());
            System.out.printf("Max queue wait: %.2f microseconds%n", ex.maxQueueWaitMicros());
            System.out.printf("Throughput: %.2f orders/sec%n", ex.throughputOrdersPerSec());
            System.out.println("Final book snapshot: " + ex.book().snapshotTopLevels(5));
        }
    }
}
