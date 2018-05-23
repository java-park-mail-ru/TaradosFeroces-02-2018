package application.websockets;


import application.models.User;
import application.models.id.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class RemotePointService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemotePointService.class);

    private final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RemotePointService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private void logSessions() {
        final Set<Map.Entry<Long, WebSocketSession>> entries = sessions.entrySet();

        LOGGER.info("logSessions: -----------------------------");
        for (Map.Entry<Long, WebSocketSession> idWebSocketSessionEntry: entries) {
            LOGGER.info("       " + idWebSocketSessionEntry.getKey()
                    + " -> " + idWebSocketSessionEntry.getValue().getId());
        }
        LOGGER.info("");
    }

    @PostConstruct
    public void init() {
        LOGGER.info("created RemotePointService object");
        this.logSessions();
    }

    public void registerUser(@NotNull Id<User> userId, @NotNull WebSocketSession webSocketSession) {
        LOGGER.info("registerUser: before registerUser: session.size=" + sessions.size());
        this.logSessions();

        sessions.put(userId.asLong(), webSocketSession);

        LOGGER.info("registerUser: after registerUser: session.size=" + sessions.size());
        this.logSessions();

        LOGGER.info("add user session: " + userId.asLong() + " -> " + webSocketSession.getId());
        LOGGER.info("registerUser: sessions( " + userId.asLong() + " ) -> " + sessions.getOrDefault(userId, null));
    }

    public void removeUser(@NotNull Id<User> userId) {
        sessions.remove(userId.asLong());
        this.logSessions();
        LOGGER.info("user session has been removed: " + userId.toString());
    }

    public boolean isConnected(@NotNull Id<User> userId) {
        LOGGER.info("isConnected: session.size=" + sessions.size());
        this.logSessions();
        LOGGER.info("isConnected: sessions.containsKey( " + userId.asLong() + " ) = " + sessions.containsKey(userId.asLong()));
        return sessions.containsKey(userId.asLong()) && sessions.get(userId.asLong()).isOpen();
    }

    public void interruptConnection(@NotNull Id<User> userId, @NotNull CloseStatus closeStatus) {
        final WebSocketSession webSocketSession = sessions.get(userId.asLong());
        if (webSocketSession != null && webSocketSession.isOpen()) {
            try {
                webSocketSession.close(closeStatus);
            } catch (IOException ignore) {
                System.out.println("42");
            }
        }
    }

    public void sendMessageToUser(@NotNull Id<User> userId, @NotNull Message message) throws IOException {
        LOGGER.info("sendMessageToUser: user: " + userId.toString() + ", msg: " + message.getClass().getName());

        final WebSocketSession webSocketSession = sessions.get(userId.asLong());
        if (webSocketSession == null) {
            LOGGER.info("sendMessageToUser: there is no session for user " + userId.toString());
            throw new IOException("No websockets for user " + userId.asLong());
        }
        LOGGER.info("sendMessageToUser: find for user " + userId.toString()
                    + " -> session " + webSocketSession.getId());

        if (!webSocketSession.isOpen()) {
            LOGGER.info("sendMessageToUser: session " + webSocketSession.getId() + " is not open");
            throw new IOException("Websockets is closed or not exists: user " + userId);
        }
        LOGGER.info("sendMessageToUser: available session " + webSocketSession.getId());

        try {
            LOGGER.info("sendMessageToUser: sending message: " + new TextMessage(objectMapper.writeValueAsString(message)));
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            throw new IOException("Sending message error: user " + userId + ", error : " + e);
        }
    }


}
