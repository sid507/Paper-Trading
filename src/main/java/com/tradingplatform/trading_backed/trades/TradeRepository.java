package com.tradingplatform.trading_backed.trades;

import com.tradingplatform.trading_backed.orders.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
}
