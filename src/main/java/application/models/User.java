package application.models;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class User {

    @NotNull
    private Long id;

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

    public User(@NotNull Long id,
                @NotNull String login,
                @NotNull String password,
                @NotNull String email,
                @Nullable Boolean emailChecked,
                @Nullable String name) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
        this.emailChecked = emailChecked;
        this.name = name;
    }

    public Long getId() {
        return id;
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
}
