package application.utils.requests;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public class UserSignUpRequest {

    @NotNull
    private final String login;

    @NotNull
    private final String email;

    @NotNull
    private final String password;

    @Nullable
    private final String name;

    @Nullable
    private final String avatar;

    @JsonCreator
    public UserSignUpRequest(@JsonProperty("login") @NotNull String login,
                             @JsonProperty("email") @NotNull String email,
                             @JsonProperty("password") @NotNull String password,
                             @JsonProperty("name") @Nullable String name,
                             @JsonProperty("avatar") @Nullable String avatar) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
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

    public String getAvatar() {
        return avatar;
    }
}
