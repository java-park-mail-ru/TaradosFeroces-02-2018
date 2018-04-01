package application.dao;


import application.models.User;
import application.models.UserScore;
import application.models.id.Id;
import application.utils.omgjava.Pair;

import application.utils.responses.ScoreData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


public interface UserDAO {

    enum UpdateStatus {
        SUCCESS,
        //LOGIN_EXIST,
        //EMAIL_EXIST,
        EMAIL_OR_LOGIN_CONFLICT,
        WRONG_ID
    }

    @NotNull
    Pair<UpdateStatus, Id<User>> addUser(@NotNull String login,
                                         @NotNull String email,
                                         @NotNull String password,
                                         @Nullable String name,
                                         @Nullable String avatar);

    @Nullable
    User getUserById(@NotNull Long id);

    @Nullable
    User getUserByLogin(@NotNull String login);

    @Nullable
    User getUserByEmail(@NotNull String email);

    boolean checkPassword(@NotNull Long id, @NotNull String password);

    UpdateStatus updateUser(@NotNull long userId, @NotNull Map<String, Object> data);


    UserScore updateScore(@NotNull Long id, @NotNull Long newScore);

    ScoreData getTopUsers(long topCount, long start);
}
