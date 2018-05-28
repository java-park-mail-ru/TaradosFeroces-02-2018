package application.mechanics.messages.out;


import application.mechanics.base.Position;
import application.utils.responses.Score;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;


public class ServerPlayerSnap {

    @JsonProperty("party_id")
    private long partyId;

    @NotNull
    @JsonProperty("pos")
    private Position position;

    @JsonIgnore
    private boolean castTheSpell;

    @NotNull
    @JsonProperty("score")
    private Score score;

    @JsonProperty("alive")
    private boolean isAlive;


    public ServerPlayerSnap() {
        this.position = new Position(0, 0);
        this.castTheSpell = false;
        this.score = new Score(0);
        this.isAlive = false;
    }

    public ServerPlayerSnap(long partyId,
                            @NotNull Position position,
                            boolean castTheSpell,
                            @NotNull Score score,
                            boolean isAlive) {
        this.partyId = partyId;
        this.position = position;
        this.castTheSpell = castTheSpell;
        this.score = score;
        this.isAlive = isAlive;
    }


    public long getPartyId() {
        return partyId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isCastTheSpell() {
        return castTheSpell;
    }

    public void setCastTheSpell(boolean castTheSpell) {
        this.castTheSpell = castTheSpell;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
