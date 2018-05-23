package application.mechanics.game;

import application.mechanics.services.MechanicsTimeService;
import org.jetbrains.annotations.NotNull;


public class MechanicPart implements Snapable {
    private int score;

    @NotNull
    private final MechanicsTimeService timeService;

    public MechanicPart(@NotNull MechanicsTimeService timeService) {
        this.timeService = timeService;
        score = 0;
    }

    public int updateScore(int delta) {
        score += delta;
        return score;
    }

    public int getScore() {
        return score;
    }


    @Override
    public Snap<? extends Snapable> makeSnap() {
        return new MechanicSnapshot(this);
    }


    public static final class MechanicSnapshot implements Snap<MechanicPart> {
        private int score;

        public MechanicSnapshot(MechanicPart mechanicPart) {
            this.score = mechanicPart.score;
        }

        public int getScore() {
            return score;
        }

    }
}
