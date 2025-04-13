package com.tradingplatform.trading_backed.orders;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrderById(Long id);
    List<Order> findOrderByUserId(Long userId);
    List<Order> findOrderByStockSymbol(String stockSymbol);
    
    // @Query("SELECT o FROM Order o WHERE o.stockSymbol = :stockSymbol AND o.status = 'FILLED'")
    // List<Order> findFilledOrdersByStockSymbol(String stockSymbol);
}
