package com.tradingplatform.trading_backed.orders;

public enum OrderStatus {
    PENDING,
    FILLED,
    CANCELED,
    REJECTED,
    PARTIALLY_FILLED,
    EXPIRED;

    public static OrderStatus fromString(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown order status: " + status);
        }
    }
}
