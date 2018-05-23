package application.services;


import application.dao.FriendsDAO;
import application.dao.UserDAO;
import application.models.User;
import application.models.id.Id;
import application.utils.requests.AddFriend;
import application.utils.requests.SelectUsersByLoginPrefix;
import application.utils.responses.UserView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class FriendsService {

    @NotNull
    private final UserDAO userDB;

    @NotNull
    private final FriendsDAO friendsDB;

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendsService.class);


    public FriendsService(@NotNull UserDAO userDB, @NotNull FriendsDAO friendsDB) {
        this.userDB = userDB;
        this.friendsDB = friendsDB;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("created FriendsService object");
    }

    public boolean areTheyFriends(long userId1, long userId2) throws UserDoesNotExist {
        final User user1 = userDB.getUserById(userId1);
        if (user1 == null) {
            throw new UserDoesNotExist();
        }
        final User user2 = userDB.getUserById(userId2);
        if (user2 == null) {
            throw new UserDoesNotExist();
        }

        return friendsDB.areTheyFriends(userId1, userId2);
    }

    public boolean areTheyFriends(@NotNull User user1, @NotNull User user2) {
        return friendsDB.areTheyFriends(user1.getId(), user2.getId());
    }



    public boolean addFriend(long userId, @NotNull AddFriend addFriend) throws UserDoesNotExist {
        final User friend = userDB.getUserByLogin(addFriend.getLogin());
        if (friend == null) {
            throw new UserDoesNotExist();
        }

        return friendsDB.addFriend(userId, friend.getId());
    }

    public boolean addFriend(long userId, long friendId) throws UserDoesNotExist {
        final User friend = userDB.getUserById(friendId);
        if (friend == null) {
            throw new UserDoesNotExist();
        }

        return friendsDB.addFriend(userId, friend.getId());
    }

    public Long addFriendshipRequest(@NotNull User sender, @NotNull User friend) {
       return friendsDB.addFriendshipRequest(sender.getId(), friend.getId());
    }

    private Id<User> deleteFriendshipRequest(@NotNull Long requestId, @NotNull User reciever) {
        return friendsDB.deleteFriendshipRequest(requestId, reciever.getId());
    }

    public boolean handleFriendshipResponse(@NotNull Long requestId, @NotNull User friend) {
        Id<User> senderId = this.deleteFriendshipRequest(requestId, friend);
        if (senderId == null) {
            return false;
        }

        return friendsDB.addFriend(friend.getId(), senderId.asLong());
    }

    @Nullable
    public List<UserView> getAllFriendsOfUser(long id, @NotNull SelectUsersByLoginPrefix userFriends) {
        String prefix = null;
        if (!userFriends.getPrefix().equals("")) {
            prefix = userFriends.getPrefix();
        }
        return friendsDB.getAllFriendsOfUser(id, prefix);
    }


    public static class UserDoesNotExist extends Exception {
        public UserDoesNotExist() {
            super("User does not exist");
        }
    }

}
