package com.nandu.marketsim;

import java.util.Objects;

public record Trade(
        long price,
        long quantity,
        String takerOrderId,
        String makerOrderId
) {
    public Trade {
        if (price <= 0) throw new IllegalArgumentException("price must be > 0");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        Objects.requireNonNull(takerOrderId, "takerOrderId");
        Objects.requireNonNull(makerOrderId, "makerOrderId");
    }
}
