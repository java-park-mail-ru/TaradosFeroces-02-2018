package application.utils.responses;

import application.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class UserFullInfo extends UserInfo {

    @NotNull
    @JsonProperty("email")
    private String email;

    @Nullable
    @JsonProperty("name")
    private String name;
    
    @Nullable
    @JsonProperty("avatar")
    private String avatar;

    public UserFullInfo(User user) {
        super(user.getLogin());
        email = user.getEmail();
        name = user.getName();
        avatar = user.getAvatar();
    }
}
