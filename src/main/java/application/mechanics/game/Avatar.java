package application.mechanics.game;

import application.mechanics.GameSession;
import application.mechanics.base.Position;
import application.mechanics.game.spells.Spell;
import application.mechanics.messages.out.ServerPlayerSnap;
import application.models.User;

import application.utils.responses.Score;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;


public class Avatar {

    public static class InitialAvatarSnap {

        @JsonProperty("party_id")
        private long partyId;

        @NotNull
        @JsonProperty("login")
        private String login;

        @JsonProperty("x")
        private double posX;

        @JsonProperty("y")
        private double posY;

        @NotNull
        @JsonProperty("color")
        private String color;


        public InitialAvatarSnap(@NotNull Avatar avatar) {
            this.partyId = avatar.getPartyId();
            this.login = avatar.getUser().getLogin();
            this.posX = avatar.getPosition().getPosX();
            this.posY = avatar.getPosition().getPosY();
            this.color = "red";
        }
    }

    public static class EndAvatarSnap {

        @JsonProperty("party_id")
        private long partyId;

        @NotNull
        @JsonProperty("login")
        private String login;

        @NotNull
        @JsonProperty("score")
        private Score score;

        public EndAvatarSnap(@NotNull Avatar avatar) {
            this.partyId = avatar.getPartyId();
            this.login = avatar.getUser().getLogin();
            this.score = avatar.getScore();
        }
    }

    @JsonProperty("party_id")
    private long partyId;

    @NotNull
    private final User userProfile;

    @NotNull
    private Position position;

    @NotNull
    private Spell spell;
    private boolean isCastingSpell;
    private long lastTimeSpell;

    @NotNull
    private Score score;

    private boolean isAlive;

    public Avatar(long partyId, @NotNull User userProfile, @NotNull Position position, @NotNull Spell spell) {
        this.partyId = partyId;
        this.userProfile = userProfile;
        this.position = position;
        this.spell = spell;
        this.isCastingSpell = false;
        this.score = new Score(0);
        this.isAlive = true;
        this.lastTimeSpell = -spell.getCooldown();
    }

    public InitialAvatarSnap getInitialSnap() {
        return new InitialAvatarSnap(this);
    }

    public EndAvatarSnap getEndSnap() {
        return new EndAvatarSnap(this);
    }

    @Override
    public String toString() {
        return "avatar["
                + "user=(" + userProfile.getId() + "  " + userProfile.getLogin()
                + ")]";
    }

    @NotNull
    public User getUser() {
        return userProfile;
    }

    public long getId() {
        return userProfile.getId();
    }

    public long getPartyId() {
        return partyId;
    }

    public boolean castSpell(@NotNull GameSession gameSession) {
        final long now = gameSession.getTimeService().time();

        if (lastTimeSpell + spell.getCooldown() <= now) {
            lastTimeSpell = now;

            spell.perform(gameSession);

            setCastingSpell(true);
        }
        setCastingSpell(false);
        return isCastingSpell();
    }

    public boolean isCastingSpell() {
        return isCastingSpell;
    }

    public void setCastingSpell(boolean castingSpell) {
        isCastingSpell = castingSpell;
    }

    @NotNull
    public Score getScore() {
        return score;
    }

    @NotNull
    public void setScore(int score) {
        this.score = new Score(score);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @NotNull
    public ServerPlayerSnap makeSnapshot() {
        final ServerPlayerSnap result = new ServerPlayerSnap(
                partyId,
                position,
                false,
                score,
                this.isAlive()
        );
        return result;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void reset() {
        setCastingSpell(false);
    }


}
