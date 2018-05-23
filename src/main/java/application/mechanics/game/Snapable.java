package application.mechanics.game;


public interface Snapable {

    default boolean shouldBeSnapped() {
        return true;
    }

    Snap<? extends Snapable> makeSnap();
}
