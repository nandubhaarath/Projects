package com.nandu.marketsim;

import java.util.Objects;

public record Order(
        String orderId,
        Side side,
        OrderType type,
        long quantity,
        Long limitPrice,
        long timestampNs
) {
    public Order {
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(side, "side");
        Objects.requireNonNull(type, "type");

        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }

        if (type == OrderType.LIMIT) {
            if (limitPrice == null || limitPrice <= 0) {
                throw new IllegalArgumentException(
                        "limitPrice must be provided and > 0 for LIMIT orders"
                );
            }
        } else { // MARKET
            if (limitPrice != null) {
                throw new IllegalArgumentException(
                        "limitPrice must be null for MARKET orders"
                );
            }
        }
    }

    public static Order limit(
            String orderId,
            Side side,
            long quantity,
            long limitPrice,
            long timestampNs
    ) {
        return new Order(orderId, side, OrderType.LIMIT, quantity, limitPrice, timestampNs);
    }

    public static Order market(
            String orderId,
            Side side,
            long quantity,
            long timestampNs
    ) {
        return new Order(orderId, side, OrderType.MARKET, quantity, null, timestampNs);
    }
}
