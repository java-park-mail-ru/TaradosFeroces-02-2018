package application.models;

import application.mechanics.base.Coordinates;
import application.models.id.Id;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;


public class Mob {

    @NotNull
    private final Id<Mob> id;

    @NotNull
    private final String name;

    @Nullable
    private final String description;

    private final long level;


    public Mob(@NotNull Id<Mob> id,
               long level,
               @NotNull String name,
               @Nullable String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = level;
    }

    public Mob(long id,
               long level,
               @NotNull String name,
               @Nullable String description) {
        this.id = new Id<>(id);
        this.name = name;
        this.description = description;
        this.level = level;
    }

    public Mob() {
        this.id = new Id<>(-1L);
        this.name = "NoName";
        this.description = "Empty info";
        this.level = 0;
    }

    @NotNull
    public Id<Mob> getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public long getLevel() {
        return level;
    }

    public static LevelComparator getLevelComparator() {
        return new LevelComparator();
    }

    @NotNull
    public MobStartState getMobStartState(@NotNull Coordinates start, long delay) {
        return new MobStartState(this, start, delay);
    }

    public static class MobStartState {

        private final Id<Mob> mobId;

        private Coordinates start;

        private long delay;


        public MobStartState(@NotNull Mob mob, @NotNull Coordinates start, long delay) {
            this.mobId = mob.getId();
            this.start = start;
            this.delay = delay;
        }

        public MobStartState(@NotNull Id<Mob> mobId, @NotNull Coordinates start, long delay) {
            this.mobId = mobId;
            this.start = start;
            this.delay = delay;
        }

        public MobStartState() {
            this.mobId = Id.defaultId();
            this.start = new Coordinates(0.0, 0.0);
            this.delay = 0;
        }
    }

    public static class LevelComparator implements Comparator<Mob> {

        @Override
        public int compare(Mob lhs, Mob rhs) {
            return Long.compare(lhs.getLevel(), rhs.getLevel());
        }
    }
}
