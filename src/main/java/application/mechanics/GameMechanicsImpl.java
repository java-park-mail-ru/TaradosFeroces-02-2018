package application.mechanics;

import application.mechanics.game.Avatar;
import application.mechanics.messages.in.ClientSnap;
import application.mechanics.services.ClientSnapshotsService;
import application.mechanics.services.GameSessionService;
import application.mechanics.services.ServerSnapshotsService;
import application.models.User;
import application.models.id.Id;
import application.websockets.RemotePointService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


@Service
public class GameMechanicsImpl implements GameMechanics {
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMechanics.class.getSimpleName());

    @NotNull
    private final ClientSnapshotsService clientSnapshotsService;
    @NotNull
    private final ServerSnapshotsService serverSnapshotService;
    @NotNull
    private final RemotePointService remotePointService;
    @NotNull
    private final GameSessionService gameSessionService;

    @NotNull
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    @NotNull
    private final ObjectMapper objectMapper;

    public GameMechanicsImpl(@NotNull ClientSnapshotsService clientSnapshotsService,
                             @NotNull ServerSnapshotsService serverSnapshotService,
                             @NotNull RemotePointService remotePointService,
                             @NotNull GameSessionService gameSessionService,
                             @NotNull ObjectMapper objectMapper) {
        this.clientSnapshotsService = clientSnapshotsService;
        this.serverSnapshotService = serverSnapshotService;
        this.remotePointService = remotePointService;
        this.gameSessionService = gameSessionService;
        this.objectMapper = objectMapper;
    }


    @Override
    public void addClientSnapshot(@NotNull Id<User> userId, @NotNull ClientSnap clientSnap) {
        LOGGER.info("added client snapshot: " + userId.asLong() + " -> " + clientSnap.getMovement().toString());
        tasks.add(() -> clientSnapshotsService.pushClientSnap(userId.asLong(), clientSnap));
    }

    @Override
    public void interruptGameWithUser(@NotNull Id<User> userId) {
        final GameSession gameSession = gameSessionService.getSessionForUser(userId.asLong());

        if (gameSession != null) {
            gameSession.interrupt();
        }
    }


    @Override
    public void gmStep(long frameTime) {

        while (!tasks.isEmpty()) {
            final Runnable nextTask = tasks.poll();
            if (nextTask == null) {
                continue;
            }
            try {
                nextTask.run();
            } catch (RuntimeException ex) {
                LOGGER.error("Cant handle game task", ex);
            }
        }

        for (GameSession session : gameSessionService.getSessions()) {
            clientSnapshotsService.processSnapshotsFor(session);
        }

        final Iterator<GameSession> iterator = gameSessionService.getSessions().iterator();
        final Collection<GameSession> sessionsToTerminate = new ArrayList<>();

        while (iterator.hasNext()) {
            final GameSession session = iterator.next();
            final long aliveUsersCount = session.getUsers().stream().filter(Avatar::isAlive).count();
            if (aliveUsersCount == 0) {
                gameSessionService.notifyGameIsOver(session, CloseStatus.NORMAL);
                continue;
            }
            try {
                serverSnapshotService.sendSnapshotsFor(session);
            } catch (RuntimeException ex) {
                sessionsToTerminate.add(session);
                LOGGER.error("Session was terminated!");
            }
            sessionsToTerminate.forEach(s -> gameSessionService.notifyGameIsOver(s, CloseStatus.SERVER_ERROR));
        }

        /*
        while (!waiters.isEmpty()) {
            final Long candidate = waiters.poll();
            if (!insureCandidate(candidate)) {
                continue;
            }
            final User newPlayer = accountService.getUser(candidate);
            gameSessionService.addNewPlayer(newPlayer);
        }
        */

        clientSnapshotsService.clear();
    }

    @Override
    public void reset() {
        LOGGER.info("Reset");
    }


}
