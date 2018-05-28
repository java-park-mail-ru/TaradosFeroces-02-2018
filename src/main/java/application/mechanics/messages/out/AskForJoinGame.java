package application.mechanics.messages.out;

import application.party.Party;
import application.party.messages.out.PartyView;
import application.websockets.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;


public class AskForJoinGame extends Message {

    @NotNull
    @JsonProperty("party")
    private final PartyView party;

    @JsonProperty("id")
    private final long id;

    public AskForJoinGame(@NotNull PartyView party, long id) {
        this.party = party;
        this.id = id;
    }

    public AskForJoinGame(@NotNull Party party, long id) {
        this.party = new PartyView(party);
        this.id = id;
    }
}
