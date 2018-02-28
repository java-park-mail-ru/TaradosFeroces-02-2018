package application.dao.implementations;


import application.dao.UserDAO;
import application.models.User;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class UserDAOImplRunTime implements UserDAO {
    private ConcurrentHashMap<Long, User> users;
    private AtomicLong idCounter;

    public UserDAOImplRunTime() {
        this.users = new ConcurrentHashMap<Long, User>();
        this.idCounter = new AtomicLong(42L);
    }

    @Override
    public @NotNull Long addUser(@NotNull String login, @NotNull String email, @NotNull String password, @Nullable String name) {
        final Long id = this.idCounter.incrementAndGet();
        // FIXME: 28.02.18 Low-iq solution
        users.put(id, new User(id, login, password, email, false, name));
        return id;
    }

    @Override
    public @Nullable User getUserById(@NotNull Long id) {
        return users.get(id);
    }

    @Override
    public @Nullable User getUserByLogin(@NotNull String login) {
        return users.searchValues(4, (User user) -> user.getLogin().equals(login) ? user : null);
    }

    @Override
    public @Nullable User getUserByEmail(@NotNull String email) {
        return users.searchValues(4, (User user) -> user.getEmail().equals(email) ? user : null);
    }

    @Override
    public void updatePassword(@NotNull Long id, @NotNull String password) {
        users.get(id).setPassword(password);
    }

    @Override
    public void deleteUser(@NotNull Long id) {

    }

    @Override
    public boolean checkPassword(@NotNull Long id, @NotNull String password) {
        return this.getUserById(id).getPassword().equals(password);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
