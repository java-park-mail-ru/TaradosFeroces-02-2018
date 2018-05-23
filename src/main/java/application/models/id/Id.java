package application.models.id;

public class Id<T> {
    private final long id;

    public Id(Long id) {
        this.id = id;
    }

    public long asLong() {
        return id;
    }

    public static <T> Id<T> of(Long id) {
        return new Id<>(id);
    }
}
