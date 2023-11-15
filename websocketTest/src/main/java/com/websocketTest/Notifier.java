package com.websocketTest;


import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

public interface Notifier {
    void onOpen(Session session);
    void onMessage(Session session, String message);
    void onClose(Session session, CloseReason closeReason);
    void onError(Session session, Throwable throwable);
}



