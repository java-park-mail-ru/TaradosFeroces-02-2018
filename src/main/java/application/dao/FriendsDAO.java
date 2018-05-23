package application.dao;

import application.models.User;
import application.models.id.Id;
import application.utils.responses.UserView;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FriendsDAO {

    boolean addFriend(long userId, long friendId);

    Long addFriendshipRequest(long userId, long friendId);

    Id<User> deleteFriendshipRequest(long requestId, long friendId);

    boolean areTheyFriends(long userId, long friendId);

    boolean deleteFriend(long userId, long friendId);

    @Nullable
    List<UserView> getAllFriendsOfUser(long userId, @Nullable String friendNamePrefix);
}
