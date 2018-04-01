package application.controllers;


import application.dao.UserDAO;
import application.dao.UserDAO.UpdateStatus;
import application.models.User;
import application.models.id.Id;
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



@RestController
@CrossOrigin//(origins = {"http://tf-sand-server.herokuapp.com/", "http://localhost:8080/"})
@RequestMapping("/alexalone")
public class SessionController {
    private static final String JSON = MediaType.APPLICATION_JSON_UTF8_VALUE; // "application/json;charset=UTF-8"
    private static final String USER_ID = "Deadlinez-user-id";

    private UserDAO usersDataBase;

    public SessionController(UserDAO usersDataBase) {
        this.usersDataBase = usersDataBase;
    }

    @PostMapping(path = "/signup", consumes = JSON, produces = JSON)
    public ResponseEntity<Message> signup(@RequestBody UserSignUpRequest body, HttpSession httpSession) {

        final String login = body.getLogin();

        final Pair<UpdateStatus, Id<User>> updateStatusIdPair =
                usersDataBase.addUser(login, body.getEmail(), body.getPassword(), body.getName(), body.getAvatar());
        final UpdateStatus status = updateStatusIdPair.getArg1();

        switch (status) {
            case SUCCESS:
                httpSession.setAttribute(USER_ID, updateStatusIdPair.getArg2().asLong());
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new Message("User has signed up!"));

            case EMAIL_OR_LOGIN_CONFLICT:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User with this login/email exists"));

            case WRONG_ID:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User with this id does not exist"));

            default:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("if U C this, so, better call Soul!"));

        }
    }

    @PostMapping(path = "/signin", consumes = JSON, produces = JSON)
    public ResponseEntity<Message> signin(@RequestBody UserSignInRequest body, HttpSession httpSession) {

        final String login = body.getLogin();
        final User user = usersDataBase.getUserByLogin(login);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new Message("User is not exist"));
        }

        final Long id = user.getId();
        if (!usersDataBase.checkPassword(id, body.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Message("Wrong password"));
        }

        httpSession.setAttribute(USER_ID, id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new Message("User has sighed in!"));
    }


    @GetMapping(path = "/me", produces = JSON)
    public ResponseEntity whoami(HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute(USER_ID);
        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        final User user = usersDataBase.getUserById(id);
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


    @PostMapping(path = "/score", consumes = JSON, produces = JSON)
    public ResponseEntity score(@RequestBody ScoreRequest body, HttpSession httpSession) {

        final long position = body.getPosition();
        final long count = body.getCount();

        ScoreData scoreData = usersDataBase.getTopUsers(count, position);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(scoreData);
    }

    @PostMapping(path = "/user/update", consumes = JSON, produces = JSON)
    public ResponseEntity update(@RequestBody HashMap<String, Object> body, HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute(USER_ID);

        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        final UpdateStatus updateStatus = usersDataBase.updateUser(id, body);

        switch (updateStatus) {
            case EMAIL_OR_LOGIN_CONFLICT:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User with this login/email exists"));
            case WRONG_ID:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("WTF id!"));
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
