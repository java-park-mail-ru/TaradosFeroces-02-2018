package application.utils.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;


public class UserInfo {

    @NotNull
    @JsonProperty("login")
    private String login;

    public UserInfo(String login) {
        this.login = login;
    }
}
