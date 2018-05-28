package application.mechanics.messages.in;

import application.websockets.Message;

import com.fasterxml.jackson.annotation.JsonProperty;


public class JoinGame extends Message {

    @JsonProperty("login")
    private final String login;

    public JoinGame(@JsonProperty(value = "login") String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}
