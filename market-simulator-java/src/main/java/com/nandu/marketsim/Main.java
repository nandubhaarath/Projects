package com.nandu.marketsim;

public class Main {
    public static void main(String[] args) {
        Simulator sim = new Simulator();

        // Seed liquidity at t=0ms
        sim.schedule(0, EventType.ORDER_ARRIVAL, Order.limit("S1", Side.SELL, 10, 101, 1));
        sim.schedule(0, EventType.ORDER_ARRIVAL, Order.limit("S2", Side.SELL,  6, 102, 2));

        // Two buyers for the same best ask price
        // A is "sent first" (timestampNs smaller), but arrives later (higher latency)
        sim.schedule(8, EventType.ORDER_ARRIVAL, Order.limit("A", Side.BUY, 10, 101, 3));
        sim.schedule(2, EventType.ORDER_ARRIVAL, Order.limit("B", Side.BUY, 10, 101, 4));

        sim.run();

        System.out.println("Timed trades:");
        for (var tt : sim.timedTrades()) {
            var tr = tt.trade();
            System.out.printf("t=%dms  %d @ %d  taker=%s maker=%s%n",
                    tt.timeMs(), tr.quantity(), tr.price(), tr.takerOrderId(), tr.makerOrderId());
        }

        System.out.println("\nFinal book snapshot:");
        System.out.println(sim.book().snapshotTopLevels(5));
    }
}
