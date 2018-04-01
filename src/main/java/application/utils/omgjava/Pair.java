package application.utils.omgjava;

import java.util.Objects;

public class Pair<T1, T2> {

    private final T1 arg1;
    private final T2 arg2;

    public Pair(T1 arg1, T2 arg2) {
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public T1 getArg1() {
        return arg1;
    }

    public T2 getArg2() {
        return arg2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return Objects.equals(getArg1(), pair.getArg1())
                && Objects.equals(getArg2(), pair.getArg2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getArg1(), getArg2());
    }
}
