package application.mechanics.messages.out;


import application.mechanics.game.Scene;
import application.mechanics.game.Avatar;
import application.websockets.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;


public class InitGame extends Message {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitGame.class);

    @NotNull
    @JsonProperty("users")
    private final ArrayList<Avatar.InitialAvatarSnap> users;

    @NotNull
    @JsonProperty("scene")
    private final Scene scene;


    public InitGame(@NotNull ArrayList<Avatar.InitialAvatarSnap> users, @NotNull Scene scene) {
        this.users = users;
        this.scene = scene;
        LOGGER.info("---Construct InitGame: users.size=" + users.size());
    }

    public int getUsersCount() {
        return users.size();
    }
}
