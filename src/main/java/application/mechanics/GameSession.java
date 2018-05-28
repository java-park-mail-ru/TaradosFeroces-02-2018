package application.mechanics;


import application.mechanics.game.Scene;
import application.mechanics.game.Avatar;
import application.mechanics.services.GameSessionService;
import application.mechanics.services.MechanicsTimeService;
import application.models.id.Id;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class GameSession {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private boolean isFinished;

    @NotNull
    private final Id<GameSession> sessionId;

    @NotNull
    private final ArrayList<Avatar> users;

    @NotNull
    private Scene scene;

    @NotNull
    private final MechanicsTimeService timeService;

    @NotNull
    private final GameSessionService gameSessionService;


    public GameSession(@NotNull ArrayList<Avatar> users,
                       @NotNull Scene scene,
                       @NotNull MechanicsTimeService timeService,
                       @NotNull GameSessionService gameSessionService) {
        this.sessionId = Id.of(ID_GENERATOR.incrementAndGet());
        this.timeService = timeService;
        this.scene = scene;
        this.users = users;
        this.gameSessionService = gameSessionService;
    }

    @NotNull
    public Id<GameSession> getSessionId() {
        return sessionId;
    }

    public ArrayList<Avatar> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object subj) {
        if (this == subj) {
            return true;
        }
        if (subj == null || getClass() != subj.getClass()) {
            return false;
        }
        final GameSession another = (GameSession) subj;
        return sessionId.equals(another.sessionId);
    }


    public void interrupt() {
        gameSessionService.forceTerminate(this);
    }

    @NotNull
    public Scene getScene() {
        return scene;
    }

    @NotNull
    public MechanicsTimeService getTimeService() {
        return timeService;
    }

    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }

    @Override
    public String toString() {
        return '['
                + "sessionId=" + sessionId
                + ", users=[" + users.stream()
                                .map(Avatar::toString).reduce(" ", (first, second) -> first + second)
                + " ]";
    }
}
