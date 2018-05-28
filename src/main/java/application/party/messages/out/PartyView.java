package application.party.messages.out;

import application.party.Party;
import application.party.Player;
import application.websockets.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PartyView extends Message {

    @NotNull
    @JsonProperty("leader")
    private final Player leader;

    @NotNull
    @JsonProperty("users")
    private final ArrayList<Player> users;

    public PartyView(@NotNull Party party) {
        leader = party.getLeader();
        users = party.getUsers();
    }
}
