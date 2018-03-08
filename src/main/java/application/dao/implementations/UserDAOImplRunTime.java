package application.dao.implementations;


import application.dao.UserDAO;
import application.models.User;

import application.models.UserScore;
import application.models.id.Id;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class UserDAOImplRunTime implements UserDAO {
    private static final int LOAD_CONST = 4;
    private ConcurrentHashMap<Long, User> users;
    private AtomicLong idCounter;

    public UserDAOImplRunTime() {
        this.users = new ConcurrentHashMap<Long, User>();
        this.idCounter = new AtomicLong(42L);
    }

    @Override
    public @NotNull Id<User> addUser(@NotNull String login, @NotNull String email,
                                     @NotNull String password, @Nullable String name) {
        final Long id = idCounter.incrementAndGet();
        users.put(id, new User(id, login, password, email, name));
        return new Id<User>(id);
    }

    @Override
    public @Nullable User getUserById(@NotNull Long id) {
        return users.get(id);
    }

    @Override
    public @Nullable User getUserByLogin(@NotNull String login) {
        return users.searchValues(LOAD_CONST, (User user) -> user.getLogin().equals(login) ? user : null);
    }

    @Override
    public @Nullable User getUserByEmail(@NotNull String email) {
        return users.searchValues(LOAD_CONST, (User user) -> user.getEmail().equals(email) ? user : null);
    }

    @Override
    public void updatePassword(@NotNull Long id, @NotNull String password) {
        users.get(id).setPassword(password);
    }

    @Override
    public void deleteUser(@NotNull Long id) {
        users.remove(id);
    }

    @Override
    public boolean checkPassword(@NotNull Long id, @NotNull String password) {
        return getUserById(id).getPassword().equals(password);
    }

    @Override
    public UserScore scoreById(@NotNull Long id) {
        return new UserScore(id, getUserById(id).getPoints());
    }

    @Override
    public UserScore updateScore(@NotNull Long id, @NotNull Long newScore) {
        User user = getUserById(id);
        user.setPoints(newScore);
        return new UserScore(id, newScore);
    }

    @Override
    public ArrayList<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void clear() {
        users.clear();
    }
}
