package application.mechanics.messages.in;

import application.websockets.Message;


public class GameReady extends Message {

    public boolean isReady() {
        return true;
    }
}
