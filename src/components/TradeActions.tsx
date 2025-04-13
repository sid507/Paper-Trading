import React from 'react';
import { MarketFeed } from '../types/MarketFeed';

interface TradeActionsProps {
    selectedMarketFeed: MarketFeed;
    onBuy: (symbol: string, amount: number) => void;
    onSell: (symbol: string, amount: number) => void;
}

const TradeActions: React.FC<TradeActionsProps> = ({ selectedMarketFeed, onBuy, onSell }) => {
    const handleBuy = () => {
        const amount = prompt(`Enter the amount of shares to buy for ${selectedMarketFeed.symbol}:`);
        if (amount) {
            onBuy(selectedMarketFeed.symbol, parseInt(amount, 10));
        }
    };

    const handleSell = () => {
        const amount = prompt(`Enter the amount of shares to sell for ${selectedMarketFeed.symbol}:`);
        if (amount) {
            onSell(selectedMarketFeed.symbol, parseInt(amount, 10));
        }
    };

    return (
        <div className="trade-actions">
            <button onClick={handleBuy}>Buy</button>
            <button onClick={handleSell}>Sell</button>
        </div>
    );
};

export default TradeActions;