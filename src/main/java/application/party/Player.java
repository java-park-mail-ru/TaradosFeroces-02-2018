package application.party;

import application.models.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public class Player {

    @NotNull
    @JsonIgnore
    private Long id;

    @JsonProperty("party_id")
    private long partyId;

    @NotNull
    @JsonProperty("login")
    private String login;

    @Nullable
    @JsonProperty("avatar")
    private String avatar;


    public Player(@NotNull User user, long partyId) {
        this.partyId = partyId;
        this.id = user.getId();
        this.login = user.getLogin();
        this.avatar = user.getAvatar();
    }

    public Player(@NotNull Long id, @NotNull String login, @Nullable String avatar, long partyId) {
        this.partyId = partyId;
        this.id = id;
        this.login = login;
        this.avatar = avatar;
    }

    public long getPartyId() {
        return partyId;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getAvatar() {
        return avatar;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Player that = (Player) object;
        return Objects.equals(id, that.id) && Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }
}