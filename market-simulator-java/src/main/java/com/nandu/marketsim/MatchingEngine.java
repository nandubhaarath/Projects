package com.nandu.marketsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatchingEngine {

    private final OrderBook book;

    public MatchingEngine(OrderBook book) {
        this.book = book;
    }

    public List<Trade> submit(Order incoming) {
        if (incoming.quantity() <= 0) {
            throw new IllegalArgumentException("Incoming order must have quantity > 0");
        }

        List<Trade> trades = new ArrayList<>();
        long remaining = incoming.quantity();

        boolean isBuy = incoming.side() == Side.BUY;
        boolean isMarket = incoming.type() == OrderType.MARKET;

        while (remaining > 0 && crosses(incoming, isBuy, isMarket)) {
            // Take best maker from opposite side (FIFO at best price)
            Optional<Order> makerOpt = isBuy ? book.pollBestAsk() : book.pollBestBid();
            if (makerOpt.isEmpty()) break;

            Order maker = makerOpt.get();

            long makerPrice = maker.limitPrice(); // maker is always resting LIMIT
            long makerQty = maker.quantity();
            long fillQty = Math.min(remaining, makerQty);

            // Trade price = maker price (resting order price)
            trades.add(new Trade(
                    makerPrice,
                    fillQty,
                    incoming.orderId(),
                    maker.orderId()
            ));

            remaining -= fillQty;

            // If maker not fully filled, put remainder back at the front (keeps time priority)
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

        // If incoming is LIMIT and not fully filled, rest it on the book with remaining qty
        if (!isMarket && remaining > 0) {
            Order rest = Order.limit(
                    incoming.orderId(),
                    incoming.side(),
                    remaining,
                    incoming.limitPrice(),
                    incoming.timestampNs()
            );
            book.addLimitOrder(rest);
        }

        // If incoming is MARKET and remaining > 0, it just doesn't fill (book was empty)
        return trades;
    }

    private boolean crosses(Order incoming, boolean isBuy, boolean isMarket) {
        if (isBuy) {
            // BUY crosses if there is any ask and:
            // - market order OR limitPrice >= best ask
            var bestAsk = book.bestAskPrice();
            if (bestAsk.isEmpty()) return false;
            if (isMarket) return true;
            return incoming.limitPrice() >= bestAsk.get();
        } else {
            // SELL crosses if there is any bid and:
            // - market order OR limitPrice <= best bid
            var bestBid = book.bestBidPrice();
            if (bestBid.isEmpty()) return false;
            if (isMarket) return true;
            return incoming.limitPrice() <= bestBid.get();
        }
    }
}
