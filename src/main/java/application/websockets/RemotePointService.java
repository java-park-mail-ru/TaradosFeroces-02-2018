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
    private static final boolean DEBUG = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(RemotePointService.class);

    private final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RemotePointService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    private void logSessions() {
        if (DEBUG) {
            final Set<Map.Entry<Long, WebSocketSession>> entries = sessions.entrySet();

            LOGGER.info("logSessions: "
                    + entries.stream()
                        .map(idws -> (idws.getKey().toString() + " -> " + idws.getValue().getId()))
                        .reduce("{ ", (lhs, rhs) -> (lhs + "; " + rhs))
                        .concat(" }")
            );

            /*
            for (Map.Entry<Long, WebSocketSession> idWebSocketSessionEntry : entries) {
                LOGGER.info("       " + idWebSocketSessionEntry.getKey()
                        + " -> " + idWebSocketSessionEntry.getValue().getId());
            }
            */

            LOGGER.info("");
        }
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

        LOGGER.info("            : after registerUser: session.size=" + sessions.size());
        this.logSessions();
    }

    public void removeUser(@NotNull Id<User> userId) {
        sessions.remove(userId.asLong());
        this.logSessions();
        LOGGER.info("user session has been removed: " + userId.toString());
    }

    public boolean isConnected(@NotNull Long userId) {

        LOGGER.info("isConnected: session.size=" + sessions.size());
        this.logSessions();
        LOGGER.info("isConnected: sessions.containsKey( " + userId + " ) = " + sessions.containsKey(userId));

        return sessions.containsKey(userId) && sessions.get(userId).isOpen();
    }

    public void interruptConnection(@NotNull Id<User> userId, @NotNull CloseStatus closeStatus) {
        final WebSocketSession webSocketSession = sessions.get(userId.asLong());
        if (webSocketSession != null && webSocketSession.isOpen()) {
            try {
                webSocketSession.close(closeStatus);
            } catch (IOException ignore) {
                LOGGER.warn("interruptConnection: user.id=" + userId.asLong()
                        + ", closeStatus: " + closeStatus.getReason());
            }
        }
    }

    public synchronized void sendMessageToUser(@NotNull Id<User> userId, @NotNull Message message) throws IOException {
        LOGGER.info("sendMessageToUser:");
        LOGGER.info("    : user: " + userId.asLong() + ", msg.class=" + message.getClass().getName());

        final WebSocketSession webSocketSession = sessions.get(userId.asLong());
        if (webSocketSession == null) {
            LOGGER.info("    : there is no session for user.id=" + userId.asLong());

            throw new IOException("No websockets for user " + userId.asLong());
        }

        LOGGER.info("    : find for user.id=" + userId.asLong()
                    + " -> session " + webSocketSession.getId());

        if (!webSocketSession.isOpen()) {
            LOGGER.info("    : session " + webSocketSession.getId() + " is not open");
            throw new IOException("Websockets is closed or not exists: user " + userId);
        }

        LOGGER.info("    : available session " + webSocketSession.getId());

        try {
            final TextMessage textMessage = new TextMessage(objectMapper.writeValueAsString(message));

            LOGGER.info("    : sending message.payload=" + textMessage.getPayload());

            webSocketSession.sendMessage(textMessage);
        } catch (IOException e) {
            throw new IOException("Sending message error: user " + userId + ", error : " + e);
        }
    }

    public synchronized void sendMessageToUserQuiet(@NotNull Id<User> userId, @NotNull Message message) throws IOException {

        final WebSocketSession webSocketSession = sessions.get(userId.asLong());
        if (webSocketSession == null) {
            throw new IOException("No websockets for user " + userId.asLong());
        }
        if (!webSocketSession.isOpen()) {
            throw new IOException("Websockets is closed or not exists: user " + userId);
        }


        try {
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            throw new IOException("Sending message error: user " + userId + ", error : " + e);
        }
    }


}
