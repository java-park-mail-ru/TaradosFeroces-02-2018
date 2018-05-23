package application.utils.responses;

import application.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public class UserPartyView extends UserView {

    @NotNull
    private Long id;

    @NotNull
    @JsonProperty("login")
    private String login;

    @Nullable
    @JsonProperty("avatar")
    private String avatar;


    public UserPartyView(@NotNull User user) {
        super(user);
        this.id = user.getId();
    }

    public UserPartyView(@NotNull Long id, @NotNull String login, @Nullable String avatar) {
        super(login, avatar);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @NotNull
    public UserView makeView() {
        return new UserView(login, avatar);
    }
}
