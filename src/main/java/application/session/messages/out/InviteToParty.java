package application.session.messages.out;

import application.models.User;
import application.websockets.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InviteToParty extends Message {

    @NotNull
    @JsonProperty("leader")
    private final String leader;

    @Nullable
    @JsonProperty("avatar")
    private final String avatar;


    public InviteToParty(@NotNull User user) {
        this.leader = user.getLogin();
        this.avatar = user.getAvatar();
    }

}
