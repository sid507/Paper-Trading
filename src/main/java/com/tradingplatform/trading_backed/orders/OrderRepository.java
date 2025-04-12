package com.tradingplatform.trading_backed.orders;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrderById(Long id);
    List<Order> findOrderByUserId(Long userId);
    List<Order> findOrderByStockSymbol(String stockSymbol);
}
