package com.tradingplatform.trading_backed.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingplatform.trading_backed.feed.MyWebSocketHandler;
import com.tradingplatform.trading_backed.feed.UserSessions;
import com.tradingplatform.trading_backed.orders.*;
import com.tradingplatform.trading_backed.trades.Trade;
import com.tradingplatform.trading_backed.trades.TradeRepository;

import lombok.RequiredArgsConstructor;

import java.util.PriorityQueue;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

@Service
@RequiredArgsConstructor
public class OrderMatchingEngine {


    private final OrderBook orderBook;
    private final TradeRepository tradeRepository;  
    private final MyWebSocketHandler myWebSocketHandler;

    @Autowired
    private UserSessions userSessions;

    // Buy orders (Max-Heap: Highest price first)
    private final Queue<Order> buyOrders = new PriorityQueue<>((o1, o2) -> o2.getPrice().compareTo(o1.getPrice()));

    // Sell orders (Min-Heap: Lowest price first)
    private final Queue<Order> sellOrders = new PriorityQueue<>((o1, o2) -> o1.getPrice().compareTo(o2.getPrice()));

    public synchronized void addOrder(Order order) {
        if (order.getOrderType() == OrderType.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
        matchOrders(); // Check for possible matches
    }

    //45Q8AI4YNN1ZKSF4

    private synchronized void matchOrders() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order bestBid = buyOrders.peek(); // Highest Buy Order
            Order bestAsk = sellOrders.peek(); // Lowest Sell Order

            // Check if a trade is possible
            if (bestBid.getPrice().compareTo(bestAsk.getPrice()) >= 0) {
                int tradeQuantity = Math.min(bestBid.getQuantity(), bestAsk.getQuantity());
                Trade trade = new Trade();
                trade.setBuyerOrderUsername(bestBid.getUser().getUsername());
                trade.setSellerOrderUsername(bestAsk.getUser().getUsername());
                trade.setQuantity(tradeQuantity);
                trade.setPrice(bestAsk.getPrice());
                trade.setSymbol(bestAsk.getStockSymbol());
                trade.setSide(bestBid.getOrderType());
                // Set other trade details as needed
                // Save the trade to the repository
                tradeRepository.save(trade);
                String buyerUsername = bestBid.getUser().getUsername();
                String sellerUsername = bestAsk.getUser().getUsername();
                try {
                    String tradeString = ( new ObjectMapper()).writeValueAsString(trade);
                    myWebSocketHandler.sendMsgToUser(buyerUsername,tradeString);
                    myWebSocketHandler.sendMsgToUser(sellerUsername, tradeString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Trade Executed: " + tradeQuantity + " shares at $" + bestAsk.getPrice());

                // Update quantities or remove orders
                if (bestBid.getQuantity() > tradeQuantity) {
                    bestBid.setQuantity(bestBid.getQuantity() - tradeQuantity);
                } else {
                    buyOrders.poll(); // Remove fully matched order
                }

                if (bestAsk.getQuantity() > tradeQuantity) {
                    bestAsk.setQuantity(bestAsk.getQuantity() - tradeQuantity);
                } else {
                    sellOrders.poll(); // Remove fully matched order
                }
            } else {
                break; // No more possible matches
            }
        }
    }
}
