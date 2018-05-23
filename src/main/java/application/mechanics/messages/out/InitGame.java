package application.mechanics.messages.out;


import application.models.User;
import application.models.id.Id;
import application.websockets.Message;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



public class InitGame {
    public static final class Request extends Message {

        private Id<User> self;
        private List<Id<User>> teammates = new ArrayList<>();


        @NotNull
        public Id<User> getSelf() {
            return self;
        }

        @NotNull
        public List<Id<User>> getTeammates() {
            return teammates;
        }

        public void setSelf(Id<User> self) {
            this.self = self;
        }

        @NotNull
        public void addTeammate(Id<User> user) {
            teammates.add(user);
        }
    }
}
