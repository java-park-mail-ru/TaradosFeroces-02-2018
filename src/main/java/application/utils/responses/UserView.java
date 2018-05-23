package application.utils.responses;

import application.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserView {

    @NotNull
    @JsonProperty("login")
    private String login;

    @Nullable
    @JsonProperty("avatar")
    private String avatar;


    public UserView(@NotNull User user) {
        this.login = user.getLogin();
        this.avatar = user.getAvatar();
    }

    public UserView(@NotNull String login, @Nullable String avatar) {
        this.login = login;
        this.avatar = avatar;
    }

    public String getLogin() {
        return login;
    }

    public String getAvatar() {
        return avatar;
    }
}
