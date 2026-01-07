package com.nandu.marketsim;

public record QueuedOrder(long enqueuedAtNs, Order order) {}
