package application.session.messages.out;

import application.websockets.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class PartyUpdate extends Message {

    @NotNull
    @JsonProperty("leader")
    private final String leader;

    public PartyUpdate(@NotNull String leader) {
        this.leader = leader;
    }
}
