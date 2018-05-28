package application.mechanics.messages.out;

import application.websockets.Message;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AskForJoinGameTimeLeft extends Message {

    @JsonProperty("id")
    private final long id;

    @JsonProperty("time_left")
    private final long timeLeft;

    public AskForJoinGameTimeLeft(long id, long timeLeft) {
        this.id = id;
        this.timeLeft = timeLeft;
    }
}