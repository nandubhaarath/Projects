package com.nandu.marketsim;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        OrderBook book = new OrderBook();
        MatchingEngine engine = new MatchingEngine(book);

        // Seed book: sellers
        book.addLimitOrder(Order.limit("S1", Side.SELL, 10, 101, 1));
        book.addLimitOrder(Order.limit("S2", Side.SELL,  6, 102, 2));

        // Buyer comes in: BUY 15 @ 102 => should fill 10@101 + 5@102
        Order buy = Order.limit("B1", Side.BUY, 15, 102, 3);

        List<Trade> trades = engine.submit(buy);

        System.out.println("Trades:");
        for (Trade t : trades) {
            System.out.println(t);
        }

        System.out.println("\nFinal book snapshot:");
        System.out.println(book.snapshotTopLevels(5));
    }
}
