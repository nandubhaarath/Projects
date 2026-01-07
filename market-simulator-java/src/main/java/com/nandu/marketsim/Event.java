package com.nandu.marketsim;

public record Event(
        long timeMs,
        long seq,
        EventType type,
        Object payload
) implements Comparable<Event> {

    @Override
    public int compareTo(Event other) {
        int t = Long.compare(this.timeMs, other.timeMs);
        if (t != 0) return t;
        return Long.compare(this.seq, other.seq);
    }
}
