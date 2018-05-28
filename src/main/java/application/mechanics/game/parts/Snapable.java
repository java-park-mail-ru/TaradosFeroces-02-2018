package application.mechanics.game.parts;


public interface Snapable {

    default boolean shouldBeSnapped() {
        return true;
    }

    Snap<? extends Snapable> makeSnap();
}
