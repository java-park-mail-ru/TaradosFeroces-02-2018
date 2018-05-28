package application.mechanics.messages.handlers;


import application.mechanics.messages.in.GameReady;

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
public class GameReadyHandler extends MessageHandler<GameReady> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameReadyHandler.class);


    @NotNull
    private final GameInitService gameInitService;

    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public GameReadyHandler(@NotNull GameInitService gameInitService,
                           @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(GameReady.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.gameInitService = gameInitService;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("Registration handler");
        messageHandlerContainer.registerHandler(GameReady.class, this);
    }

    @Override
    public void handle(@NotNull GameReady message, @NotNull Id<User> receiver) {
        LOGGER.info("handle: message: " + message.toString());
        LOGGER.info("      : isReady : " + message.isReady());

        gameInitService.addReadiness(receiver);
    }
}
