package application.mechanics;

import application.mechanics.messages.in.ClientSnap;
import application.mechanics.services.ClientSnapshotsService;
import application.mechanics.services.GameSessionService;
import application.mechanics.services.MechanicsTimeService;
import application.mechanics.services.ServerSnapshotsService;
import application.models.User;
import application.models.id.Id;
import application.services.AccountService;
import application.websockets.RemotePointService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


@Service
public class MechanicExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MechanicExecutor.class);

    @NotNull
    private AccountService accountService;

    @NotNull
    private ClientSnapshotsService clientSnapshotsService;

    @NotNull
    private ServerSnapshotsService serverSnapshotService;

    @NotNull
    private final GameSessionService gameSessionService;

    @NotNull
    private RemotePointService remotePointService;

    @NotNull
    private ObjectMapper objectMapper;

    private final ThreadFactory threadFactory = action -> {
        final Thread thread = new Thread(action);
        thread.setName("Deadlinez-mechanic");
        return thread;
    };

    @NotNull
    private final ExecutorService tickExecutors =
            Executors.newFixedThreadPool(GameConfig.THREADS_NUM, threadFactory);

    @NotNull
    private final GameMechanics[] gameMechanics = new GameMechanics[GameConfig.THREADS_NUM];


    public MechanicExecutor(@NotNull AccountService accountService,
                            @NotNull ClientSnapshotsService clientSnapshotsService,
                            @NotNull ServerSnapshotsService serverSnapshotService,
                            @NotNull GameSessionService gameSessionService,
                            @NotNull RemotePointService remotePointService,
                            @NotNull ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.clientSnapshotsService = clientSnapshotsService;
        this.serverSnapshotService = serverSnapshotService;
        this.gameSessionService = gameSessionService;
        this.remotePointService = remotePointService;
        this.objectMapper = objectMapper;
    }



    @PostConstruct
    public void init() {
        for (int i = 0; i < gameMechanics.length; ++i) {
            gameMechanics[i] = new GameMechanicsImpl(
                    clientSnapshotsService,
                    serverSnapshotService,
                    remotePointService,
                    gameSessionService,
                    objectMapper
            );
            final Runnable worker = new Worker(gameMechanics[i]);
            tickExecutors.execute(worker);
        }
    }



    public void addClientSnapshot(@NotNull Id<User> userId, @NotNull ClientSnap clientSnap) {
        for (GameMechanics gameMechanic: gameMechanics) {
            gameMechanic.addClientSnapshot(userId, clientSnap);
        }
    }

    public void interruptGameWithUser(@NotNull Id<User> userId) {
        for (GameMechanics gameMechanic: gameMechanics) {
            gameMechanic.interruptGameWithUser(userId);
        }
    }

    private static class Worker implements Runnable {

        private final GameMechanics gameMechanics;

        Worker(GameMechanics gameMechanics) {
            this.gameMechanics = gameMechanics;
        }

        private final Clock clock = Clock.systemDefaultZone();

        @Override
        public void run() {
            while (true) {
                final long before = clock.millis();

                gameMechanics.gmStep(0);

                final long after = clock.millis();
                MechanicsTimeService.sleep(GameConfig.STEP_TIME - (after - before));

                if (Thread.currentThread().isInterrupted()) {
                    gameMechanics.reset();
                    return;
                }
            }
        }
    }
}