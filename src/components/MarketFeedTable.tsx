import React from 'react';
import TradeActions from './TradeActions';
import { MarketFeed } from '../types/MarketFeed';

interface MarketFeedTableProps {
    marketData: MarketFeed[];
    onTrade: (symbol: string, action: 'buy' | 'sell') => void;
}

const MarketFeedTable: React.FC<MarketFeedTableProps> = ({ marketData, onTrade }) => {
    const renderRows = () => {
        return marketData.map((feed) => (
            <tr key={feed.symbol}>
                <td>{feed.symbol}</td>
                <td>{feed.price.toFixed(2)}</td>
                <td>{feed.volume}</td>
                <td>{new Date(feed.timestamp).toLocaleString()}</td>
                <td>
                    <TradeActions 
                    onBuy={() => onTrade(feed.symbol, 'buy')}
                    onSell={() => onTrade(feed.symbol, 'sell')}
                    selectedMarketFeed={feed}
                    />
                </td>
            </tr>
        ));
    };

    return (
        <table>
            <thead>
                <tr>
                    <th>Symbol</th>
                    <th>Price</th>
                    <th>Volume</th>
                    <th>Timestamp</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                {renderRows()}
            </tbody>
        </table>
    );
};

export default MarketFeedTable;