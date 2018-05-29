package application.controllers;


import application.models.User;
import application.services.AccountService;
import application.services.FriendsService;
import application.services.NotificationService;
import application.utils.requests.AddFriend;
import application.utils.requests.FriendshipResponse;
import application.utils.requests.SelectUsersByLoginPrefix;
import application.utils.responses.Message;

import application.utils.responses.UserView;
import application.websockets.RemotePointService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.List;


@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping(
        path = BaseController.API_PATH + "/user/friend",
        produces = BaseController.JSON
)
public class FriendsController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FriendsController.class);

    @NotNull private FriendsService friendsService;
    @NotNull private AccountService accountService;
    @NotNull private NotificationService notificationService;
    @NotNull private RemotePointService remotePointService;

    public FriendsController(@NotNull FriendsService friendsService,
                             @NotNull AccountService accountService,
                             @NotNull NotificationService notificationService,
                             @NotNull RemotePointService remotePointService) {
        this.friendsService = friendsService;
        this.accountService = accountService;
        this.notificationService = notificationService;
        this.remotePointService = remotePointService;
    }


    @PostConstruct
    void init() {
        LOGGER.info("Created!");
    }

    @PostMapping(path = "/add", consumes = BaseController.JSON)
    public ResponseEntity addFriend(@RequestBody AddFriend body, HttpSession httpSession) {

        LOGGER.info("/api/user/friend/add");

        final Long id = (Long) httpSession.getAttribute(USER_ID);

        if (id == null) {
            LOGGER.info("/user/addfriend: httpSession is not exit");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

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

        LOGGER.info("/user/addfriend: sender: login: " + user.getLogin() + ", id=" + user.getId());
        LOGGER.info("        intended friend: login: " + friend.getLogin() + ", id=" + friend.getId());

        final Long requestId = friendsService.addFriendshipRequest(user, friend);
        notificationService.askForFriendship(friend, user, requestId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Message("Friendship request has been sent"));
    }

    @PostMapping(path = "/all", consumes = BaseController.JSON)
    public ResponseEntity selectUserFriends(@RequestBody SelectUsersByLoginPrefix body, HttpSession httpSession) {

        LOGGER.info("/user/friend/all: prefix='" + body.getPrefix() + "'");

        final Long id = (Long) httpSession.getAttribute(USER_ID);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        final User user = accountService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Message("User is forbidden"));
        }

        final List<UserView> allFriendsOfUser =
                friendsService.getAllFriendsOfUser(id, body);

        if (allFriendsOfUser == null || allFriendsOfUser.isEmpty()) {
            LOGGER.info("/user/friends: allFriendsOfUser is empty");

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new Message("You are alone"));
        }

        allFriendsOfUser.forEach(userView -> userView.setOnline(remotePointService.isConnected(userView.getId())));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allFriendsOfUser);
    }


    @PostMapping(path = "/response", consumes = BaseController.JSON)
    public ResponseEntity handleFriendshipResponse(@RequestBody FriendshipResponse body, HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute(USER_ID);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        final User user = accountService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Message("User is forbidden"));
        }

        if (body.getAnswer().equals(FriendshipResponse.Answers.ACCEPT)) {
            if (friendsService.handleFriendshipResponse(body.getRequestId(), user)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Message("User is successfully added in your friends list"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Message("Error"));
            }
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Message("User is ignored"));
    }
}
