package com.tradingplatform.trading_backed.feed;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class UserSessions {
    private final Map<String,WebSocketSession> users = new ConcurrentHashMap<>();

    static UserSessions obj;
    


    // public static UserSessions getInstance() {
    //     if (obj == null) {
    //         obj = new UserSessions();
    //     }
    //     return obj;
    // }

    public void addSession(String userId, WebSocketSession session) {
        users.put(userId, session);
        System.out.println("User session added: " + users.get(userId));
    }
    public void removeSession(String userId) {
        users.remove(userId);
    }
    public WebSocketSession getSession(String userId) {
        return users.get(userId);
    }    

    //get the list of all sessions
    public List<WebSocketSession> getAllSessions() {
        return List.copyOf(users.values());
    }
}
