package com.nandu.marketsim;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {

        try (ConcurrentExchange ex = new ConcurrentExchange(1000)) {
            ex.start();

            // Seed liquidity (submit into the queue too, to keep single-writer rule)
            ex.submit(Order.limit("S1", Side.SELL, 10, 101, 1));
            ex.submit(Order.limit("S2", Side.SELL,  6, 102, 2));

            int producerThreads = 4;
            int ordersPerProducer = 50;

            ExecutorService producers = Executors.newFixedThreadPool(producerThreads);

            for (int p = 0; p < producerThreads; p++) {
                int producerId = p;
                producers.submit(() -> {
                    for (int i = 0; i < ordersPerProducer; i++) {
                        String id = "B" + producerId + "_" + i;
                        // each tries to buy 1 share at 101
                        Order o = Order.limit(id, Side.BUY, 1, 101, 1000 + producerId * 100 + i);
                        try {
                            ex.submit(o);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                });
            }

            producers.shutdown();
            producers.awaitTermination(2, TimeUnit.SECONDS);

            // Drain queue then stop
            Thread.sleep(300);
            ex.stop();
            Thread.sleep(300);

            System.out.println("Processed orders: " + ex.processedCount());
            System.out.println("Trades executed: " + ex.trades().size());
            System.out.printf("Avg queue wait: %.2f microseconds%n", ex.avgQueueWaitMicros());
            System.out.println("Final book snapshot: " + ex.book().snapshotTopLevels(5));
        }
    }
}
