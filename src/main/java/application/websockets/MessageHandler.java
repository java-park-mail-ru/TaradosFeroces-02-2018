package application.websockets;

import application.models.User;
import application.models.id.Id;

import org.jetbrains.annotations.NotNull;


public abstract class MessageHandler<T extends Message> {
    @NotNull
    private final Class<T> clazz;

    public MessageHandler(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public void handleMessage(@NotNull Message message, @NotNull Id<User> addressee) throws HandleException {
        try {
            handle(clazz.cast(message), addressee);
        } catch (ClassCastException ex) {
            throw new HandleException("Can't read incomming message of type " + message.getClass(), ex);
        }
    }

    public abstract void handle(@NotNull T message, @NotNull Id<User> addressee) throws HandleException;
}
