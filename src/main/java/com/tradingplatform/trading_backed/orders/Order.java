package com.tradingplatform.trading_backed.orders;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.Builder.Default;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tradingplatform.trading_backed.auth.User;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User name cannot be null")
    @ManyToOne  // Linking to the User who placed the order
    @JoinColumn(name = "username")
    private User user;

    @NotNull(message = "Stock symbol cannot be null")
    @Column(nullable = false)
    private String stockSymbol;  // Stock Ticker (e.g., AAPL, TSLA)

    @NotNull(message = "Price cannot be null")
    @Column(nullable = false)
    private BigDecimal price;  // Price per unit

    @NotNull(message = "Quantity cannot be null")
    @Column(nullable = false)
    private int quantity;  // Number of stocks to buy/sell

    @NotNull(message = "Order type cannot be blank")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType; // BUY or SELL

    private int filledQty; // Number of stocks filled

    // order status
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // PENDING, FILLED, CANCELLED

    private LocalDateTime timestamp = LocalDateTime.now(); // Auto timestamp

    @Column
    private BigDecimal mtm;

    @Column
    private BigDecimal lastMarketPrice;

    public BigDecimal getMtm() {
        return mtm;
    }

    public void setMtm(BigDecimal mtm) {
        this.mtm = mtm;
    }

    public BigDecimal getLastMarketPrice() {
        return lastMarketPrice;
    }

    public void setLastMarketPrice(BigDecimal lastMarketPrice) {
        this.lastMarketPrice = lastMarketPrice;
    }
}
