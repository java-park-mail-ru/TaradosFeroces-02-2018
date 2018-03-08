package application.utils.responses;

import application.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserFullInfo {

    @NotNull
    @JsonProperty("id")
    private final long id;

    @NotNull
    @JsonProperty("login")
    private String login;

    @NotNull
    @JsonProperty("email")
    private String email;

    @Nullable
    @JsonProperty("name")
    private String name;

    public UserFullInfo(User user) {
        id = user.getId();
        login = user.getLogin();
        email = user.getEmail();
        name = user.getName();
    }
}
