package application.models;


import application.models.id.Id;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;


public class User {

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

    public User(@NotNull long id,
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailChecked() {
        return emailChecked;
    }

    public void setEmailChecked(Boolean emailChecked) {
        this.emailChecked = emailChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static class PointComparator implements Comparator<User> {

        @Override
        public int compare(User user, User t1) {
            return Long.compare(user.getPoints(), t1.getPoints());
        }
    }
}
