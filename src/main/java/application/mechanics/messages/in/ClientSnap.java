package application.mechanics.messages.in;

import application.mechanics.base.Movement;
import application.websockets.Message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ClientSnap extends Message {

    @NotNull
    @JsonProperty(value = "x", defaultValue = "0")
    private int moveX;

    @NotNull
    @JsonProperty(value = "y", defaultValue = "0")
    private int moveY;

    @NotNull
    @JsonProperty("id")
    private int id;

    @NotNull
    @JsonProperty(value = "spell", defaultValue = "false")
    private boolean spell;

    @JsonIgnore
    private long time;


    public int getXMove() {
        return moveX;
    }

    public int getYMove() {
        return moveY;
    }

    @NotNull
    public Movement getMovement() {
        return new Movement((double) moveX, (double) moveY);
    }

    public int getId() {
        return id;
    }

    public boolean doesPerformingSpell() {
        return spell;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
