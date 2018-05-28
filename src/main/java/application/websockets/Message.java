package application.websockets;


import application.mechanics.messages.in.ClientSnap;
import application.mechanics.messages.in.GameReady;
import application.mechanics.messages.in.InterruptGame;
import application.mechanics.messages.in.JoinGame;
import application.mechanics.messages.out.*;
import application.party.messages.out.*;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;



@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "cls"
)
@JsonSubTypes({
        @Type(value = Ping.class, name = "ping"),

        @Type(value = AskForFriendship.class, name = "AskForFriendship"),

        @Type(value = InviteToParty.class, name = "InviteToParty"),
        @Type(value = LeaveParty.class, name = "LeaveParty"),
        @Type(value = PartyView.class, name = "PartyView"),

        @Type(value = AskForJoinGame.class, name = "AskForJoinGame"),
        @Type(value = AskForJoinGameTimeLeft.class, name = "AFJG_TimeLeft"),
        @Type(value = InitGame.class, name = "InitGame"),
        @Type(value = GamePrepare.class, name = "GamePrepare"),

        @Type(value = JoinGame.class, name = "JoinGame"),
        @Type(value = GameReady.class, name = "GameReady"),

        @Type(value = ServerSnap.class, name = "ServerSnap"),
        @Type(value = ClientSnap.class, name = "ClientSnap"),

        @Type(value = InterruptGame.class, name = "InterruptGame"),
        @Type(value = EndGame.class, name = "EndGame"),
})
public abstract class Message {
}