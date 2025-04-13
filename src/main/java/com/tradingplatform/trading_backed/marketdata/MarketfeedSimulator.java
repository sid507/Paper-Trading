package com.tradingplatform.trading_backed.marketdata;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingplatform.trading_backed.feed.MyWebSocketHandler;
import com.tradingplatform.trading_backed.orders.OrderService;


@Component
public class MarketfeedSimulator {

    @Autowired
    private MyWebSocketHandler myWebSocketHandler;

    @Autowired
    private OrderService orderService;

    private final Random random = new Random();
    private BigDecimal lastPrice = BigDecimal.valueOf(100.0); // Initial price
    private static final BigDecimal MAX_PRICE_CHANGE_PERCENT = BigDecimal.valueOf(0.002); // 0.2% max change
    
    public MarketfeedSimulator() {
        // Default constructor
        //create a function which gets called every second
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(10000);
                        MarketFeed marketFeed = getMarketFeed("AAPL");
                        orderService.updateMTM(marketFeed.symbol, marketFeed.getPrice());

                        ObjectMapper objectMapper = new ObjectMapper();
                        String marketFeedJson = objectMapper.writeValueAsString(marketFeed);
                        //add type
                        marketFeedJson = "{\"type\":\"MARKET_FEED\",\"data\":" + marketFeedJson + "}";
                        myWebSocketHandler.broadcastToRoom(marketFeedJson);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    // Method to simulate market feed for a given symbol
    public MarketFeed getMarketFeed(String symbol) {
        MarketFeed marketFeed = new MarketFeed();
        marketFeed.setSymbol(symbol);
        marketFeed.setPrice(generateRandomPrice());
        marketFeed.setVolume(generateRandomVolume());
        marketFeed.setTimestamp(System.currentTimeMillis());
        return marketFeed;
    }

    // Updated method to generate price with minimal difference
    private BigDecimal generateRandomPrice() {
        // Calculate maximum price change (0.2% of last price)
        BigDecimal maxChange = lastPrice.multiply(MAX_PRICE_CHANGE_PERCENT);
        
        // Generate random change between -maxChange and +maxChange
        double changePercent = -1.0 + (2.0 * random.nextDouble()); // Random value between -1 and 1
        BigDecimal change = maxChange.multiply(BigDecimal.valueOf(changePercent));
        
        // Calculate new price
        lastPrice = lastPrice.add(change).setScale(2, BigDecimal.ROUND_HALF_UP);
        
        // Ensure price stays within reasonable bounds (e.g., 50-500)
        if (lastPrice.compareTo(BigDecimal.valueOf(50.0)) < 0) {
            lastPrice = BigDecimal.valueOf(50.0);
        } else if (lastPrice.compareTo(BigDecimal.valueOf(500.0)) > 0) {
            lastPrice = BigDecimal.valueOf(500.0);
        }
        
        return lastPrice;
    }

    // Updated method to generate more realistic volume
    private int generateRandomVolume() {
        // Generate volume with normal distribution
        double mean = 500.0; // Average volume
        double stdDev = 100.0; // Standard deviation
        int volume = (int) Math.round(random.nextGaussian() * stdDev + mean);
        return Math.max(1, Math.min(1000, volume)); // Clamp between 1 and 1000
    }

    // Inner class to represent the market feed
    public static class MarketFeed {
        private String symbol;
        private BigDecimal price;
        private int volume;
        private long timestamp;

        // Getters and setters
        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "MarketFeed{" +
                    "symbol='" + symbol + '\'' +
                    ", price=" + price +
                    ", volume=" + volume +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}