package application.mechanics.services;


import application.mechanics.GameSession;
import application.mechanics.game.Scene;
import application.mechanics.game.Avatar;
import application.mechanics.messages.in.ClientSnap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClientSnapshotsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSnapshotsService.class.getSimpleName());

    @NotNull
    private final Map<Long, ArrayList<ClientSnap>> userToSnaps = new HashMap<>();


    public synchronized void pushClientSnap(long userId, @NotNull ClientSnap snap) {
        final ArrayList<ClientSnap> userSnaps = userToSnaps.computeIfAbsent(userId, u -> new ArrayList<>());
        userSnaps.add(snap);
    }

    @Nullable
    public synchronized ArrayList<ClientSnap> getSnapsForUser(long user) {
        return userToSnaps.get(user);
    }


    public void processSnapshotsFor(GameSession gameSession) {
        final Collection<Avatar> players = gameSession.getUsers();
        final Scene scene = gameSession.getScene();
        for (Avatar player: players) {
            final ArrayList<ClientSnap> playerSnaps = getSnapsForUser(player.getId());

            if (playerSnaps == null || playerSnaps.isEmpty()) {
                continue;
            }

            final ClientSnap lastSnap = playerSnaps.get(playerSnaps.size() - 1);
            player.setPosition(
                    scene.calcNextPosition(
                            player.getPosition(),
                            lastSnap.getMovement()
                    )
            );

            // CHECK COLLISIONS

            for (ClientSnap snap: playerSnaps) {
                if (!snap.doesPerformingSpell()) {
                    continue;
                }
                player.castSpell(gameSession);
            }
        }
    }

    public void clear() {
        userToSnaps.clear();
    }
}