package application.dao.implementations;


import application.dao.UserDAO;
import application.models.User;
import application.models.UserScore;
import application.models.id.Id;
import application.utils.omgjava.Pair;

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

import java.util.List;
import java.util.Map;


@Transactional
@Component
public class UserDAOImpl implements UserDAO {

    @Autowired
    private JdbcTemplate template;

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAOImpl.class);

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
        final String query = "INSERT INTO users(login, password, email, fullname, avatar) VALUES(?,?,?,?,?) RETURNING id";

        try {
            if (name == null) {
                name = "";
            }
            if (avatar == null) {
                avatar = "";
            }
            final Id<User> id =
                    new Id<>(template.queryForObject(query, Long.class, login, password, email, name, avatar));

            return new Pair<>(UpdateStatus.SUCCESS, id);
        } catch (DuplicateKeyException ex) {
            System.out.println("\n\n\n###USER_DAO: " + ex.toString() + "\n\n\n");
            return new Pair<>(UpdateStatus.EMAIL_OR_LOGIN_CONFLICT, null);
        } catch (BadSqlGrammarException ex) {
            System.out.println("\n\n\n###USER_DAO: " + ex.toString() + "\n\n\n");
            return new Pair<>(UpdateStatus.DB_ERROR, null);
        }
    }

    @Override
    public @Nullable User getUserById(@NotNull Long id) {
        try {
            final String query = "SELECT * FROM users WHERE id = ?";
            return template.queryForObject(query, USER_MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public @Nullable User getUserByLogin(@NotNull String login) {
        try {
            final String query = "SELECT * FROM users WHERE login = ?";
            return template.queryForObject(query, USER_MAPPER, login);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public @Nullable User getUserByEmail(@NotNull String email) {
        try {
            final String query = "SELECT * FROM users WHERE email = ?";
            return template.queryForObject(query, USER_MAPPER, email);
        } catch (EmptyResultDataAccessException e) {
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
            String query = "UPDATE users SET ";

            boolean smth = false;

            if (data.containsKey("login")) {
                query += "login = \'" + data.get("login").toString() + "\'";
                smth = true;
            }
            if (data.containsKey("email")) {
                if (smth) {
                    query += ", ";
                    smth = true;
                }
                query += "email = \'" + data.get("email").toString() + "\'";
            }
            if (data.containsKey("password")) {
                if (smth) {
                    query += ", ";
                    smth = true;
                }
                query += "password = \'" + data.get("password").toString() + "\'";
            }
            if (data.containsKey("name")) {
                if (smth) {
                    query += ", ";
                    smth = true;
                }
                query += "fullname = \'" + data.get("name").toString()+ "\'";
            }
            if (data.containsKey("avatar")) {
                if (smth) {
                    query += ", ";
                    smth = true;
                }
                query += "avatar = \'" + data.get("avatar").toString()+ "\'";
            }

            if (!smth) {
                return UpdateStatus.SUCCESS;
            }

            query +=  " WHERE id = ?;";
            template.update(query, userId);

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
