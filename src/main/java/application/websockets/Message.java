package application.websockets;


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

})
public abstract class Message {
}