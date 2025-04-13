package com.tradingplatform.trading_backed.orders;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingplatform.trading_backed.auth.UserRepository;
import com.tradingplatform.trading_backed.feed.MyWebSocketHandler;
import com.tradingplatform.trading_backed.trades.TradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final MyWebSocketHandler myWebSocketHandler;
    private final TradeRepository tradeRepository;


    // place order
    public Order placeOrder(String stockSymbol, int quantity, BigDecimal price, OrderType orderType) {
        
        String userName = ((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        return userRepository.findByUsername(userName).map(user -> {
            Order order = new Order();
            order.setStockSymbol(stockSymbol);
            order.setQuantity(quantity);
            order.setPrice(price);
            order.setOrderType(orderType);
            order.setUser(user);
            return orderRepository.save(order);
        })
        .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // get order according to user
    public List<Order> getOrdersByUser() {
        String userName = ((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByUsername(userName)
                .map(user-> { return orderRepository.findOrderByUserId(user.getId());})
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

            // 📌 3. Get Orders by Stock Symbol
    public List<Order> getOrdersByStockSymbol(String stockSymbol) {
        return orderRepository.findOrderByStockSymbol(stockSymbol);
    }


    //save order
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public void sendOrderToUser(Order order) throws Exception {
        try {
            String message = objectMapper.writeValueAsString(Map.of(
                "type", "ORDER_UPDATE",
                "order", order
            ));
            String username = order.getUser().getUsername();
            myWebSocketHandler.sendMsgToUser(username, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // delete all order
    public void deleteAllOrders() {
        tradeRepository.deleteAll();
        orderRepository.deleteAll();
    }

}
