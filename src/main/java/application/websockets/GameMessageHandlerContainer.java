package application.websockets;

import application.models.User;
import application.models.id.Id;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


@Service
public class GameMessageHandlerContainer implements MessageHandlerContainer {

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMessageHandlerContainer.class);

    private final Map<Class<?>, MessageHandler<?>> handlersMap = new HashMap<>();


    @PostConstruct
    private void init() {
        LOGGER.info("created GameMessageContainer object");
    }

    @Override
    public void handle(@NotNull Message message, @NotNull Id<User> addressee) throws HandleException {
        final MessageHandler<?> messageHandler = handlersMap.get(message.getClass());

        if (messageHandler == null) {
            throw new HandleException("There is no handlers for messages of following type: " + message.getClass().getName());
        }

        messageHandler.handleMessage(message, addressee);
        LOGGER.trace("Message handled: type =[" + message.getClass().getName() + "]");
    }

    @Override
    public <T extends Message> void registerHandler(@NotNull Class<T> clazz, MessageHandler<T> handler) {
        LOGGER.info("add handler: " + clazz.getName() + " -> " + handler.getClass().getName());
        handlersMap.put(clazz, handler);
    }
}
