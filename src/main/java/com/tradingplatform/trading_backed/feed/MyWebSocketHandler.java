package com.tradingplatform.trading_backed.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private UserSessions userSessions;


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received: " + payload);
        // Example: Parse the message to determine the action (join, leave, or broadcast)
        String[] parts = payload.split(":", 2);
        String action = parts[0];
        String userName = parts.length > 1 ? parts[1] : "";

        switch (action) {
            case "join":
                joinRoom(userName, session);
                break;
            case "leave":
                leaveRoom(userName, session);
                break;
            case "broadcast":
                sendMsgToUser(userName, "Message to room: " + userName);
                break;
            default:
                session.sendMessage(new TextMessage("Unknown action: " + action));
        }
    }

    private void joinRoom(String username, WebSocketSession session) throws Exception {
        userSessions.addSession(username, session);        
        session.sendMessage(new TextMessage("User: " + username+" joined the room"));
    }

    private void leaveRoom(String roomName, WebSocketSession session) throws Exception {
        // users.remove(roomName);
        userSessions.removeSession(roomName);
        session.sendMessage(new TextMessage("Left room: " + roomName));
    }

    public void sendMsgToUser(String username, String message) throws Exception {
        WebSocketSession session = this.userSessions.getSession(username);
        session.sendMessage(new TextMessage(message));
                
        }
    

    //
}