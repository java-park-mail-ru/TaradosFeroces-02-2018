package application.websockets;

import application.models.User;
import application.models.id.Id;
import org.jetbrains.annotations.NotNull;

public interface MessageHandlerContainer {

    void handle(@NotNull Message message, @NotNull Id<User> addressee) throws HandleException;

    <T extends Message> void registerHandler(@NotNull Class<T> clazz, MessageHandler<T> handler);
}
