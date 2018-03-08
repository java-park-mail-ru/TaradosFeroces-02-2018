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

    public UserFullInfo(User user) {
        super(user);
        email = user.getEmail();
        name = user.getName();
    }
}
