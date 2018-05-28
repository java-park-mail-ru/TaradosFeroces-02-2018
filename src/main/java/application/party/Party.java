package application.party;

import application.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Party {

    private final Player leader;

    private final ArrayList<Player> users = new ArrayList<>();

    public Party(@NotNull User leader) {
        this.leader = new Player(leader, 0);
    }

    public void addUser(@NotNull User user) {
        final Player userPartyView = new Player(user, users.size() + 1);

        if (!users.contains(userPartyView) && !leader.equals(userPartyView)) {
            users.add(userPartyView);
        }
    }

    public void removeUser(@NotNull User user) {
        users.removeIf(player -> (player.getId().equals(user.getId())));
    }

    public Player getLeader() {
        return leader;
    }

    public ArrayList<Player> getUsers() {
        return users;
    }

    public ArrayList<Player> getAllUsers() {
        return Stream.concat(
                users.stream(),
                Stream.of(leader)
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Long> getAllIds() {
        return Stream.concat(
                Stream.of(leader.getId()),
                users.stream()
                .map(Player::getId)
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    public int size() {
        return users.size() + 1;
    }

    public boolean isLeader(@NotNull User user) {
        return this.leader.getId() == user.getId();
    }

}
