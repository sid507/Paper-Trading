let socket: WebSocket | null = null;
const listeners: ((data: any) => void)[] = [];

export interface WebSocketConfig {
    userName: string;
}

export const connectWebSocket = (onMessage: (data: any) => void, config: WebSocketConfig) => {
    // Add the listener to our array
    listeners.push(onMessage);

    // If we already have a connection, just return it
    if (socket && socket.readyState === WebSocket.OPEN) {
        return socket;
    }

    // Create new connection if none exists
    socket = new WebSocket('ws://localhost:8080/ws');

    socket.onopen = () => {
        console.log('WebSocket connection established');
        if (socket) {
            setTimeout(() => {
                socket?.send(`join:${config.userName}`);
            }, 1000);
        }
    };

    socket.onmessage = (event) => {
        try {
            const data = JSON.parse(event.data);
            // Notify all listeners
            listeners.forEach(listener => listener(data));
        } catch (e) {
            console.log("Error parsing message: ", e);
        }
    };

    socket.onerror = (error) => {
        console.error('WebSocket error:', error);
    };

    socket.onclose = () => {
        console.log('WebSocket connection closed');
        socket = null;
        // Attempt to reconnect after a delay
        setTimeout(() => {
            if (!socket) {
                connectWebSocket(onMessage, config);
            }
        }, 5000);
    };

    return socket;
};

export const disconnectWebSocket = (onMessage: (data: any) => void) => {
    // Remove the listener
    const index = listeners.indexOf(onMessage);
    if (index > -1) {
        listeners.splice(index, 1);
    }

    // If no more listeners, close the connection
    if (listeners.length === 0 && socket) {
        socket.close();
        socket = null;
    }
};