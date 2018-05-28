package application.mechanics.messages.out;


import application.mechanics.game.Avatar;
import application.websockets.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class ServerSnap extends Message {

    @NotNull
    @JsonProperty("players")
    private final ArrayList<ServerPlayerSnap> players;

    @JsonProperty("party_id")
    private long index;

    public ServerSnap(@NotNull ArrayList<Avatar> players) {
        this.players = players.stream().map(Avatar::makeSnapshot).collect(Collectors.toCollection(ArrayList::new));
        this.index = 0;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    @NotNull
    public ArrayList<ServerPlayerSnap> getPlayers() {
        return players;
    }


}
