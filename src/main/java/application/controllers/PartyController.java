package application.controllers;


import application.models.User;
import application.services.AccountService;
import application.services.FriendsService;
import application.services.NotificationService;
import application.services.PartyService;
import application.session.Party;
import application.session.messages.out.PartyView;
import application.utils.requests.PartyInvite;
import application.utils.requests.PartyInviteResponse;
import application.utils.responses.Message;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;

import static application.utils.requests.PartyInviteResponse.Answers.DECLINE;


@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping("/api/party")
public class PartyController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartyController.class);

    @NotNull
    private FriendsService friendsService;

    @NotNull
    private AccountService accountService;

    @NotNull
    private NotificationService notificationService;

    @NotNull
    private PartyService partyService;

    public PartyController(@NotNull FriendsService friendsService,
                           @NotNull AccountService accountService,
                           @NotNull NotificationService notificationService,
                           @NotNull PartyService partyService) {
        this.friendsService = friendsService;
        this.accountService = accountService;
        this.notificationService = notificationService;
        this.partyService = partyService;
    }

    @PostMapping(path = "/invite", consumes = JSON, produces = JSON)
    public ResponseEntity invite(@RequestBody PartyInvite body, HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute(USER_ID);

        if (id == null) {
            LOGGER.info("/api/party/invite: httpSession is not exit");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        LOGGER.info("                 : httpSession: id: " + id);


        final User user = accountService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Message("User is forbidden"));
        }

        final String friendLogin = body.getLogin();
        final User friend = accountService.getUserByLogin(friendLogin);
        if (friend == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Message("Intended friend does not exist"));
        }

        LOGGER.info("                 : sender: login: " + user.getLogin() + ", id=" + user.getId());
        LOGGER.info("        : intended friend: login: " + friend.getLogin() + ", id=" + friend.getId());


        boolean areTheyFriends = friendsService.areTheyFriends(user, friend);

        if (!areTheyFriends) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Message("They are not friends"));
        }

        boolean hasBeenSent = notificationService.askForPartyInvitation(friend, user);

        if (!hasBeenSent) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Message("Intended user is not on-line now"));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Message("User is ignored"));
    }


    @PostMapping(path = "/response", consumes = JSON, produces = JSON)
    public ResponseEntity inviteResponse(@RequestBody PartyInviteResponse body, HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute(USER_ID);
        LOGGER.info("/api/party/response <----------------------------");

        if (id == null) {
            LOGGER.warn("                   : httpSession is not exit");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }
        LOGGER.info("                   : id=" + id);

        final User user = accountService.getUserById(id);
        if (user == null) {
            LOGGER.warn("                   : user is null, id=" + id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Message("User is forbidden"));
        }
        LOGGER.info("                   : user: login: " + user.getLogin() + ", id=" + user.getId());


        final User leader = accountService.getUserByLogin(body.getLeader());
        if (leader == null) {
            LOGGER.warn("                   : leader is not found (.login=" + body.getLeader() + ")");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Message("Leader is not authorized"));
        }

        LOGGER.info("                   : leader: login: " + leader.getLogin() + ", id=" + leader.getId());

        if (body.getAnswer().equals(DECLINE)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Message("Declined"));
        }

        final Long partyLeaderId = partyService.getPartyLeaderId(user);

        if (partyLeaderId != null) {
            final Party oldParty = partyService.getParty(partyLeaderId);
            if (oldParty == null) {
                LOGGER.warn("                   : leader.id -> null party");
            } else {
                LOGGER.info("                   : leave old part (party.leader=" + oldParty.getLeader().getId() + ")");
                oldParty.removeUser(user);
                notificationService.sendLeavePartyNotification(user);
            }
        }

        Party party = partyService.getParty(leader.getId());
        if (party == null) {
            partyService.createPartyWithLeader(leader);
            party = partyService.getParty(leader.getId());

            if (party == null) {
                LOGGER.warn("                   : leader.id -> null party (leader.id=" + leader.getId() + ")");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Message("Party has not been created"));

            } else {
                LOGGER.info("                   : created party with leader (leader.id=" + leader.getId() + ")");
            }
        }

        party.addUser(user);

        final PartyView partyView = new PartyView(party);

        final ArrayList<Long> allIds = party.getAllIds();
        for (Long userId: allIds) {
            notificationService.sendMessage(userId, partyView);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Message("User -> party"));
    }
}