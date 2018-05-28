package application.mechanics.messages.handlers;

import application.mechanics.messages.in.JoinGame;
import application.mechanics.services.GameInitService;
import application.models.User;
import application.models.id.Id;
import application.websockets.MessageHandler;
import application.websockets.MessageHandlerContainer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class JoinGameHandler extends MessageHandler<JoinGame> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinGameHandler.class);


    @NotNull
    private final GameInitService gameInitService;

    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public JoinGameHandler(@NotNull GameInitService gameInitService,
                           @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(JoinGame.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.gameInitService = gameInitService;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("Registration handler");
        messageHandlerContainer.registerHandler(JoinGame.class, this);
    }

    @Override
    public void handle(@NotNull JoinGame message, @NotNull Id<User> receiver) {
        LOGGER.info("handle: message: " + message.toString());
        LOGGER.info("      : sender.login= " + message.getLogin());

        gameInitService.addConfirmation(receiver);
    }
}
