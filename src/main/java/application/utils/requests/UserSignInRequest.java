package application.utils.requests;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class UserSignInRequest {

    @NotNull
    private String loginOrEmail;
    @NotNull
    private Boolean isLogin;
    @NotNull
    private String password;

    public UserSignInRequest(@JsonProperty("login_or_email") @NotNull String loginOrEmail,
                             @JsonProperty("is_login") @NotNull Boolean isLogin,
                             @JsonProperty("password") @NotNull String password) {
        this.loginOrEmail = loginOrEmail;
        this.isLogin = isLogin;
        this.password = password;
    }

    public String getLoginOrEmail() {
        return loginOrEmail;
    }

    public Boolean getIsLogin() {
        return isLogin;
    }

    public String getPassword() {
        return password;
    }
}
