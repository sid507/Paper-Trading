import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { connectWebSocket } from '../utils/websocket';
import '../styles/MarketFeedScreen.css';
import OrderScreen from './OrderScreen';
import { ArrowUpIcon, ArrowDownIcon } from '@heroicons/react/24/solid';
import TradeModal from '../components/TradeModal';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';


interface MarketFeedType {
    type: String;
    data: MarketFeed;
}

interface MarketFeed {
    symbol: string;
    price: number;
    volume: number;
    timestamp: number;
    change?: number;
}

interface TradeDetails {
    symbol: string;
    orderType: 'BUY' | 'SELL';
    currentPrice: number;
}

const MarketFeedScreen: React.FC = () => {
    const { jwt, logout, userName } = useAuth();
    const [marketFeed, setMarketFeed] = useState<MarketFeed[]>([]);
    const [tradeDetails, setTradeDetails] = useState<TradeDetails | null>(null);
    const navigate = useNavigate();


    useEffect(() => {
        const socket = connectWebSocket((marketData: MarketFeedType) => {
            if (marketData.type !== 'MARKET_FEED') return;
            let data = marketData.data;
            setMarketFeed((prevFeed) => {
                const existingFeedIndex = prevFeed.findIndex((feed) => feed.symbol === data.symbol);
                if (existingFeedIndex !== -1) {
                    const updatedFeed = [...prevFeed];
                    const previousPrice = updatedFeed[existingFeedIndex].price;
                    updatedFeed[existingFeedIndex] = {
                        ...updatedFeed[existingFeedIndex],
                        price: data.price,
                        volume: data.volume,
                        timestamp: data.timestamp,
                        change: ((data.price - previousPrice) / previousPrice) * 100,
                    };
                    return updatedFeed;
                }
                return [...prevFeed, { ...data, change: 0 }];
            });
        },{userName:userName!});

        return () => socket.close();
    }, []);

    const handleTrade = async (symbol: string, action: 'BUY' | 'SELL', quantity: number, price: number) => {
        try {
            const response = await axios.post(
                'http://localhost:8080/api/orders/place',
                {
                    stockSymbol: symbol,
                    price: price,
                    quantity: quantity,
                    orderType: action,
                    user: { username: userName },
                },
                { headers: { Authorization: `Bearer ${jwt}` } }
            );
            console.log(`${action} order placed:`, response.data);
            setTradeDetails(null); // Close modal after successful order
        } catch (error) {
            if (axios.isAxiosError(error) && error.response?.status === 401) {
                logout(); // Handle expired tokens
                navigate('/login');
            }
            console.error(`Error placing ${action} order:`, error);
        }
    };

    return (
        <div className="market-screen">
            {/* Market Feed Section */}
            <div className="market-section">
                <div className="market-header">
                    <h1>Market Watch</h1>
                    <div className="market-stats">
                        <span className="stat">
                            Active Symbols: {marketFeed.length}
                        </span>
                    </div>
                </div>

                <div className="market-table-container">
                    <table className="market-table">
                        <thead>
                            <tr>
                                <th>Symbol</th>
                                <th>Last Price</th>
                                <th>24h Change</th>
                                <th>Volume</th>
                                <th>Trade</th>
                            </tr>
                        </thead>
                        <tbody>
                            {marketFeed.map((feed) => (
                                <tr key={feed.symbol} className="market-row">
                                    <td className="symbol-cell">
                                        <span className="symbol-text">{feed.symbol}</span>
                                    </td>
                                    <td className="price-cell">
                                        ${feed.price.toFixed(2)}
                                    </td>
                                    <td className={`change-cell ${feed.change && feed.change > 0 ? 'positive' : 'negative'}`}>
                                        {feed.change && feed.change > 0 ? <ArrowUpIcon className="change-icon" /> : <ArrowDownIcon className="change-icon" />}
                                        {Math.abs(feed.change || 0).toFixed(2)}%
                                    </td>
                                    <td className="volume-cell">
                                        {Number(feed.volume).toLocaleString()}
                                    </td>
                                    <td className="action-cell">
                                        <div className="action-buttons">
                                            <button
                                                className="trade-btn buy"
                                                onClick={() => setTradeDetails({
                                                    symbol: feed.symbol,
                                                    orderType: 'BUY',
                                                    currentPrice: feed.price
                                                })}
                                            >
                                                Buy
                                            </button>
                                            <button
                                                className="trade-btn sell"
                                                onClick={() => setTradeDetails({
                                                    symbol: feed.symbol,
                                                    orderType: 'SELL',
                                                    currentPrice: feed.price
                                                })}
                                            >
                                                Sell
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Orders Section */}
            <div className="orders-wrapper">
                <OrderScreen jwt={jwt!} />
            </div>

            {tradeDetails && (
                <TradeModal
                    symbol={tradeDetails.symbol}
                    currentPrice={tradeDetails.currentPrice}
                    orderType={tradeDetails.orderType}
                    onConfirm={(quantity, price) =>
                        handleTrade(tradeDetails.symbol, tradeDetails.orderType, quantity, price)
                    }
                    onClose={() => setTradeDetails(null)}
                />
            )}
        </div>
    );
};

export default MarketFeedScreen;