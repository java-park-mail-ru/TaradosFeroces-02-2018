package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class UserSignOutRequest {

    @NotNull
    private String login;

    public UserSignOutRequest(@JsonProperty("login") @NotNull String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
