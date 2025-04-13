import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { connectWebSocket } from '../utils/websocket';
import '../styles/OrderScreen.css';
import { useAuth } from '../context/AuthContext';

interface WebSocketOrder {
    order:{
        id: string | null;
        stockSymbol: string;
        quantity: number;
        price: number;
        orderType: 'BUY' | 'SELL';
        filledQty: number;
        timestamp: string;
        user: {
            id: string | null;
            username: string;
            email: string | null;
            password: string | null;
            subscribedSymbols: string[] | null;
        };
    }
    type: string;
}

interface Order {
    id: string;
    stockSymbol: string;
    quantity: number;
    price: number;
    orderType: 'BUY' | 'SELL';
    status: 'PENDING' | 'FILLED' | 'CANCELLED';
    timestamp: string;
    filledQty: number;
}

const OrderScreen: React.FC<{ jwt: string }> = ({ jwt }) => {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const { userName} = useAuth();

    const handleDeleteAllOrders = async () => {
        try {
            await axios.delete('http://localhost:8080/api/orders/delete', {
                headers: { Authorization: `Bearer ${jwt}` }
            });
            setOrders([]); // Clear orders from state
        } catch (err) {
            setError('Failed to delete orders');
            console.error('Error deleting orders:', err);
        }
    };

    useEffect(() => {
        // Initial fetch of orders
        const fetchOrders = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/orders/user', {
                    headers: { Authorization: `Bearer ${jwt}` }
                });
                setOrders(response.data);
            } catch (err) {
                setError('Failed to fetch orders');
            } finally {
                setLoading(false);
            }
        };

        fetchOrders();

        // WebSocket connection for real-time updates
        const socket = connectWebSocket((data: WebSocketOrder) => {
            if (data.type === 'ORDER_UPDATE') {
                setOrders(prevOrders => {
                    let order = data.order;
                    // Convert WebSocket order to Order format
                    const newOrder: Order = {
                        id: order.id || String(Date.now()), // Generate temporary ID if null
                        stockSymbol: order.stockSymbol,
                        quantity: order.quantity,
                        price: order.price,
                        orderType: order.orderType,
                        status: order.filledQty >= order.quantity ? 'FILLED' : 'PENDING',
                        timestamp: order.timestamp,
                        filledQty: order.filledQty
                    };

                    // Check if order exists
                    const orderIndex = prevOrders.findIndex(prevOrder => 
                        prevOrder.id === newOrder.id
                    );

                    if (orderIndex !== -1) {
                        const updatedOrders = [...prevOrders];
                        updatedOrders[orderIndex] = { ...updatedOrders[orderIndex], ...newOrder };
                        return updatedOrders;
                    }
                    return [newOrder, ...prevOrders];
                });
            }
        },{ userName:userName! });

        return () => socket.close();
    }, [jwt]);

    if (loading) return <div className="order-screen-loading">Loading orders...</div>;
    if (error) return <div className="order-screen-error">{error}</div>;

    return (
        <div className="order-screen">
            <div className="order-header">
                <div className="header-left">
                    <h1>Orders</h1>
                    <div className="order-summary">
                        <span>Total Orders: {orders.length}</span>
                    </div>
                </div>
                <button 
                    className="delete-all-button"
                    onClick={handleDeleteAllOrders}
                    disabled={orders.length === 0}
                >
                    Delete All Orders
                </button>
            </div>

            <div className="order-table-container">
                <table className="order-table">
                    <thead>
                        <tr>
                            <th>Time</th>
                            <th>Symbol</th>
                            <th>Type</th>
                            <th>Price</th>
                            <th>Quantity</th>
                            <th>Filled</th>
                            <th>Status</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((order) => (
                            <tr 
                                key={order.id} 
                                className={`order-row ${order.orderType.toLowerCase()}`}
                            >
                                <td>{new Date(order.timestamp).toLocaleString()}</td>
                                <td>{order.stockSymbol}</td>
                                <td className={`order-type ${order.orderType.toLowerCase()}`}>
                                    {order.orderType}
                                </td>
                                <td>${order.price.toFixed(2)}</td>
                                <td>{order.quantity}</td>
                                <td>{order.filledQty}</td>
                                <td className={`order-status ${order?.status?.toLowerCase()}`}>
                                    {order?.status ?? 'PENDING'}
                                </td>
                                <td>${(order.price * order.quantity).toFixed(2)}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default OrderScreen;