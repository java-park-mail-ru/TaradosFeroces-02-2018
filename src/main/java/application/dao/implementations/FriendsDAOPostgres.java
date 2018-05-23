package application.dao.implementations;


import application.dao.FriendsDAO;
import application.models.User;
import application.models.id.Id;
import application.utils.responses.UserView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Transactional
public class FriendsDAOPostgres implements FriendsDAO {

    @Autowired
    private JdbcTemplate template;

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendsDAOPostgres.class);


    @Override
    public boolean addFriend(long userId, long friendId) {
        final String query = "INSERT INTO friends(id1, id2) VALUES (?,?), (?,?);";

        try {
            template.update(query,
                    userId, friendId,
                    friendId, userId);
        } catch (DataAccessException ex) {
            LOGGER.warn("DataAccessException: " + ex.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public Long addFriendshipRequest(long userId, long friendId) {
        final String query = "INSERT INTO friendship_requests(sender, friend) VALUES (?,?) RETURNING id;";

        LOGGER.info("addFriendshipRequest: user_id=" + userId + ", friend_id=" + friendId);

        try {
            return template.queryForObject(query, Long.class, userId, friendId);
        } catch (DataAccessException ex) {
            LOGGER.warn("DataAccessException: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public Id<User> deleteFriendshipRequest(long requestId, long friendId) {
        final String queryForUser = "SELECT sender FROM friendship_requests WHERE id = ? AND friend = ?;";
        final String queryForDelete = "DELETE FROM friendship_requests VALUES WHERE id = ? AND friend = ?;";

        LOGGER.info("deleteFriendshipRequest: request_id=" + requestId + ", friend_id=" + friendId);

        Id<User> senderId = null;

        try {
            senderId = Id.of(template.queryForObject(queryForUser, Long.class, requestId, friendId));
        } catch (DataAccessException ex) {
            LOGGER.warn("query for sender id: DataAccessException: " + ex.getMessage());
            return null;
        }

        try {
            template.update(queryForDelete, requestId, friendId);
        } catch (DataAccessException ex) {
            LOGGER.warn("update query: DataAccessException: " + ex.getMessage());
            return null;
        }

        return senderId;
    }

    @Override
    public boolean areTheyFriends(long userId, long friendId) {
        final String query = "SELECT COUNT(*) FROM friends WHERE id1 = ? AND id2 = ?;";

        try {
            Long count = template.queryForObject(query, Long.class,
                    userId, friendId);

            return (count > 0);
        } catch (DataAccessException ex) {
            LOGGER.warn("DataAccessException: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) {
        final String query = "DELETE FROM friends VALUES WHERE (id1 = ? AND id2 = ?) OR (id1 = ? AND id2 = ?);";

        try {
            template.update(query,
                    userId, friendId,
                    friendId, userId);
        } catch (DataAccessException ex) {
            LOGGER.warn("DataAccessException: " + ex.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public @Nullable List<UserView> getAllFriendsOfUser(long userId,  @Nullable String friendNamePrefix) {

        LOGGER.info("getAllFriendsOfUser: userId=" + userId + ", friendNamePrefix='"
                    + friendNamePrefix + "'");

        try {
            final String queryWithoutPrefix =
                    "SELECT login, avatar FROM users u JOIN friends f ON u.id = f.id2 "
                        + "WHERE f.id1 = ? ORDER BY LOWER(login);";

            final String queryWithPrefix =
                    "SELECT login, avatar FROM users u JOIN friends f ON u.id = f.id2 "
                        + "WHERE f.id1 = ? AND LOWER(login) LIKE '" + friendNamePrefix + "%' ORDER BY LOWER(login);";



            List<Map<String, Object>> maps = null;

            if (friendNamePrefix != null) {
                LOGGER.info("getAllFriendsOfUser: prefix is not empty");
                maps = template.queryForList(queryWithPrefix, userId);
            } else {
                LOGGER.info("getAllFriendsOfUser: prefix is null");
                maps = template.queryForList(queryWithoutPrefix, userId);
            }

            LOGGER.info("getAllFriendsOfUser: found " + maps.size() + "user(s)");

            List<UserView> usersViews = new ArrayList<>();

            for (Map<String, Object> map: maps) {
                usersViews.add(new UserView(
                        (String) map.get("login"), (String) map.getOrDefault("avatar", "")
                ));
            }
            LOGGER.info("getAllFriendsOfUser: usersViews = " + usersViews.toString());

            return usersViews;

        } catch (DataAccessException e) {
            LOGGER.error("error in getTopUsers: " + e.getMessage());
            return null;
        }
    }
}
