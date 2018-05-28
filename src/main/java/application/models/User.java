package application.models;


import application.models.id.Id;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;


public class User {

    public class Fields {
        public static final String PASSWORD = "password";
        public static final String EMAIL = "email";
        public static final String LOGIN = "login";
        public static final String NAME = "name";
        public static final String AVATAR = "avatar";
    }

    @NotNull
    private final Id<User> id;

    @NotNull
    private String login;
    @NotNull
    private String password;

    @NotNull
    private String email;
    @Nullable
    private Boolean emailChecked;

    @Nullable
    private String name;

    @Nullable
    private long points;

    @Nullable
    private String avatar;

    public User(long id,
                @NotNull String login,
                @NotNull String password,
                @NotNull String email,
                @Nullable String name,
                @Nullable String avatar) {
        this.id = new Id<>(id);
        this.login = login;
        this.password = password;
        this.email = email;
        this.emailChecked = false;
        this.name = name;
        this.points = 0L;
        this.avatar = avatar;
    }

    public long getId() {
        return id.asLong();
    }

    public Id<User> getUserId() {
        return id;
    }

    @NotNull
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailChecked() {
        return emailChecked;
    }

    public void setEmailChecked(@Nullable Boolean emailChecked) {
        this.emailChecked = emailChecked;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    @Nullable
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(@Nullable String avatar) {
        this.avatar = avatar;
    }

    public static class PointComparator implements Comparator<User> {

        @Override
        public int compare(User user, User t1) {
            return Long.compare(user.getPoints(), t1.getPoints());
        }
    }
}
