package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class UserUpdateRequest {
    @NotNull
    private final String login;

    @NotNull
    private final Map<String, String> data;


    public UserUpdateRequest(@JsonProperty("login") @NotNull String login,
                             @JsonProperty("data") @NotNull Map<String, String> data) {
        this.login = login;
        this.data = data;
    }

    public String getLogin() {
        return login;
    }

    public Map<String, String> getData() {
        return data;
    }
}
