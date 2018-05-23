package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class AddFriend {

    @NotNull
    private final String login;

    public AddFriend(@JsonProperty("login") @NotNull String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
