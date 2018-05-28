package application.party.messages.out;

import application.websockets.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class Ping extends Message {

    @NotNull
    @JsonProperty("message")
    private final String message;

    public Ping(@NotNull String message) {
        this.message = message;
    }
}
