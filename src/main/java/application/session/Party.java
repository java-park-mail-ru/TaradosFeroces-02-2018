package application.session;

import application.models.User;
import application.utils.responses.UserPartyView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Party {

    private final UserPartyView leader;

    private final ArrayList<UserPartyView> users = new ArrayList<>();

    public Party(@NotNull User leader) {
        this.leader = new UserPartyView(leader);
    }

    public void addUser(@NotNull User user) {
        final UserPartyView userPartyView = new UserPartyView(user);
        if (!users.contains(userPartyView)) {
            users.add(new UserPartyView(user));
        }
    }

    public void removeUser(@NotNull User user) {
        users.removeIf(userView -> (userView.getId().equals(user.getId())));
    }

    public UserPartyView getLeader() {
        return leader;
    }

    public ArrayList<UserPartyView> getUsers() {
        return users;
    }

    public ArrayList<Long> getAllIds() {
        return Stream.concat(
                users.stream()
                .map(UserPartyView::getId),
                Stream.of(leader.getId())
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    public int size() {
        return users.size() + 1;
    }

    public boolean isLeader(@NotNull User user) {
        return this.leader.getId() == user.getId();
    }

}
