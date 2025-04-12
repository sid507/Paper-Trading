package com.tradingplatform.trading_backed.orders;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tradingplatform.trading_backed.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

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

}
