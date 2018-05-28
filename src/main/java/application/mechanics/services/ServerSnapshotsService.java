package application.mechanics.services;


import application.mechanics.GameSession;
import application.mechanics.game.Avatar;
import application.mechanics.messages.out.ServerSnap;
import application.models.id.Id;
import application.websockets.RemotePointService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;


@Service
public class ServerSnapshotsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSnapshotsService.class);

    @NotNull
    private final RemotePointService remotePointService;

    @NotNull
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ServerSnapshotsService(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void sendSnapshotsFor(@NotNull GameSession gameSession) {

        if (gameSession.getUsers().isEmpty()) {
            throw new RuntimeException("No players snaps");
        }
        final ArrayList<Avatar> avatars = gameSession.getUsers();

        final ServerSnap snap = new ServerSnap(avatars);

        try {
            for (int i = 0; i < avatars.size(); ++i) {
                snap.setIndex(i);
                remotePointService.sendMessageToUserQuiet(Id.of(avatars.get(i).getId()), snap);
            }
        } catch (IOException e) {
            LOGGER.error("Error sending server snap {}", e.getMessage());
        }

    }
}
