package application.utils.requests;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class UserSignInRequest {

    @NotNull
    private final String login;
    @NotNull
    private final String password;

    public UserSignInRequest(@JsonProperty("login") @NotNull String login,
                             @JsonProperty("password") @NotNull String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
