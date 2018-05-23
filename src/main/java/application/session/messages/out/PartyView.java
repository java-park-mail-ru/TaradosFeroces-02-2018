package application.session.messages.out;

import application.session.Party;
import application.utils.responses.UserPartyView;
import application.websockets.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PartyView extends Message {

    @NotNull
    @JsonProperty("leader")
    private final UserPartyView leader;

    @NotNull
    @JsonProperty("users")
    private final ArrayList<UserPartyView> users;

    public PartyView(@NotNull Party party) {
        leader = party.getLeader();
        users = party.getUsers();
    }
}
