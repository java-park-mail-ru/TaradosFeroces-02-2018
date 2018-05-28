package application.mechanics.messages.handlers;


import application.mechanics.MechanicExecutor;
import application.mechanics.messages.in.ClientSnap;
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
public class ClientSnapHandler extends MessageHandler<ClientSnap> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSnapHandler.class);

    @NotNull
    private MechanicExecutor mechanicExecutor;

    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public ClientSnapHandler(@NotNull MechanicExecutor mechanicExecutor,
                             @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(ClientSnap.class);
        this.mechanicExecutor = mechanicExecutor;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(ClientSnap.class, this);
    }

    @Override
    public void handle(@NotNull ClientSnap message, @NotNull Id<User> receiver) {
        LOGGER.info("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ id=" + message.getId()
                + " x=" + message.getXMove()
                + " y=" + message.getYMove()
        );
        mechanicExecutor.addClientSnapshot(receiver, message);
    }
}