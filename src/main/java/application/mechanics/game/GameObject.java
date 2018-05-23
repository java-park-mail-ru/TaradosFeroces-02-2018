package application.mechanics.game;

import application.models.id.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public abstract class GameObject {
    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    @NotNull
    private final Map<Class<?>, Snapable> snapshots = new HashMap<>();

    @NotNull
    private final Id<GameObject> id;

    public GameObject() {
        this.id = Id.of(ID_GENERATOR.incrementAndGet());
    }

    @NotNull
    @JsonProperty("id")
    public Id<GameObject> getId() {
        return id;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Snapable> T getSnap(@NotNull Class<T> clazz) {
        return (T) snapshots.get(clazz);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends Snapable> T claimSnapshot(@NotNull Class<T> clazz) {
        @Nullable final T snap = getSnap(clazz);
        if (snap == null) {
            throw new NullPointerException("Claimed snap shouldn't be null");
        }
        return snap;
    }

    public <T extends Snapable> void addSnapshot(@NotNull Class<T> clazz, @NotNull T gameSnapShot) {
        snapshots.put(clazz, gameSnapShot);
    }


    @NotNull
    public List<Snap<? extends Snapable>> getSnapshots() {
        // May be java can be functional...
        return snapshots.values().stream()
                .filter(Snapable::shouldBeSnapped)
                .map(Snapable::makeSnap)
                .collect(Collectors.toList());
    }

    public abstract Snap<? extends Snapable> makeSnap();
}
