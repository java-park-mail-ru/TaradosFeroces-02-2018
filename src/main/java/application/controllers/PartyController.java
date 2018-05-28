package application.controllers;


import application.models.User;
import application.party.Player;
import application.services.AccountService;
import application.services.FriendsService;
import application.services.NotificationService;
import application.services.PartyService;
import application.party.Party;
import application.party.messages.out.PartyView;
import application.utils.requests.PartyInvite;
import application.utils.requests.PartyInviteResponse;
import application.utils.responses.Message;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;


import static application.utils.requests.PartyInviteResponse.Answers.DECLINE;


@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping(
        path = BaseController.API_PATH + "/party",
        produces = BaseController.JSON
)
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

    @PostConstruct
    void init() {
        LOGGER.info("Created!");
    }

    @GetMapping("/get")
    public ResponseEntity getMyParty(HttpSession httpSession) {
        final Long id = (Long) httpSession.getAttribute(USER_ID);

        LOGGER.info("/api/party/get:");

        if (id == null) {
            LOGGER.info(LOG_TAB_1 + ": httpSession is not exit");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        LOGGER.info(LOG_TAB_1 + ": httpSession: id: " + id);

        final User user = accountService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Message("User is forbidden"));
        }

        final Party party = partyService.getParty(user);

        if (party == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Message("Party not found"));
        }

        LOGGER.info(LOG_TAB_1 + ": party: "
            + party.getAllUsers().stream()
            .map(Player::getLogin)
            .reduce("[ ", (line, string) -> (line + ", " + string))
            .concat(" ]")
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(new PartyView(party));
    }

    @PostMapping(path = "/invite", consumes = BaseController.JSON)
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


    @PostMapping(path = "/join", consumes = BaseController.JSON)
    public ResponseEntity inviteResponse(@RequestBody PartyInviteResponse body, HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute(USER_ID);
        LOGGER.info("/api/party/join <----------------------------");

        if (id == null) {
            LOGGER.warn(LOG_TAB_1 + ": httpSession is not exit");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        LOGGER.info(LOG_TAB_1 + ": id=" + id);

        final User user = accountService.getUserById(id);
        if (user == null) {
            LOGGER.warn(LOG_TAB_1 + ": user is null, id=" + id);

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Message("User is forbidden"));
        }

        LOGGER.info(LOG_TAB_1 + ": user: login: " + user.getLogin() + ", id=" + user.getId());


        final User leader = accountService.getUserByLogin(body.getLeader());
        if (leader == null) {
            LOGGER.warn(LOG_TAB_1 + ": leader is not found (.login=" + body.getLeader() + ")");

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Message("Leader is not authorized"));
        }

        LOGGER.info(LOG_TAB_1 + ": leader: login: " + leader.getLogin() + ", id=" + leader.getId());

        if (body.getAnswer().equals(DECLINE)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Message("Declined"));
        }

        if (partyService.addToPartyWithLeader(user, leader)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new Message("User -> party"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message("Party adding error"));
        }
    }
}