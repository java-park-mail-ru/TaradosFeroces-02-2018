package application.controllers;


import application.dao.UserDAO;
import application.models.User;
import application.utils.requests.ScoreRequest;
import application.utils.requests.UserSignInRequest;
import application.utils.requests.UserSignUpRequest;
import application.utils.requests.UserUpdateRequest;
import application.utils.responses.Message;
import application.utils.responses.ScoreData;
import application.utils.responses.UserFullInfo;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
//import javax.servlet.http.HttpSession;
import java.util.List;


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
    public ResponseEntity<Message> signup(@RequestBody UserSignUpRequest body, ServletContext httpSession) {

        final String login = body.getLogin();

        if (usersDataBase.getUserByLogin(login) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new Message("User with this login exists"));
        }

        final long id = usersDataBase.addUser(login, body.getEmail(), body.getPassword(), body.getName(), body.getAvatar()).asLong();

        httpSession.setAttribute(USER_ID, id);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Message("User has signed up!"));
    }

    @PostMapping(path = "/signin", consumes = JSON, produces = JSON)
    public ResponseEntity<Message> signin(@RequestBody UserSignInRequest body, ServletContext httpSession) {

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
                //.header("Set-Cookie", USER_ID + "=" + id.toString())
                .body(new Message("User has sighed in!"));
    }


    @GetMapping(path = "/me", produces = JSON)
    public ResponseEntity whoami(ServletContext httpSession) {

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
    public ResponseEntity<Message> signout(ServletContext httpSession) {

        if (httpSession.getAttribute(USER_ID) == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        httpSession.removeAttribute(USER_ID);

        return ResponseEntity
                .status(HttpStatus.OK)
                //.header("Set-Cookie", USER_ID + "=")
                .body(new Message("User have gone!"));
    }


    @PostMapping(path = "/score", consumes = JSON, produces = JSON)
    public ResponseEntity score(@RequestBody ScoreRequest body, ServletContext httpSession) {

        final long position = body.getPosition();
        final long count = body.getCount();

        final List users = usersDataBase.getAll();
        users.sort(new User.PointComparator());

        final List list = users.subList((int) position, (int) position + (int) count);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ScoreData(list));

    }

    @PostMapping(path = "/user/update", consumes = JSON, produces = JSON)
    public ResponseEntity update(@RequestBody UserUpdateRequest body, ServletContext httpSession) {

        final Long id = (Long) httpSession.getAttribute(USER_ID);
        if (id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new Message("User is not authorized"));
        }

        final UserDAO.UpdateInfo updateInfo = usersDataBase.updateUser(id, body.getLogin(), body.getEmail(), body.getAvatar());

        switch (updateInfo) {
            case LOGIN_EXIST:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User with this login exists"));
            case EMAIL_EXIST:
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new Message("User with this email exists"));
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
                        .status(HttpStatus.OK)
                        .body(new Message("If you see this message, call Sanchez!"));

        }
    }

}
