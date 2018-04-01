package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



public class UserUpdateRequest {
    @NotNull
    private final String login;

    @NotNull
    private final String email;

    @Nullable
    private final String avatar;

    public UserUpdateRequest(@JsonProperty("login") @NotNull String login,
                             @JsonProperty("email") @NotNull String email,
                             @JsonProperty("avatar") @Nullable String avatar) {
        this.login = login;
        this.email = email;
        this.avatar = avatar;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }
}
