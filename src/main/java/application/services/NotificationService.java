package application.services;

import application.models.User;
import application.models.id.Id;
import application.session.messages.out.AddAsFriendRequest;
import application.session.messages.out.InviteToParty;
import application.session.messages.out.LeaveParty;
import application.websockets.Message;
import application.websockets.RemotePointService;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @NotNull
    private final RemotePointService remotePointService;

    public NotificationService(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void askForFriendship(@NotNull User user, @NotNull User sender, Long requestId) {
        LOGGER.info("askForFriendship: user_id=" + user.getId()
                + ", sender_id=" + sender.getId()
                + ", request_id=" + requestId);

        try {
            if (remotePointService.isConnected(user.getUserId())) {
                remotePointService.sendMessageToUser(user.getUserId(), new AddAsFriendRequest(sender, requestId));
            } else {
                LOGGER.info("askForFriendship: user is not connected now");
            }
        } catch (IOException ex) {
            LOGGER.warn(
                    String.format("Failed to send askForFriendship message to user %s : ",
                            user.getId()), ex);
        }
    }

    public boolean askForPartyInvitation(@NotNull User user, @NotNull User leader) {
        LOGGER.info("askForPartyInvitation: user.id=" + user.getId() + ", leader.id=" + leader.getId());

        try {
            if (remotePointService.isConnected(user.getUserId())) {
                remotePointService.sendMessageToUser(user.getUserId(), new InviteToParty(leader));
            } else {
                LOGGER.info("                     : user is not connected now");
                return false;
            }

        } catch (IOException ex) {
            LOGGER.warn(
                    String.format("                     : Failed to send askForPartyInvitation message to user %s : ",
                            user.getId()), ex);
        }
        return true;
    }

    public boolean sendLeavePartyNotification(@NotNull User user) {
        LOGGER.info("sendLeavePartyNotification: user.id=" + user.getId());

        try {
            if (remotePointService.isConnected(user.getUserId())) {
                remotePointService.sendMessageToUser(user.getUserId(), new LeaveParty());
            } else {
                LOGGER.info("                     : user is not connected now");
            }

        } catch (IOException ex) {
            LOGGER.warn(
                    String.format("                     : Failed to send LeaveParty message to user %s : ",
                            user.getId()), ex);
        }

        return true;
    }

    public boolean sendMessage(@NotNull Long userId, @NotNull Message message) {
        LOGGER.info("sendMessage: user.id=" + userId);
        LOGGER.info("           : message.class=" + message.getClass().getName());

        try {
            if (remotePointService.isConnected(Id.of(userId))) {
                remotePointService.sendMessageToUser(Id.of(userId), message);
            } else {
                LOGGER.info("    : receiver is not connected now");
                return false;
            }

        } catch (IOException ex) {
            LOGGER.warn("    : IOException: message.class=" + message.getClass().getName());
        }

        return true;
    }
}
