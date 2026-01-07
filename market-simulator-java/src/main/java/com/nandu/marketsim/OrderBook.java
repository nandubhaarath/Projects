package com.nandu.marketsim;

import java.util.*;

/**
 * Stores resting LIMIT orders using price-time (FIFO) priority.
 *
 * Bids: highest price first
 * Asks: lowest price first
 *
 * Within the same price, orders are FIFO (queue).
 */
public class OrderBook {

    // BUY side: best = highest price => reverse order
    private final NavigableMap<Long, Deque<Order>> bids = new TreeMap<>(Comparator.reverseOrder());

    // SELL side: best = lowest price => natural order
    private final NavigableMap<Long, Deque<Order>> asks = new TreeMap<>();

    public void addLimitOrder(Order order) {
        if (order.type() != OrderType.LIMIT) {
            throw new IllegalArgumentException("Only LIMIT orders can rest on the book");
        }

        var price = order.limitPrice();
        if (price == null) {
            throw new IllegalArgumentException("LIMIT order must have a limitPrice");
        }

        NavigableMap<Long, Deque<Order>> sideMap = (order.side() == Side.BUY) ? bids : asks;

        // Create price level if missing, then append to queue (FIFO)
        sideMap.computeIfAbsent(price, p -> new ArrayDeque<>()).addLast(order);
    }

    public Optional<Long> bestBidPrice() {
        return bids.isEmpty() ? Optional.empty() : Optional.of(bids.firstKey());
    }

    public Optional<Long> bestAskPrice() {
        return asks.isEmpty() ? Optional.empty() : Optional.of(asks.firstKey());
    }

    public Optional<Order> peekBestBid() {
        return bestBidPrice().map(p -> bids.get(p).peekFirst());
    }

    public Optional<Order> peekBestAsk() {
        return bestAskPrice().map(p -> asks.get(p).peekFirst());
    }

    public int bidLevels() {
        return bids.size();
    }

    public int askLevels() {
        return asks.size();
    }

    /**
     * Debug snapshot: top N price levels with aggregated quantity.
     */
    public BookSnapshot snapshotTopLevels(int depth) {
        return new BookSnapshot(
                snapshotSide(bids, depth),
                snapshotSide(asks, depth)
        );
    }

    private static List<PriceLevel> snapshotSide(NavigableMap<Long, Deque<Order>> side, int depth) {
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
    public Optional<Order> pollBestAsk() {
        return pollBest(asks);
    }

    public Optional<Order> pollBestBid() {
        return pollBest(bids);
    }

    private static Optional<Order> pollBest(NavigableMap<Long, Deque<Order>> side) {
        if (side.isEmpty()) return Optional.empty();
        var bestPrice = side.firstKey();
        var q = side.get(bestPrice);

        var order = q.pollFirst(); // take FIFO head
        if (q.isEmpty()) {
            side.remove(bestPrice); // remove empty price level
        }
        return Optional.ofNullable(order);
    }

    public void addFirstAtPrice(Order order) {
        if (order.type() != OrderType.LIMIT) {
            throw new IllegalArgumentException("Only LIMIT orders can be placed on book");
        }
        var price = order.limitPrice();
        if (price == null) throw new IllegalArgumentException("LIMIT order must have limitPrice");

        NavigableMap<Long, Deque<Order>> sideMap = (order.side() == Side.BUY) ? bids : asks;
        sideMap.computeIfAbsent(price, p -> new ArrayDeque<>()).addFirst(order);
    }


    // ---------- Snapshot types (small, useful for debugging) ----------

    public record PriceLevel(long price, long totalQuantity) {}

    public record BookSnapshot(List<PriceLevel> bids, List<PriceLevel> asks) {}
}
