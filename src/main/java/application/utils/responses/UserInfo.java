package application.utils.responses;

import application.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;


public class UserInfo {

    @NotNull
    @JsonProperty("email")
    private String email;

    @NotNull
    @JsonProperty("login")
    private String login;

    public UserInfo(@NotNull User user) {
        this.email = user.getEmail();
        this.login = user.getLogin();
    }

    public UserInfo(@NotNull String email, @NotNull String login) {
        this.email = email;
        this.login = login;
    }
}
