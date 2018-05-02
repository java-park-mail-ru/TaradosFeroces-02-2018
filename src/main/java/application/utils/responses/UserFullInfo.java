package application.utils.responses;

import application.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class UserFullInfo extends UserInfo {

    @NotNull
    @JsonProperty("name")
    private String name;

    @Nullable
    @JsonProperty("points")
    private long points;
    
    @Nullable
    @JsonProperty("avatar")
    private String avatar;

    public UserFullInfo(User user) {
        super(user);
        name = user.getName();
        avatar = user.getAvatar();
        points = user.getPoints();
    }
}
