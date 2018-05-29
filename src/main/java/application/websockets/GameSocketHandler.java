package application.websockets;

import application.models.User;
import application.models.id.Id;
import application.services.AccountService;
import application.controllers.UserController;

import application.party.messages.out.Ping;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class GameSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSocketHandler.class);
    private static final CloseStatus ACCESS_DENIED = new CloseStatus(4500, "Not logged in. Access denied.");
    private static final String USER_ID = UserController.USER_ID;

    @NotNull
    private final AccountService accountService;

    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    @NotNull
    private final RemotePointService remotePointService;

    private final ObjectMapper objectMapper;

    public GameSocketHandler(@NotNull AccountService accountService,
                             @NotNull MessageHandlerContainer messageHandlerContainer,
                             @NotNull RemotePointService remotePointService,
                             ObjectMapper objectMapper) {
        LOGGER.info("creating GameSocketHandler object");

        this.accountService = accountService;
        this.messageHandlerContainer = messageHandlerContainer;
        this.remotePointService = remotePointService;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("SameParameterValue")
    private void closeSession(@NotNull WebSocketSession webSocketSession, CloseStatus closeStatus) {
        final CloseStatus status = closeStatus == null ? CloseStatus.SERVER_ERROR : closeStatus;

        try {
            LOGGER.warn("closeSession: wsSession: " + webSocketSession.getId());
            webSocketSession.close(status);
        } catch (IOException ignore) {
            LOGGER.warn("closeSession: ignored error ");
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        final Long id = (Long) webSocketSession.getAttributes().get(USER_ID);
        LOGGER.info("user: " + id + ", ws: " + webSocketSession.getId());

        if (id == null || accountService.getUserById(id) == null) {
            LOGGER.warn("User requested websocket has not registered or logged in. Websocket opening has been denied.");
            closeSession(webSocketSession, ACCESS_DENIED);
            return;
        }

        LOGGER.info("afterConnectionEstablished: connection is established: user.id=" + id
                + " -> ws.id=" + webSocketSession.getId());

        remotePointService.registerUser(Id.of(id), webSocketSession);
        remotePointService.isConnected(id);
        try {
            remotePointService.sendMessageToUser(Id.of(id), new Ping("Hello, my dear user!"));
        } catch (IOException e) {
            LOGGER.warn("afterConnectionEstablished: IOException: " + e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession webSocketSession, TextMessage message) {
        LOGGER.info("");
        LOGGER.info("handleTextMessage: -----------------------");
        LOGGER.info("                 : ws_Session.id=" + webSocketSession.getId());


        if (!webSocketSession.isOpen()) {
            LOGGER.info("                 : webSocketSession is not opened");
            return;
        }

        LOGGER.info("                 : ws.id=" + webSocketSession.getId());

        final Long id = (Long) webSocketSession.getAttributes().get(USER_ID);
        final User user;

        if (id == null) {
            LOGGER.info("                 : userId is null");
            return;
        } else {
            user = accountService.getUserById(id);
            if (user == null) {
                LOGGER.info("                 : there is no user with id " + id.toString());
                closeSession(webSocketSession, ACCESS_DENIED);
                return;
            }
            LOGGER.info("                 : user.login=" + user.getLogin());
        }

        LOGGER.info("                 : handling message.payload=" + message.getPayload());
        handleMessage(user, message);
    }

    private void handleMessage(User user, TextMessage text) {
        LOGGER.info("");
        LOGGER.info("handleMessage: -----------------------");

        final Message message;
        try {
            message = objectMapper.readValue(text.getPayload(), Message.class);
            LOGGER.info("             : message: " + message);
        } catch (IOException ex) {
            LOGGER.error("wrong json format at game response", ex);
            return;
        }

        try {
            //noinspection ConstantConditions
            LOGGER.info("             : handling message: " + message);
            messageHandlerContainer.handle(message, user.getUserId());
        } catch (HandleException e) {
            LOGGER.error("Can't handle message of type " + message.getClass().getName() + " with content: " + text, e);
        }
    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        LOGGER.warn("Websocket transport problem", throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        LOGGER.info("afterConnectionClosed: " + webSocketSession.getId());

        final Long userId = (Long) webSocketSession.getAttributes().get(USER_ID);
        if (userId == null) {
            LOGGER.warn("User disconnected but his session was not found (closeStatus=" + closeStatus + ')');
            return;
        }

        LOGGER.info("      : removing user.id=" + userId);
        remotePointService.removeUser(Id.of(userId));
        LOGGER.info("      : user has been removed (.id=" + userId.toString() + ")");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

