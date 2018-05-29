package application.utils.responses;

import application.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class UserView {

    @NotNull
    @JsonIgnore
    private Long id;

    @NotNull
    @JsonProperty("login")
    private String login;

    @Nullable
    @JsonProperty("avatar")
    private String avatar;

    @Nullable
    @JsonProperty("online")
    private boolean online;

    public UserView(@NotNull User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.avatar = user.getAvatar();
        this.online = false;
    }

    public UserView(@NotNull Long id, @NotNull String login, @Nullable String avatar) {
        this.id = id;
        this.login = login;
        this.avatar = avatar;
        this.online = false;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setOnline(boolean status) {
        this.online = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        UserView that = (UserView) object;
        return Objects.equals(id, that.id) && Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }
}
