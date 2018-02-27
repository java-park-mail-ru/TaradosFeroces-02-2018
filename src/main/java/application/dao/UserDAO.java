package application.dao;


import application.models.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UserDAO {

    @NotNull /* id */
    Long addUser(@NotNull String login,
                 @NotNull String email,
                 @NotNull String password,
                 @Nullable String name);

    @Nullable
    User getUserById(@NotNull Long id);

    @Nullable
    User getUserByLogin(@NotNull String login);

    void updatePassword(@NotNull Long id, @NotNull String password);

    void deleteUser(@NotNull Long id);

    boolean checkPassword(@NotNull Long id, @NotNull String password);

    void clear();
}
