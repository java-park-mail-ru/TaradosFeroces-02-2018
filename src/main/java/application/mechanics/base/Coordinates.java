package application.mechanics.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class Coordinates {

    private static final double DELTA = 1e-12;

    @JsonProperty("x")
    private double posX;

    @JsonProperty("y")
    private double posY;

    public Coordinates(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public Coordinates set(double newPosX, double newPosY) {
        this.posX = newPosX;
        this.posY = newPosY;
        return this;
    }

    public Coordinates add(@NotNull Coordinates coordinates) {
        this.posX += coordinates.posX;
        this.posY += coordinates.posY;
        return this;
    }

    public double getX() {
        return posX;
    }

    public double getY() {
        return posY;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Coordinates that = (Coordinates) obj;
        return Math.abs(that.posX - posX) < DELTA
                && Math.abs(that.posY - posY) < DELTA;
    }

    @Override
    public int hashCode() {
        return Objects.hash(posX, posY);
    }
}
