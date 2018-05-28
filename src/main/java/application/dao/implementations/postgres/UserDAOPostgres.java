package application.dao.implementations.postgres;


import application.dao.UserDAO;
import application.models.User;
import application.models.UserScore;
import application.models.id.Id;
import application.utils.omgjava.Pair;

import application.utils.responses.UserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
@Transactional
public class UserDAOPostgres implements UserDAO {

    @Autowired
    private JdbcTemplate template;

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAOPostgres.class);

    private static final RowMapper<User> USER_MAPPER = (res, num) ->
            new User(res.getLong("id"),
                    res.getString("login"),
                    res.getString("password"),
                    res.getString("email"),
                    res.getString("fullname"),
                    res.getString("avatar")
            );


    @Override
    public @NotNull Pair<UpdateStatus, Id<User>> addUser(@NotNull String login,
                                                         @NotNull String email,
                                                         @NotNull String password,
                                                         @Nullable String name,
                                                         @Nullable String avatar) {

        final String query =
                "INSERT INTO users(login, password, email, fullname, avatar) "
                    + "VALUES(?,?,?,?,?) RETURNING id";

        try {
            if (name == null) {
                name = "";
            }
            if (avatar == null) {
                avatar = "";
            }
            final Id<User> id =
                    new Id<>(template.queryForObject(query, Long.class,
                            login, password, email, name, avatar
                    ));

            return new Pair<>(UpdateStatus.SUCCESS, id);

        } catch (DuplicateKeyException ex) {
            LOGGER.warn("DuplicateKeyException: " + ex.getMessage());
            return new Pair<>(UpdateStatus.EMAIL_OR_LOGIN_CONFLICT, null);

        } catch (BadSqlGrammarException ex) {
            LOGGER.warn("BadSqlGrammarException: " + ex.getMessage());
            return new Pair<>(UpdateStatus.DB_ERROR, null);
        }
    }

    protected @Nullable <T> T getUserByCondition(@NotNull String condition,
                                                 @NotNull RowMapper<T> rowUserMapper,
                                                 @Nullable Object... args) {

        final String query = "SELECT * FROM users WHERE " + condition;

        try {
            LOGGER.info("getUserByCondition: condition = " + condition);
            LOGGER.info("getUserByCondition: args: length=" + args.length);

            final List<T> results = template.query(query, rowUserMapper, args);

            if (results.size() == 0) {
                LOGGER.info("getUserByCondition: result is null");
                return null;
            } else {
                final T result = results.get(0);
                return result;
            }
        } catch (EmptyResultDataAccessException ex) {
            LOGGER.warn("getUserByRequest( " + query + " ): EmptyResultDataAccessException: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public @Nullable User getUserById(@NotNull Long id) {
        LOGGER.info("getUserById: args: " + id);
        return getUserByCondition("id = ?", USER_MAPPER, id);
    }

    @Override
    public @Nullable User getUserByLogin(@NotNull String login) {
        LOGGER.info("getUserByLogin: args: " + login);
        return getUserByCondition("login = ?", USER_MAPPER, login);
    }

    @Override
    public @Nullable User getUserByEmail(@NotNull String email) {
        LOGGER.info("getUserByEmail: args: " + email);
        return getUserByCondition("email = ?", USER_MAPPER, email);
    }

    @Override
    public @Nullable List<UserView> selectUsersByLoginPrefix(@Nullable String prefix)  {

        LOGGER.info("selectUsersByLoginPrefix: loginPrefix='" + prefix + "'");

        try {
            final String queryWithoutPrefix =
                    "SELECT id, login, avatar FROM users ORDER BY LOWER(login);";

            final String queryWithPrefix =
                    "SELECT id, login, avatar FROM users "
                           + "WHERE LOWER(login) LIKE '" + prefix + "%' ORDER BY LOWER(login);";

            List<Map<String, Object>> maps = null;

            if (prefix != null) {
                LOGGER.info("selectUsersByLoginPrefix: prefix is not empty");
                maps = template.queryForList(queryWithPrefix);
            } else {
                LOGGER.info("selectUsersByLoginPrefix: prefix is null");
                maps = template.queryForList(queryWithoutPrefix);
            }

            LOGGER.info("selectUsersByLoginPrefix: found " + maps.size() + " user(s)");

            List<UserView> usersViews = new ArrayList<>();

            for (Map<String, Object> map: maps) {
                usersViews.add(new UserView(
                        (Long) map.get("id"),
                        (String) map.get("login"),
                        (String) map.getOrDefault("avatar", "")
                ));
            }

            LOGGER.info("selectUsersByLoginPrefix: usersViews = " + usersViews.toString());
            return usersViews;

        } catch (DataAccessException e) {
            LOGGER.error("selectUsersByLoginPrefix: error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean checkPassword(@NotNull Long id, @NotNull String password) {
        final String query = "SELECT password FROM users WHERE id = ?";

        try {
            String originPass = template.queryForObject(query,
                    (res, num) -> res.getString("password"), id);

            return originPass.equals(password);

        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public UpdateStatus updateUser(@NotNull long userId, @NotNull Map<String, ? extends Object>  data) {
        try {
            StringBuilder query = new StringBuilder("UPDATE users SET ");

            boolean isEmpty = true;
            String[] parameters = {"login", "email", "fullname", "avatar"};

            for (String parameter: parameters) {
                if (data.containsKey(parameter)) {
                    if (!isEmpty) {
                        query.append(", ");
                    }
                    query.append(parameter).append(" = \'").append(data.get(parameter).toString()).append("\'");
                    isEmpty = false;
                }
            }

            if (isEmpty) {
                return UpdateStatus.SUCCESS;
            }

            query.append(" WHERE id = ?;");
            template.update(query.toString(), userId);

            return UpdateStatus.SUCCESS;

        } catch (DuplicateKeyException e) {
            LOGGER.error("DuplicateKeyException in updateUser");
            return UpdateStatus.EMAIL_OR_LOGIN_CONFLICT;
        }
    }


    @Override
    public UserScore updateScore(@NotNull Long id, @NotNull Long newScore) {
        try {
            String query = "UPDATE users SET points = ? WHERE id = ?";
            template.update(query, newScore);
            return new UserScore(id, newScore);

        } catch (DataAccessException e) {
            LOGGER.error("error in updateScore: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getTopUsers(long topCount, long start) {
        try {
            final String query =
                    "SELECT points, login, email FROM users ORDER BY points DESC LIMIT ? OFFSET ?;";
            List<Map<String, Object>> top = template.queryForList(query, topCount, start);
            return top;

        } catch (DataAccessException e) {
            LOGGER.error("error in getTopUsers: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void clearTable() {
        try {
            template.execute("TRUNCATE TABLE users CASCADE");

        } catch (DataAccessException e) {
            LOGGER.error("error in clearTable: " + e.getMessage());
        }
    }


}
