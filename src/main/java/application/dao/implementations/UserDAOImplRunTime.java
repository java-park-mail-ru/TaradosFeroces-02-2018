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

        updateScore(addUser("Alexander", "", "", "", "").asLong(), 100500L);
        updateScore(addUser("Toha", "", "", "", "").asLong(), 27L);
        updateScore(addUser("Danchetto", "", "", "", "").asLong(), 5444L);
        updateScore(addUser("Lolkek1", "", "", "", "").asLong(), 218L);
        updateScore(addUser("Lolkek2", "", "", "", "").asLong(), 0L);
        updateScore(addUser("Lolkek3", "", "", "", "").asLong(), 314L);
        updateScore(addUser("Lolkek4", "", "", "", "").asLong(), 54802L);
        updateScore(addUser("Java is shit", "", "", "", "").asLong(), 21L);
        updateScore(addUser("Lolkek5", "", "", "", "").asLong(), 48329L);
        updateScore(addUser("Lolkek6", "", "", "", "").asLong(), 541L);
        updateScore(addUser("Lolkek7", "", "", "", "").asLong(), 10000L);
        updateScore(addUser("CPP is the fckn best lang", "", "", "", "").asLong(), 100L);
        updateScore(addUser("Lolkek8", "", "", "", "").asLong(), 2300L);
        updateScore(addUser("Lolkek9", "", "", "", "").asLong(), 178L);
        updateScore(addUser("LolkekA", "", "", "", "").asLong(), 12L);
    }

    @Override
    public @NotNull Id<User> addUser(@NotNull String login, @NotNull String email,
                                     @NotNull String password, @Nullable String name,
                                     @Nullable String avatar) {
        final Long id = idCounter.incrementAndGet();
        users.put(id, new User(id, login, password, email, name, avatar));
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
    public boolean checkPassword(@NotNull Long id, @NotNull String password) {
        return getUserById(id).getPassword().equals(password);
    }

    @Override
    @NotNull
    public UpdateInfo updateUser(@NotNull long userId, @NotNull String login, @NotNull String email, @Nullable String avatar) {

        User user = getUserById(userId);
        if (user == null) {
            return UpdateInfo.WRONG_ID;
        }

        if (!user.getLogin().equals(login)) {
            if (getUserByLogin(login) != null) {
                return UpdateInfo.LOGIN_EXIST;
            }

            user.setLogin(login);
        }

        if (!user.getEmail().equals(email)) {
            if (getUserByEmail(email) != null) {
                return UpdateInfo.EMAIL_EXIST;
            }

            user.setEmail(email);
            user.setEmailChecked(false);
        }

        user.setAvatar(avatar);

        return UpdateInfo.SUCCESS;
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

}
