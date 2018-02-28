package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class UserSignOutRequest {

    @NotNull
    private String loginOrEmail;
    @NotNull
    private Boolean isLogin;

    public UserSignOutRequest(@JsonProperty("login_or_email") @NotNull String loginOrEmail,
                              @JsonProperty("is_login") @NotNull Boolean isLogin) {
        this.loginOrEmail = loginOrEmail;
        this.isLogin = isLogin;
    }

    public String getLoginOrEmail() {
        return loginOrEmail;
    }

    public Boolean getLogin() {
        return isLogin;
    }
}
