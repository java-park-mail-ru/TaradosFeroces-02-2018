package application.services;


import application.dao.UserDAO;
import application.models.User;
import application.models.id.Id;
import application.utils.omgjava.Pair;
import application.utils.requests.UserSignUpRequest;

import application.utils.responses.Score;
import application.utils.responses.ScoreData;
import application.utils.responses.ScoreView;
import application.utils.responses.UserInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AccountService {
    @NotNull
    private final UserDAO userDB;

    @NotNull
    private final PasswordEncoder encoder;

    public enum ChangePasswordStatus {
        SUCCESS,
        USER_NOT_EXISTS,
        WRONG_PASSWORD,
        PASSWORDS_MATCHES,
        DB_ERROR
    }

    public enum UpdateStatus {
        SUCCESS,
        USER_NOT_EXISTS,
        EMAIL_CONFLICT,
        LOGIN_CONFLICT,
        EMAIL_AND_LOGIN_CONFLICT,
        DB_ERROR
    }

    public enum AuthCheckStatus {
        SUCCESS,
        USER_NOT_EXISTS,
        WRONG_PASSWORD,
    }


    public AccountService(@NotNull UserDAO userDB, @NotNull PasswordEncoder encoder) {
        this.userDB = userDB;
        this.encoder = encoder;
    }


    public Pair<UpdateStatus, Id<User>> addUser(@NotNull UserSignUpRequest userSignup) {

        final String encodedPassword = encoder.encode(userSignup.getPassword());

        boolean loginCrutch = (userDB.getUserByLogin(userSignup.getLogin()) != null);

        if (userDB.getUserByEmail(userSignup.getEmail()) != null) {
            if (loginCrutch) {
                return new Pair<>(UpdateStatus.EMAIL_AND_LOGIN_CONFLICT, null);
            } else {
                return new Pair<>(UpdateStatus.EMAIL_CONFLICT, null);
            }
        } else if (loginCrutch) {
            return new Pair<>(UpdateStatus.LOGIN_CONFLICT, null);
        }

        Pair<UserDAO.UpdateStatus, Id<User>> statusIdPair = userDB.addUser(
                userSignup.getLogin(),
                userSignup.getEmail(),
                encodedPassword,
                userSignup.getName(),
                userSignup.getAvatar()
        );

        switch (statusIdPair.getArg1()) {
            case SUCCESS:
                return new Pair<>(UpdateStatus.SUCCESS, statusIdPair.getArg2());
            default:
                return new Pair<>(UpdateStatus.DB_ERROR, null);
        }
    }

    public Pair<AuthCheckStatus, Id<User>> checkSignin(@NotNull String login, @NotNull String password) {
        final User user = userDB.getUserByLogin(login);

        if (user == null) {
            return new Pair<>(AuthCheckStatus.USER_NOT_EXISTS, null);
        }

        return encoder.matches(password, user.getPassword())
                ? new Pair<>(AuthCheckStatus.SUCCESS, user.getUserId())
                : new Pair<>(AuthCheckStatus.WRONG_PASSWORD, null);
    }

    public ChangePasswordStatus changePassword(@NotNull String login,
                                               @NotNull String oldPassword,
                                               @NotNull String newPassword) {
        final User user = userDB.getUserByEmail(login);
        if (user == null) {
            return ChangePasswordStatus.USER_NOT_EXISTS;
        }
        if (!encoder.matches(oldPassword, user.getPassword())) {
            return ChangePasswordStatus.WRONG_PASSWORD;
        }
        if (encoder.matches(newPassword, user.getPassword())) {
            return ChangePasswordStatus.PASSWORDS_MATCHES;
        }

        final String encodedPassword = encoder.encode(newPassword);


        Map<String, String> updateMap = new HashMap<String, String>();
        updateMap.put(User.Fields.PASSWORD, encodedPassword);

        UserDAO.UpdateStatus status = userDB.updateUser(user.getId(), updateMap);

        if (status.equals(UserDAO.UpdateStatus.SUCCESS)) {
            return ChangePasswordStatus.SUCCESS;
        } else {
            return ChangePasswordStatus.DB_ERROR;
        }
    }


    private UpdateStatus updateUser(@Nullable User user, @NotNull Map<String, Object> data) {
        if (user == null) {
            return UpdateStatus.USER_NOT_EXISTS;
        }

        final long userId = user.getId();

        boolean loginCrutch = false;
        if (data.containsKey(User.Fields.LOGIN)) {
            final User loginUser = userDB.getUserByLogin((String) data.get(User.Fields.LOGIN));
            if (loginUser != null && loginUser.getId() != userId) {
                loginCrutch = true;
            }
        }
        if (data.containsKey(User.Fields.EMAIL)) {
            final User emailUser = userDB.getUserByEmail((String) data.get(User.Fields.EMAIL));
            if (emailUser != null && emailUser.getId() != userId) {
                if (loginCrutch) {
                    return UpdateStatus.EMAIL_AND_LOGIN_CONFLICT;
                } else {
                    return UpdateStatus.EMAIL_CONFLICT;
                }
            }
        } else if (loginCrutch) {
            return UpdateStatus.LOGIN_CONFLICT;
        }

        UserDAO.UpdateStatus status = userDB.updateUser(userId, data);

        switch (status) {
            case SUCCESS:
                return UpdateStatus.SUCCESS;
            default:
                return UpdateStatus.DB_ERROR;
        }
    }


    public UpdateStatus updateUser(@NotNull String login, @NotNull Map<String, Object> data) {
        final User user = userDB.getUserByLogin(login);
        return updateUser(user, data);
    }

    public UpdateStatus updateUser(@NotNull long id, @NotNull Map<String, Object> data) {
        final User user = userDB.getUserById(id);
        return updateUser(user, data);
    }

    @Nullable
    public User getUserById(long id) {
        final User user = userDB.getUserById(id);

        if (user != null) {
            user.setPassword("");
        }
        return user;
    }

    public ScoreData getTopUsers(long topCount, long start) {

        List<Map<String, Object>> top = userDB.getTopUsers(topCount, start);
        if (top == null) {
            return null;
        }

        ScoreData scoreData = new ScoreData();
        for (Map<String, Object> map: top) {
            scoreData.addScoreView(new ScoreView(
                    new Score((Long) map.get("points")),
                    new UserInfo((String) map.get("email"), (String) map.get("login"))
            ));
        }
        return scoreData;
    }

    public void deleteAllUsers() {
        userDB.clearTable();
    }
}