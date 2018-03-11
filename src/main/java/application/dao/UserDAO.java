package application.dao;


import application.models.User;

import application.models.UserScore;
import application.models.id.Id;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public interface UserDAO {

    enum UpdateInfo {
        SUCCESS,
        LOGIN_EXIST,
        EMAIL_EXIST,
        WRONG_ID
    }

    @NotNull
    Id<User> addUser(@NotNull String login,
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

    UpdateInfo updateUser(
        @NotNull long userId,
        @NotNull String login,
        @NotNull String email,
        @Nullable String avatar);


    UserScore updateScore(@NotNull Long id, @NotNull Long newScore);

    ArrayList<User> getAll();

}
