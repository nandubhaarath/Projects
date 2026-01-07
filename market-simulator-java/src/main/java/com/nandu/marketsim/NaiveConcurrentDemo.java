package com.nandu.marketsim;

import java.util.List;
import java.util.concurrent.*;

public class NaiveConcurrentDemo {

    public static void main(String[] args) throws Exception {
        OrderBook book = new OrderBook();
        MatchingEngine engine = new MatchingEngine(book);

        // Seed liquidity: ONLY 10 shares at 101
        book.addLimitOrder(Order.limit("S1", Side.SELL, 10, 101, 1));

        int threads = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        // Barrier ensures threads start submit() at the same time
        CyclicBarrier barrier = new CyclicBarrier(threads);

        Callable<Integer> task = () -> {
            barrier.await();

            // Everyone tries to buy 1 share @ 101 at the same time
            List<Trade> trades = engine.submit(Order.limit(
                    "B-" + Thread.currentThread().threadId(),
                    Side.BUY,
                    1,
                    101,
                    System.nanoTime()
            ));
            return trades.size();
        };

        List<Future<Integer>> results = pool.invokeAll(java.util.Collections.nCopies(threads, task));
        pool.shutdown();
        pool.awaitTermination(2, TimeUnit.SECONDS);

        int totalTrades = 0;
        for (Future<Integer> f : results) totalTrades += f.get();

        System.out.println("Total trades reported by threads: " + totalTrades);
        System.out.println("Final book snapshot: " + book.snapshotTopLevels(5));
    }
}
