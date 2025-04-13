package com.tradingplatform.trading_backed.orders;

import com.tradingplatform.trading_backed.auth.User;
import com.tradingplatform.trading_backed.auth.UserRepository;
import com.tradingplatform.trading_backed.engine.OrderMatchingEngine;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMatchingEngine orderMatchingEngine;
    private final UserRepository userRepository;

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
        Optional<User> user = userRepository.findByUsername(order.getUser().getUsername());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        //create order build logic
        Order newOrder = Order.builder()
                .stockSymbol(order.getStockSymbol())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .orderType(order.getOrderType())
                .user(user.get())
                .build();

        newOrder = orderService.saveOrder(newOrder);

        
        orderMatchingEngine.addOrder(newOrder);
        try {
            orderService.sendOrderToUser(newOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // add delete all mapping
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAllOrders() {
        orderService.deleteAllOrders();
        return ResponseEntity.ok("All orders deleted successfully");
    }
}
