package application.party.messages.out;

import application.models.User;
import application.websockets.Message;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AskForFriendship extends Message {

    @NotNull
    @JsonProperty("login")
    private final String login;

    @Nullable
    @JsonProperty("avatar")
    private final String avatar;

    @NotNull
    @JsonProperty("request_id")
    private final Long requestId;

    public AskForFriendship(@NotNull User user, @NotNull Long requestId) {
        this.login = user.getLogin();
        this.avatar = user.getAvatar();
        this.requestId = requestId;
    }
}