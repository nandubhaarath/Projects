package com.nandu.marketsim;

public class Main {
    public static void main(String[] args)
    {
        OrderBook book = new OrderBook();

        // Add some resting LIMIT orders
        book.addLimitOrder(Order.limit("S1", Side.SELL, 10, 101, 1));
        book.addLimitOrder(Order.limit("S2", Side.SELL,  6, 102, 2));
        book.addLimitOrder(Order.limit("B1", Side.BUY,   5, 100, 3));
        book.addLimitOrder(Order.limit("B2", Side.BUY,  12,  99, 4));

        System.out.println("Best bid: " + book.bestBidPrice().orElse(null));
        System.out.println("Best ask: " + book.bestAskPrice().orElse(null));
        System.out.println("Top levels: " + book.snapshotTopLevels(5));
    }
}
