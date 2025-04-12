package com.tradingplatform.trading_backed.orders;

import com.tradingplatform.trading_backed.engine.OrderMatchingEngine;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMatchingEngine orderMatchingEngine;

    // 📌 1. Place an order (Buy/Sell)
    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@Valid @RequestBody Order order) {
        System.out.println("Order: " + order);
        // print every param of order
        System.out.println("Stock Symbol: " + order.getStockSymbol());
        System.out.println("Quantity: " + order.getQuantity());
        System.out.println("Price: " + order.getPrice());
        System.out.println("Order Type: " + order.getOrderType());
        System.out.println("User: " + order.getUser().getEmail());
        

        orderMatchingEngine.addOrder(order);
        return ResponseEntity.ok("Order placed successfully");
    }

    // 📌 2. Get all orders for the authenticated user
    @GetMapping("/user")
    public ResponseEntity<List<Order>> getUserOrders() {
        List<Order> orders = orderService.getOrdersByUser();
        return ResponseEntity.ok(orders);
    }

    // 📌 3. Get orders by stock symbol
    @GetMapping("/{stockSymbol}")
    public ResponseEntity<List<Order>> getOrdersByStockSymbol(@PathVariable String stockSymbol) {
        List<Order> orders = orderService.getOrdersByStockSymbol(stockSymbol);
        return ResponseEntity.ok(orders);
    }
}
