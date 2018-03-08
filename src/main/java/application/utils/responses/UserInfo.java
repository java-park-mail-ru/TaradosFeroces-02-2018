package application.utils.responses;

import application.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;


public class UserInfo {

    @NotNull
    @JsonProperty("id")
    private final long id;

    @NotNull
    @JsonProperty("login")
    private String login;

    public UserInfo(User user) {
        id = user.getId();
        login = user.getLogin();
    }
}
