package application.dao;


import application.models.User;

import application.models.id.Id;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface UserDAO {

    @NotNull
    Id<User> addUser(@NotNull String login,
                     @NotNull String email,
                     @NotNull String password,
                     @Nullable String name);

    @Nullable
    User getUserById(@NotNull Long id);

    @Nullable
    User getUserByLogin(@NotNull String login);

    @Nullable
    User getUserByEmail(@NotNull String email);

    void updatePassword(@NotNull Long id, @NotNull String password);

    void deleteUser(@NotNull Long id);

    boolean checkPassword(@NotNull Long id, @NotNull String password);

    void clear();
}
