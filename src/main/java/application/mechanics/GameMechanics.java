package application.mechanics;

import application.mechanics.messages.in.ClientSnap;
import application.models.User;
import application.models.id.Id;
import org.jetbrains.annotations.NotNull;


public interface GameMechanics {

    void addClientSnapshot(@NotNull Id<User> userId, @NotNull ClientSnap clientSnap);

    void interruptGameWithUser(@NotNull Id<User> userId);

    void gmStep(long frameTime);

    void reset();
}