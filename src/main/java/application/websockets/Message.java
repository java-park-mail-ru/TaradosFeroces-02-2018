package application.websockets;


import application.mechanics.messages.in.OpenSession;
import application.mechanics.messages.out.InitGame;

import application.session.messages.out.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;



@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "cls"
)
@JsonSubTypes({
        @Type(value = OpenSession.Request.class, name = "os"),
        @Type(value = InitGame.Request.class, name = "ig"),
        @Type(value = AddAsFriendRequest.class, name = "aaf"),
        @Type(value = InviteToParty.class, name = "itp"),
        @Type(value = Ping.class, name = "ping"),
        @Type(value = LeaveParty.class, name = "LeaveParty"),
        @Type(value = PartyView.class, name = "pv")
})
public abstract class Message {
}