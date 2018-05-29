package application.models.id;

import java.io.Serializable;
import java.util.Objects;

public class Id<T> implements Serializable {

    private final long id;

    public Id(Long id) {
        this.id = id;
    }

    public long asLong() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Id<?> id1 = (Id<?>) obj;

        return id == id1.id;
    }

    public static <T> Id<T> defaultId() {
        return new Id<>(-1L);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Id{ " + id + " }";
    }

    public static <T> Id<T> of(Long id) {
        return new Id<>(id);
    }
}
