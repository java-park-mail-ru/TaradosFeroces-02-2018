package application.mechanics.messages.handlers;


import application.mechanics.MechanicExecutor;
import application.mechanics.messages.in.InterruptGame;
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
public class InterruptGameHandler extends MessageHandler<InterruptGame> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterruptGameHandler.class);


    @NotNull
    private final MechanicExecutor mechanicExecutor;

    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public InterruptGameHandler(@NotNull MechanicExecutor mechanicExecutor,
                                @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(InterruptGame.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.mechanicExecutor = mechanicExecutor;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("Registration handler");
        messageHandlerContainer.registerHandler(InterruptGame.class, this);
    }

    @Override
    public void handle(@NotNull InterruptGame message, @NotNull Id<User> receiver) {
        LOGGER.info("handle: InterruptGame message: " + message.toString());
        LOGGER.info("      : user.id=" + receiver.asLong());

        mechanicExecutor.interruptGameWithUser(receiver);
    }
}


