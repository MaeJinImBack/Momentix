package com.example.momentix.domain.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
@Component
public class QueueWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        sessions.put(session.getId(), session);
        String query = session.getUri().getQuery();
        String sessionId = null;
        if (query != null && query.contains("sessionId=")) {
            sessionId = query.split("sessionId=")[1];
        }
        if (sessionId != null){
            sessions.put(sessionId, session);
        }
        log.info("새로운 웹소켓 연결 : {}, {}", session.getId(), sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("메시지 수신 : {}", payload);

        for (WebSocketSession webSocketSession : sessions.values()) {
            webSocketSession.sendMessage(new TextMessage("서버에서 메시지 보냄 : " + payload));
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }

    public void sendMessage(String sessionId, String message) throws IOException {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }

    }
}
