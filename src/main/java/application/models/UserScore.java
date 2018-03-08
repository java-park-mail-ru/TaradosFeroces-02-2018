package application.models;

import application.models.id.Id;
import org.jetbrains.annotations.NotNull;

public class UserScore {

    @NotNull
    private final Id<User> userId;

    @NotNull
    private final long points;


    public UserScore(@NotNull Long userId, @NotNull long points) {
        this.userId = new Id<>(userId);
        this.points = points;
    }

    public long getUserId() {
        return userId.asLong();
    }

    public long getPoints() {
        return points;
    }
}
