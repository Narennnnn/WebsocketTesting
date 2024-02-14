package com.websocketTest;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@ServerEndpoint(value = "/websocket")
public class ServerNotifier implements Notifier {
    private static final Logger logger = LoggerFactory.getLogger(ServerNotifier.class);
    private final ScheduledExecutorService pingScheduler = Executors.newScheduledThreadPool(1);

    @OnOpen
    public void onOpen(Session session) {
        logger.info("WebSocket session opened with ID: {}", session.getId());
        // Schedule periodic Ping messages
        pingScheduler.scheduleAtFixedRate(() -> sendPing(session), 5, 5, TimeUnit.SECONDS);
    }


    @OnMessage
    public void onMessage(Session session, String message) {
        logger.info("Received raw message: {}", message);
        String[] parts = message.split(" ");
        if (parts.length == 2) {
            double clientTime = Double.parseDouble(parts[1]);
            double serverTime = System.currentTimeMillis();
            double latency = serverTime - clientTime;
            logger.info("Latency: {} ms", latency);
            sendMessage(session, parts[0], serverTime);
        }
    }



    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("WebSocket session closed with ID: {}. Reason: {}", session.getId(), closeReason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket error in session {}: {}", session.getId(), throwable.getMessage());
    }


    private void sendPing(Session session) {
        try {
            long clientTime = System.currentTimeMillis();
            String pingMessage = "Ping from server at " + clientTime;
            ByteBuffer buffer = ByteBuffer.wrap(pingMessage.getBytes());
            session.getBasicRemote().sendPing(buffer);
        } catch (IOException e) {
            logger.error("Error sending Ping to session {}: {}", session.getId(), e.getMessage());
        }
    }


    @OnMessage
    public void onPong(Session session, PongMessage pongMessage) {
        // write message
    }

    public void sendMessage(Session session, String message, double serverTime) {
        try {
            session.getBasicRemote().sendText(message + " " + serverTime);
        } catch (IOException e) {
            logger.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
        }
    }
}
