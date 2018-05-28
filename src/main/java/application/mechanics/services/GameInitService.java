package application.mechanics.services;


import application.mechanics.base.Position;
import application.mechanics.game.Avatar;
import application.mechanics.game.spells.Spell;
import application.mechanics.messages.out.AskForJoinGame;
import application.mechanics.messages.out.AskForJoinGameTimeLeft;
import application.mechanics.messages.out.GamePrepare;
import application.models.User;
import application.models.id.Id;
import application.party.Party;
import application.party.Player;
import application.services.AccountService;
import application.websockets.RemotePointService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static application.mechanics.GameConfig.PARTY_WAITING_TIME;


@Service
public class GameInitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameInitService.class);

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private static class PartyWaiters {
        @NotNull
        private final Party party;

        @NotNull
        private final ArrayList<Avatar> avatars;

        private final long initTime;

        private volatile boolean isALive;

        private Set<Long> confirms = ConcurrentHashMap.newKeySet();
        private Set<Long> readiness = ConcurrentHashMap.newKeySet();

        private boolean isNotifiedAboutFull;

        PartyWaiters(@NotNull Party party, @NotNull ArrayList<Avatar> avatars) {
            this.party = party;
            this.avatars = avatars;
            this.initTime = Clock.systemDefaultZone().millis();
            this.isALive = true;
            this.isNotifiedAboutFull = false;
        }

        @NotNull
        ArrayList<Avatar> getAvatars() {
            return avatars;
        }

        boolean isALive() {
            return isALive || isFull();
        }

        boolean isFull() {
            return confirms.size() == party.getAllIds().size();
        }

        boolean isAllReady() {
            return readiness.size() == party.getAllIds().size();
        }

        boolean isNotifiedAboutFull() {
            return isNotifiedAboutFull;
        }

        void setNotifiedAboutFull() {
            isNotifiedAboutFull = true;
        }

        int confirmsCount() {
            return confirms.size();
        }

        int readinessCount() {
            return readiness.size();
        }


        void addConfirmation(long userId) {
            if (!confirms.contains(userId) && party.getAllIds().contains(userId)) {
                confirms.add(userId);
            }
        }

        void addReadiness(long userId) {
            if (!readiness.contains(userId) && party.getAllIds().contains(userId)) {
                readiness.add(userId);
            }
        }

        void kill() {
            isALive = false;
        }

        long getTimeLeft(long currentTime) {
            long timeLeft = currentTime - initTime;
            return (timeLeft >= PARTY_WAITING_TIME) ? 0L : (PARTY_WAITING_TIME - timeLeft);
        }

        @NotNull
        Party getParty() {
            return party;
        }
    }

    @NotNull
    private final RemotePointService remotePointService;

    @NotNull
    private final AccountService accountService;

    @NotNull
    private final GameSessionService gameSessionService;

    @NotNull
    private final Map<Long, PartyWaiters> waitersForConfirm = new ConcurrentHashMap<>();

    @NotNull
    private final Map<Long, Long> usersToPartyMap = new ConcurrentHashMap<>();

    @NotNull
    private final ExecutorService executorService =
            Executors.newFixedThreadPool(1, action -> {
                final Thread thread = new Thread(action);
                thread.setName("Deadlinez-GameInitService");
                return thread;
            });

    public GameInitService(@NotNull RemotePointService remotePointService,
                           @NotNull AccountService accountService,
                           @NotNull GameSessionService gameSessionService) {
        this.remotePointService = remotePointService;
        this.accountService = accountService;
        this.gameSessionService = gameSessionService;
    }


    @PostConstruct
    public void initAfterStartup() {
        executorService.execute(new WaitersWorker(
                remotePointService,
                waitersForConfirm,
                gameSessionService,
                usersToPartyMap
        ));
    }

    public synchronized void addConfirmation(@NotNull Id<User> userId) {
        LOGGER.info("addConfirmation:");

        final Long partyId = usersToPartyMap.getOrDefault(userId.asLong(), null);
        if (partyId == null) {
            LOGGER.info("    : there is no partyId for user.id=" + userId.asLong());
            return;
        }

        final PartyWaiters partyWaiters = waitersForConfirm.getOrDefault(partyId, null);
        if (partyWaiters == null) {
            LOGGER.error("    : there is no party for partyId.id=" + partyId);
            return;
        }
        partyWaiters.addConfirmation(userId.asLong());
    }

    public synchronized void addReadiness(@NotNull Id<User> userId) {
        LOGGER.info("addReadiness:");

        final Long partyId = usersToPartyMap.getOrDefault(userId.asLong(), null);
        if (partyId == null) {
            LOGGER.info("    : there is no partyId for user.id=" + userId.asLong());
            return;
        }

        final PartyWaiters partyWaiters = waitersForConfirm.getOrDefault(partyId, null);
        if (partyWaiters == null) {
            LOGGER.error("    : there is no party for partyId.id=" + partyId);
            return;
        }
        partyWaiters.addReadiness(userId.asLong());
    }

    public void startInitializationGameFor(@NotNull Party party) {

        final long partyId = ID_GENERATOR.incrementAndGet();

        ArrayList<Avatar> avatars = new ArrayList<>();
        final ArrayList<Player> players = party.getAllUsers();

        for (int i = 0; i < players.size(); ++i) {
            final User user = accountService.getUserById(players.get(i).getId());
            if (user == null) {
                LOGGER.warn("initGameFor: user with id=" + players.get(i).getId() + " does not found");
                LOGGER.info("           : break");
                return;
            }

            avatars.add(new Avatar(players.get(i).getPartyId(), user, new Position(0, 0), new Spell()));
        }

        for (Avatar avatar : avatars) {
            try {
                remotePointService.sendMessageToUser(
                        avatar.getUser().getUserId(),
                        new AskForJoinGame(party, partyId)
                );
            } catch (IOException e) {
                avatars.forEach(playerToCutOff -> remotePointService.interruptConnection(
                        playerToCutOff.getUser().getUserId(),
                        CloseStatus.SERVER_ERROR)
                );
                LOGGER.error("Unable to start a game", e);
            }
        }

        LOGGER.info("before inserting party: waitersForConfirm.size=" + waitersForConfirm.size());

        waitersForConfirm.put(partyId, new PartyWaiters(party, avatars));

        avatars.forEach(avatar -> usersToPartyMap.put(avatar.getId(), partyId));

        LOGGER.info("after inserting party: parties.size=" + waitersForConfirm.size());
    }



    private static class WaitersWorker implements Runnable {

        private static final Logger LOGGER = LoggerFactory.getLogger(WaitersWorker.class);

        @NotNull
        private final RemotePointService remotePointService;

        @NotNull
        private final GameSessionService gameSessionService;

        @NotNull
        private final Map<Long, PartyWaiters> waitersForConfirm;

        @NotNull
        private final Map<Long, Long> usersToPartyMap;

        WaitersWorker(@NotNull RemotePointService remotePointService,
                      @NotNull Map<Long, PartyWaiters> waitersForConfirm,
                      @NotNull GameSessionService gameSessionService,
                      @NotNull Map<Long, Long> usersToPartyMap) {
            LOGGER.info("initializing worker for GameInitService");

            this.waitersForConfirm = waitersForConfirm;
            this.remotePointService = remotePointService;
            this.gameSessionService = gameSessionService;
            this.usersToPartyMap = usersToPartyMap;
        }

        private final Clock clock = Clock.systemDefaultZone();

        @Override
        public void run() {
            while (true) {
                final long before = clock.millis();

                waitersForConfirm.forEach((partyId, partyWaiter) -> {
                    if (!partyWaiter.isFull()) {
                        final long timeLeft = partyWaiter.getTimeLeft(clock.millis());
                        for (Long userId : partyWaiter.getParty().getAllIds()) {
                            try {
                                if (timeLeft % 500 < 100) {
                                    LOGGER.info("----time left message for user.id=" + userId);
                                    remotePointService.sendMessageToUser(
                                            Id.of(userId),
                                            new AskForJoinGameTimeLeft(partyId, timeLeft)
                                    );
                                }
                            } catch (IOException e) {
                                LOGGER.warn("IOException: e: " + e.getMessage());

                            }
                        }
                        if (timeLeft == 0L) {
                            partyWaiter.kill();
                        }
                    }
                });


                for (long id: waitersForConfirm.keySet()) {

                    final PartyWaiters partyWaiters = waitersForConfirm.get(id);

                    LOGGER.info("----");
                    LOGGER.info("    party.id=" + id
                            + " : " + partyWaiters.getAvatars().size()
                            + " > "       + partyWaiters.confirmsCount()
                            + " > " + partyWaiters.readinessCount()
                    );

                    if (partyWaiters.isAllReady()) {
                        LOGGER.info("    party is ready: id=" + id);

                        gameSessionService.createGameSessionForUsers(
                                partyWaiters.getAvatars()
                        );

                        LOGGER.info("     gameSession for part should have been created");

                        waitersForConfirm.remove(id);
                        partyWaiters.getAvatars().forEach(avatar -> usersToPartyMap.remove(avatar.getId()));

                    } else if (partyWaiters.isFull()) {

                        if (!partyWaiters.isNotifiedAboutFull()) {
                            for (Long userId : partyWaiters.getParty().getAllIds()) {
                                try {

                                    LOGGER.info("---- send message to game prepare" + userId);
                                    remotePointService.sendMessageToUser(
                                            Id.of(userId),
                                            new GamePrepare()
                                    );

                                } catch (IOException e) {
                                    LOGGER.warn("IOException: e: " + e.getMessage());

                                }
                            }

                            partyWaiters.setNotifiedAboutFull();
                        }
                    } else if (!partyWaiters.isALive()) {
                        waitersForConfirm.remove(id);
                        partyWaiters.getAvatars().forEach(avatar -> usersToPartyMap.remove(avatar.getId()));

                        LOGGER.info("     party with id=" + id + " has been removed");
                    }
                }


                final long after = clock.millis();
                MechanicsTimeService.sleep(100 - (after - before));

                if (Thread.currentThread().isInterrupted()) {
                    LOGGER.warn("TREAD IS INTERRUPTED");
                    return;
                }
            }
        }
    }
}