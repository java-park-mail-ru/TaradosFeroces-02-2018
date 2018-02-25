package application.utils.requests;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public class UserSignInRequest {

    @NotNull
    private final String login;

    @NotNull
    private final String email;

    @NotNull
    private final String password;

    @Nullable
    private final String name;

    @JsonCreator
    public UserSignInRequest(@JsonProperty("login") @NotNull String login,
                             @JsonProperty("name") @Nullable String name,
                             @JsonProperty("email") @NotNull String email,
                             @JsonProperty("password") @NotNull String password) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    @NotNull
    public String getPassword() {
        return password;
    }
}
