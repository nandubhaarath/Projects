package com.nandu.marketsim;

import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {

    private final OrderBook book;

    public MatchingEngine(OrderBook book) {
        this.book = book;
    }

    /**
     * Thread-safe submission:
     * We execute the whole matching operation under the OrderBook lock
     * so that checks + polls + reinserts are atomic.
     */
    public List<Trade> submit(Order incoming) {
        return book.withLock(() -> submitInternal(incoming));
    }

    /**
     * Internal matching logic. Assumes OrderBook lock is already held.
     */
    private List<Trade> submitInternal(Order incoming) {
        if (incoming.quantity() <= 0) {
            throw new IllegalArgumentException("Incoming order must have quantity > 0");
        }

        List<Trade> trades = new ArrayList<>();
        long remaining = incoming.quantity();

        boolean isBuy = incoming.side() == Side.BUY;
        boolean isMarket = incoming.type() == OrderType.MARKET;

        while (remaining > 0 && crosses(incoming, isBuy, isMarket)) {

            // Take best maker from the opposite side (FIFO at best price)
            var makerOpt = isBuy ? book.pollBestAsk() : book.pollBestBid();
            if (makerOpt.isEmpty()) break;

            Order maker = makerOpt.get();

            // Maker is always a resting LIMIT order
            Long makerPriceObj = maker.limitPrice();
            if (makerPriceObj == null) {
                throw new IllegalStateException("Resting maker order must have a limitPrice");
            }
            long makerPrice = makerPriceObj;

            long makerQty = maker.quantity();
            long fillQty = Math.min(remaining, makerQty);

            // Trade price = maker (resting) price
            trades.add(new Trade(
                    makerPrice,
                    fillQty,
                    incoming.orderId(),
                    maker.orderId()
            ));

            remaining -= fillQty;

            // If maker partially filled, reinsert remainder at the front of the same price level.
            // This preserves time priority for the original maker.
            if (fillQty < makerQty) {
                Order makerRemainder = Order.limit(
                        maker.orderId(),
                        maker.side(),
                        makerQty - fillQty,
                        makerPrice,
                        maker.timestampNs()
                );
                book.addFirstAtPrice(makerRemainder);
            }
        }

        // If incoming is LIMIT and not fully filled, rest the remainder on the book
        if (!isMarket && remaining > 0) {
            Long limitPriceObj = incoming.limitPrice();
            if (limitPriceObj == null) {
                throw new IllegalStateException("LIMIT order must have a limitPrice");
            }
            Order rest = Order.limit(
                    incoming.orderId(),
                    incoming.side(),
                    remaining,
                    limitPriceObj,
                    incoming.timestampNs()
            );
            book.addLimitOrder(rest);
        }

        // MARKET remainder simply doesn't fill (no resting)
        return trades;
    }

    /**
     * Determines whether the incoming order crosses the current top-of-book.
     * Assumes lock held (because bestBid/bestAsk are protected).
     */
    private boolean crosses(Order incoming, boolean isBuy, boolean isMarket) {
        if (isBuy) {
            var bestAsk = book.bestAskPrice();
            if (bestAsk.isEmpty()) return false;
            if (isMarket) return true;
            return incoming.limitPrice() >= bestAsk.get();
        } else {
            var bestBid = book.bestBidPrice();
            if (bestBid.isEmpty()) return false;
            if (isMarket) return true;
            return incoming.limitPrice() <= bestBid.get();
        }
    }
}
