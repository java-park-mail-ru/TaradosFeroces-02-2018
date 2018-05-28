package application.mechanics.services;


import application.mechanics.GameConfig;
import application.mechanics.GameSession;
import application.mechanics.base.Position;
import application.mechanics.game.Scene;
import application.mechanics.game.Avatar;
import application.mechanics.messages.out.EndGame;
import application.mechanics.messages.out.InitGame;
import application.models.id.Id;

import application.websockets.RemotePointService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class GameSessionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSessionService.class);

    @NotNull
    private final Map<Long, GameSession> usersMap = new HashMap<>();
    @NotNull
    private final Set<GameSession> gameSessions = new LinkedHashSet<>();

    @NotNull
    private final RemotePointService remotePointService;

    public GameSessionService(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    @NotNull
    public Set<GameSession> getSessions() {
        return gameSessions;
    }

    @Nullable
    public GameSession getSessionForUser(long userId) {
        return usersMap.getOrDefault(userId, null);
    }

    public boolean isPlaying(long userId) {
        return usersMap.containsKey(userId);
    }

    public void forceTerminate(@NotNull GameSession gameSession) {
        final ArrayList<Avatar.EndAvatarSnap> snaps =
                gameSession.getUsers().stream()
                        .map(Avatar::getEndSnap)
                        .collect(Collectors.toCollection(ArrayList::new));

        EndGame endGameMessage = new EndGame(snaps);

        for (Avatar avatar: gameSession.getUsers()) {
            try {
                remotePointService.sendMessageToUser(
                        avatar.getUser().getUserId(),
                        endGameMessage
                );
            } catch (IOException e) {
                LOGGER.warn("Error while sending ending avatar for id=" + avatar.getId());
            }

            usersMap.remove(avatar.getId());
        }

        gameSessions.remove(gameSession);
    }

    synchronized void createGameSessionForUsers(@NotNull ArrayList<Avatar> avatars) {
        LOGGER.info("createGameSessionForUsers:");

        final GameSession gameSession = new GameSession(
                avatars,
                new Scene(),
                new MechanicsTimeService(),
                this
        );

        LOGGER.info("        : created gameSession.id=" + gameSession.getSessionId().asLong());

        gameSessions.add(gameSession);
        LOGGER.info("        : added gameSession.id=" + gameSession.getSessionId().asLong());

        gameSession.getUsers().stream()
                .map(Avatar::getId)
                .forEach(id -> usersMap.put(id, gameSession));
        LOGGER.info("        : users -> gameSession.id=" + gameSession.getSessionId().asLong());


        Scene scene = new Scene();
        LOGGER.info("        : scene.w, scene.h = " + scene.getWidth() + scene.getHeight());

        final ArrayList<Avatar> users = gameSession.getUsers();
        LOGGER.info("        : users.size=" + users.size());

        for (int i = 0; i < users.size(); i++) {
            users.get(i).setPosition(
                    new Position(GameConfig.INIT_RADIUS, i, avatars.size(),
                            scene.getWidth(), scene.getHeight())
            );

            LOGGER.info("        : users[" + i + "].pos= "
                    + users.get(i).getPosition().getPosX()
                    + " : "
                    + users.get(i).getPosition().getPosY()
            );
        }

        InitGame initGameMessage = new InitGame(
                gameSession.getUsers().stream()
                .map(Avatar::getInitialSnap)
                .collect(Collectors.toCollection(ArrayList::new)),
                scene
        );

        LOGGER.info("        : initGameMessage: users.count()=" + initGameMessage.getUsersCount());

        for (Avatar avatar: gameSession.getUsers()) {
            try {
                remotePointService.sendMessageToUser(avatar.getUser().getUserId(), initGameMessage);
            } catch (IOException e) {
                LOGGER.warn("Error while sending initial avatar for id=" + avatar.getId());
            }
        }

        LOGGER.info("        : initGameMessages have been sent already");
    }


    public void notifyGameIsOver(@NotNull GameSession gameSession,
                                 @NotNull CloseStatus closeStatus) {

        final boolean exists = gameSessions.remove(gameSession);
        final Collection<Avatar> players = gameSession.getUsers();

        for (Avatar player: players) {
            usersMap.remove(player.getId());
            if (exists) {
                remotePointService.interruptConnection(Id.of(player.getId()), closeStatus);
            }
        }
        LOGGER.info("Game #{} is over, total rooms: {}", gameSession.getSessionId(), gameSessions.size());
    }
}