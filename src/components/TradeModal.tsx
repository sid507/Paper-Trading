import React, { useState } from 'react';
import '../styles/TradeModal.css';

interface TradeModalProps {
    symbol: string;
    currentPrice: number;
    orderType: 'BUY' | 'SELL';
    onConfirm: (quantity: number, price: number) => void;
    onClose: () => void;
}

const TradeModal: React.FC<TradeModalProps> = ({
    symbol,
    currentPrice,
    orderType,
    onConfirm,
    onClose
}) => {
    const [quantity, setQuantity] = useState(1);
    const [price, setPrice] = useState(currentPrice);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onConfirm(quantity, price);
    };

    return (
        <div className="trade-modal-overlay">
            <div className="trade-modal">
                <div className="trade-modal-header">
                    <h2>{orderType} {symbol}</h2>
                    <button className="close-button" onClick={onClose}>&times;</button>
                </div>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="quantity">Quantity:</label>
                        <input
                            type="number"
                            id="quantity"
                            min="1"
                            value={quantity}
                            onChange={(e) => setQuantity(Number(e.target.value))}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="price">Price:</label>
                        <input
                            type="number"
                            id="price"
                            step="0.01"
                            min="0.01"
                            value={price}
                            onChange={(e) => setPrice(Number(e.target.value))}
                            required
                        />
                    </div>
                    <div className="order-summary">
                        <p>Total Value: ${(quantity * price).toFixed(2)}</p>
                    </div>
                    <div className="modal-actions">
                        <button type="button" onClick={onClose} className="cancel-button">
                            Cancel
                        </button>
                        <button type="submit" className={`confirm-button ${orderType.toLowerCase()}`}>
                            Confirm {orderType}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default TradeModal;