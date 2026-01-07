package com.nandu.marketsim;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Simulator {

    private final PriorityQueue<Event> queue = new PriorityQueue<>();
    private long seq = 0;
    private long nowMs = 0;

    private final OrderBook book = new OrderBook();
    private final MatchingEngine engine = new MatchingEngine(book);

    private final List<TimedTrade> timedTrades = new ArrayList<>();

    public OrderBook book() {
        return book;
    }

    public List<TimedTrade> timedTrades() {
        return timedTrades;
    }

    public long nowMs() {
        return nowMs;
    }

    public void schedule(long timeMs, EventType type, Object payload) {
        if (timeMs < 0) throw new IllegalArgumentException("timeMs must be >= 0");
        seq++;
        queue.add(new Event(timeMs, seq, type, payload));
    }

    public void run() {
        while (!queue.isEmpty()) {
            Event ev = queue.poll();
            nowMs = ev.timeMs();

            if (ev.type() == EventType.ORDER_ARRIVAL) {
                Order order = (Order) ev.payload();
                var trades = engine.submit(order);
                for (Trade tr : trades) {
                    timedTrades.add(new TimedTrade(nowMs, tr));
                }
            } else {
                throw new IllegalStateException("Unhandled event type: " + ev.type());
            }
        }
    }
}
