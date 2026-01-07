package com.nandu.marketsim;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe OrderBook using a single lock.
 *
 * Stores resting LIMIT orders using price-time (FIFO) priority:
 * - Bids: highest price first
 * - Asks: lowest price first
 * - Within the same price: FIFO queue (ArrayDeque)
 *
 * NOTE:
 * For correctness under concurrent submissions, it's best to hold the lock
 * across an entire matching operation (see withLock()).
 */
public class OrderBook {

    private final ReentrantLock lock = new ReentrantLock();

    // BUY side: best = highest price => reverse order
    private final NavigableMap<Long, Deque<Order>> bids = new TreeMap<>(Comparator.reverseOrder());

    // SELL side: best = lowest price => natural order
    private final NavigableMap<Long, Deque<Order>> asks = new TreeMap<>();

    /**
     * Run an action while holding the book lock.
     * Use this to make a whole matching operation atomic.
     */
    public <T> T withLock(Callable<T> action) {
        lock.lock();
        try {
            return action.call();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void addLimitOrder(Order order) {
        lock.lock();
        try {
            if (order.type() != OrderType.LIMIT) {
                throw new IllegalArgumentException("Only LIMIT orders can rest on the book");
            }

            Long price = order.limitPrice();
            if (price == null || price <= 0) {
                throw new IllegalArgumentException("LIMIT order must have a limitPrice > 0");
            }

            NavigableMap<Long, Deque<Order>> sideMap = (order.side() == Side.BUY) ? bids : asks;
            sideMap.computeIfAbsent(price, p -> new ArrayDeque<>()).addLast(order); // FIFO
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reinsert an order at the front of its price level queue.
     * Used when a resting maker is partially filled.
     */
    public void addFirstAtPrice(Order order) {
        lock.lock();
        try {
            if (order.type() != OrderType.LIMIT) {
                throw new IllegalArgumentException("Only LIMIT orders can be placed on book");
            }
            Long price = order.limitPrice();
            if (price == null || price <= 0) {
                throw new IllegalArgumentException("LIMIT order must have a limitPrice > 0");
            }

            NavigableMap<Long, Deque<Order>> sideMap = (order.side() == Side.BUY) ? bids : asks;
            sideMap.computeIfAbsent(price, p -> new ArrayDeque<>()).addFirst(order);
        } finally {
            lock.unlock();
        }
    }

    public Optional<Long> bestBidPrice() {
        lock.lock();
        try {
            return bids.isEmpty() ? Optional.empty() : Optional.of(bids.firstKey());
        } finally {
            lock.unlock();
        }
    }

    public Optional<Long> bestAskPrice() {
        lock.lock();
        try {
            return asks.isEmpty() ? Optional.empty() : Optional.of(asks.firstKey());
        } finally {
            lock.unlock();
        }
    }

    public Optional<Order> peekBestBid() {
        lock.lock();
        try {
            if (bids.isEmpty()) return Optional.empty();
            long bestPrice = bids.firstKey();
            Deque<Order> q = bids.get(bestPrice);
            return Optional.ofNullable(q.peekFirst());
        } finally {
            lock.unlock();
        }
    }

    public Optional<Order> peekBestAsk() {
        lock.lock();
        try {
            if (asks.isEmpty()) return Optional.empty();
            long bestPrice = asks.firstKey();
            Deque<Order> q = asks.get(bestPrice);
            return Optional.ofNullable(q.peekFirst());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Remove and return the best ask (FIFO at best price).
     */
    public Optional<Order> pollBestAsk() {
        lock.lock();
        try {
            return pollBestInternal(asks);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Remove and return the best bid (FIFO at best price).
     */
    public Optional<Order> pollBestBid() {
        lock.lock();
        try {
            return pollBestInternal(bids);
        } finally {
            lock.unlock();
        }
    }

    private static Optional<Order> pollBestInternal(NavigableMap<Long, Deque<Order>> side) {
        if (side.isEmpty()) return Optional.empty();

        long bestPrice = side.firstKey();
        Deque<Order> q = side.get(bestPrice);

        Order order = q.pollFirst(); // FIFO
        if (q.isEmpty()) {
            side.remove(bestPrice);
        }
        return Optional.ofNullable(order);
    }

    public int bidLevels() {
        lock.lock();
        try {
            return bids.size();
        } finally {
            lock.unlock();
        }
    }

    public int askLevels() {
        lock.lock();
        try {
            return asks.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Debug snapshot: top N price levels with aggregated quantity.
     * Safe under concurrency (locked).
     */
    public BookSnapshot snapshotTopLevels(int depth) {
        lock.lock();
        try {
            return new BookSnapshot(
                    snapshotSideInternal(bids, depth),
                    snapshotSideInternal(asks, depth)
            );
        } finally {
            lock.unlock();
        }
    }

    private static List<PriceLevel> snapshotSideInternal(NavigableMap<Long, Deque<Order>> side, int depth) {
        List<PriceLevel> out = new ArrayList<>();
        int count = 0;

        for (var entry : side.entrySet()) {
            long price = entry.getKey();
            long totalQty = entry.getValue().stream().mapToLong(Order::quantity).sum();
            out.add(new PriceLevel(price, totalQty));

            count++;
            if (count >= depth) break;
        }
        return out;
    }

    // ---------- Snapshot types ----------

    public record PriceLevel(long price, long totalQuantity) {}

    public record BookSnapshot(List<PriceLevel> bids, List<PriceLevel> asks) {}
}
