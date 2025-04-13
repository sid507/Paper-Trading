import axios from 'axios';

const API_URL = 'https://api.example.com/market-feed'; // Replace with your actual API endpoint

export const fetchMarketFeed = async () => {
    try {
        const response = await axios.get(API_URL);
        return response.data;
    } catch (error) {
        console.error('Error fetching market feed data:', error);
        throw error;
    }
};