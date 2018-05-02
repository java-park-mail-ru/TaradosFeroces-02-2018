package application.controllers;


import application.models.User;
import application.models.id.Id;
import application.services.AccountService;
import application.utils.omgjava.Pair;
import application.utils.requests.ScoreRequest;
import application.utils.requests.UserSignInRequest;
import application.utils.requests.UserSignUpRequest;
import application.utils.responses.Message;
import application.utils.responses.ScoreData;
import application.utils.responses.UserFullInfo;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin//(origins = {"http://tf-sand-server.herokuapp.com/", "http://localhost:8080/"})
@RequestMapping("/api")
public class UserController {
    private static final String JSON = MediaType.APPLICATION_JSON_UTF8_VALUE; // "application/json;charset=UTF-8"
    private static final String USER_ID = "Deadlinez_user_id";

    private AccountService accountService;

    public UserController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(path = "/signup", consumes = JSON, produces = JSON)
    public ResponseEntity<Message> signup(@RequestBody UserSignUpRequest body, HttpSession httpSession) {

        final String login = body.getLogin();

        final Pair<AccountService.UpdateStatus, Id<User>> updateStatusIdPair =
                accountService.addUser(body);

        final AccountService.UpdateStatus status = updateStatusIdPair.getArg1();

        switch (status) {
            case SUCCESS:
                httpSession.setAttribute(USER_ID, updateStatusIdPair.getArg2().asLong());
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new Message("User has successfully registered!"));

            case EMAIL_CONFLICT:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User with same email exists"));

            case LOGIN_CONFLICT:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User with same login exists"));

            case EMAIL_AND_LOGIN_CONFLICT:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User with same login and same email exists"));
            default:
                return ResponseEntity
                        .status(500)
                        .body(new Message("if U C this, so, better call Soul!"));

        }
    }

    @PostMapping(path = "/signin", consumes = JSON, produces = JSON)
    public ResponseEntity<Message> signin(@RequestBody UserSignInRequest body, HttpSession httpSession) {

        final Pair<AccountService.AuthCheckStatus, Id<User>> statusIdPair =
                accountService.checkSignin(body.getLogin(), body.getPassword());


        switch (statusIdPair.getArg1()) {
            case SUCCESS:
                httpSession.setAttribute(USER_ID, statusIdPair.getArg2().asLong());
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new Message("User has sighed in"));

            case USER_NOT_EXISTS:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User does nit exist"));

            case WRONG_PASSWORD:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("Wrong password"));
            default:
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Message("Wow, h...how?!c "));
        }

    }


    @GetMapping(path = "/user", produces = JSON)
    public ResponseEntity whoami(HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute(USER_ID);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        final User user = accountService.getUserById(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Message("User is deleted."));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserFullInfo(user));
    }

    @PostMapping(path = "/signout", produces = JSON)
    public ResponseEntity<Message> signout(HttpSession httpSession) {

        if (httpSession.getAttribute(USER_ID) == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        httpSession.removeAttribute(USER_ID);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new Message("User have gone!"));
    }


    @PostMapping(path = "/leaderboard", consumes = JSON, produces = JSON)
    public ResponseEntity score(@RequestBody ScoreRequest body, HttpSession httpSession) {

        final long position = body.getPosition();
        final long count = body.getCount();

        ScoreData scoreData = accountService.getTopUsers(count, position);

        if (scoreData == null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message("Empty list"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(scoreData);
    }


    @GetMapping(path = "/isauthorized", produces = JSON)
    public ResponseEntity isAuthorized(HttpSession httpSession) {
        final Long id = (Long) httpSession.getAttribute(USER_ID);

        Map<String, Boolean> map = new HashMap<>();
        map.put("is_authorized", id != null);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(map);
    }

    @PostMapping(path = "/user/update", consumes = JSON, produces = JSON)
    public ResponseEntity update(@RequestBody HashMap<String, Object> body, HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute(USER_ID);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }


        final AccountService.UpdateStatus updateStatus = accountService.updateUser(id, body);

        switch (updateStatus) {
            case EMAIL_CONFLICT:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User with this email exists"));
            case LOGIN_CONFLICT:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User with this login exists"));
            case USER_NOT_EXISTS:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User does not exist"));
            case SUCCESS:
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new Message("User data successfully updated"));
            default:
                return ResponseEntity
                        .status(500)
                        .body(new Message("If you see this message, call Sanchez!"));

        }
    }

}
