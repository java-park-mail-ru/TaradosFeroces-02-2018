package application.mechanics.messages.out;

import application.mechanics.game.Avatar;
import application.websockets.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class EndGame extends Message {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndGame.class);

    @NotNull
    @JsonProperty("users")
    private final ArrayList<Avatar.EndAvatarSnap> users;

    public EndGame(@NotNull ArrayList<Avatar.EndAvatarSnap> users) {
        this.users = users;
    }
}
