package com.tradingplatform.trading_backed.engine;

import com.tradingplatform.trading_backed.orders.*;
import java.util.PriorityQueue;
import java.util.Queue;

import org.springframework.stereotype.Service;

@Service
public class OrderBook {
    
    // Buy orders (Highest price first)
    private final Queue<Order> buyOrders = new PriorityQueue<>((o1, o2) -> o2.getPrice().compareTo(o1.getPrice()));

    // Sell orders (Lowest price first)
    private final Queue<Order> sellOrders = new PriorityQueue<>((o1, o2) -> o1.getPrice().compareTo(o2.getPrice()));

    public void addOrder(Order order) {
        if (order.getOrderType() == OrderType.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
    }

    public Order getBestBuyOrder() {
        return buyOrders.peek();
    }

    public Order getBestSellOrder() {
        return sellOrders.peek();
    }

    public Order removeBestBuyOrder() {
        return buyOrders.poll();
    }

    public Order removeBestSellOrder() {
        return sellOrders.poll();
    }
}
