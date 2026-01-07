package com.nandu.marketsim;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StressSweep {

    public static void main(String[] args) throws Exception {

        // Sweep these queue capacities
        int[] capacities = {5, 10, 20, 50, 100, 200};

        // Load parameters (feel free to tweak later)
        int producerThreads = 8;
        int ordersPerProducer = 200;
        int queueOfferTimeoutMs = 1;

        // Seed liquidity (kept constant across runs)
        // NOTE: Only 10 shares @101 will trade, by design.
        Order seedS1 = Order.limit("S1", Side.SELL, 10, 101, 1);
        Order seedS2 = Order.limit("S2", Side.SELL,  6, 102, 2);

        System.out.println("Stress sweep (single-writer exchange)");
        System.out.println("Producers=" + producerThreads +
                " Orders/producer=" + ordersPerProducer +
                " OfferTimeout=" + queueOfferTimeoutMs + "ms");
        System.out.println();

        System.out.printf("%8s | %8s | %8s | %10s | %10s | %10s | %10s | %10s%n",
                "QCap", "Accept", "Reject", "Throughput", "p50(µs)", "p95(µs)", "p99(µs)", "Max(µs)");
        System.out.println("-----------------------------------------------------------------------------------------------");

        for (int cap : capacities) {
            RunResult r = runOnce(cap, producerThreads, ordersPerProducer, queueOfferTimeoutMs, seedS1, seedS2);

            System.out.printf("%8d | %8d | %8d | %10.2f | %10.2f | %10.2f | %10.2f | %10.2f%n",
                    cap,
                    r.accepted,
                    r.rejected,
                    r.throughput,
                    r.p50,
                    r.p95,
                    r.p99,
                    r.max
            );
        }
    }

    private static RunResult runOnce(
            int queueCapacity,
            int producerThreads,
            int ordersPerProducer,
            int offerTimeoutMs,
            Order seedS1,
            Order seedS2
    ) throws Exception {

        try (ConcurrentExchange ex = new ConcurrentExchange(queueCapacity)) {
            ex.start();

            // Seed liquidity via the queue (keeps single-writer rule)
            ex.submit(seedS1);
            ex.submit(seedS2);

            ExecutorService producers = Executors.newFixedThreadPool(producerThreads);

            AtomicInteger accepted = new AtomicInteger(0);
            AtomicInteger rejected = new AtomicInteger(0);

            for (int p = 0; p < producerThreads; p++) {
                int producerId = p;
                producers.submit(() -> {
                    for (int i = 0; i < ordersPerProducer; i++) {
                        String id = "B" + producerId + "_" + i;

                        Order o = Order.limit(
                                id,
                                Side.BUY,
                                1,
                                101,
                                System.nanoTime()
                        );

                        try {
                            boolean ok = ex.trySubmit(o, offerTimeoutMs, TimeUnit.MILLISECONDS);
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

            // Let consumer drain accepted work
            Thread.sleep(1000);
            ex.stop();
            Thread.sleep(300);

            var pct = ex.queueWaitPercentilesMicros();

            return new RunResult(
                    accepted.get(),
                    rejected.get(),
                    ex.throughputOrdersPerSec(),
                    pct.p50Micros(),
                    pct.p95Micros(),
                    pct.p99Micros(),
                    ex.maxQueueWaitMicros()
            );
        }
    }

    private record RunResult(
            int accepted,
            int rejected,
            double throughput,
            double p50,
            double p95,
            double p99,
            double max
    ) {}
}
