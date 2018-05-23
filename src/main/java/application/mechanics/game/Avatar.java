package application.mechanics.game;

import application.mechanics.services.MechanicsTimeService;
import application.models.User;
import application.models.id.Id;
import org.jetbrains.annotations.NotNull;

public class Avatar extends GameObject {


    @NotNull
    private final User userProfile;

    public Avatar(@NotNull User userProfile, @NotNull MechanicsTimeService timeService) {
        this.userProfile = userProfile;
        this.addSnapshot(MechanicPart.class, new MechanicPart(timeService));
    }

    @NotNull
    public User getUserProfile() {
        return userProfile;
    }

    @NotNull
    public Id<User> getUserId() {
        return userProfile.getUserId();
    }



    @Override
    public Snap<? extends Snapable> makeSnap() {
        return null;
    }
}
