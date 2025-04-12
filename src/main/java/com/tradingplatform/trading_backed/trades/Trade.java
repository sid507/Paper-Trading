package com.tradingplatform.trading_backed.trades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

import com.tradingplatform.trading_backed.auth.User;
import com.tradingplatform.trading_backed.orders.OrderType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trades")
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buyerOrderUsername; 
    private String sellerOrderUsername;
    
    private String symbol;
    private OrderType side; // "buy" or "sell"
    private BigDecimal price;
    private int quantity;
}
