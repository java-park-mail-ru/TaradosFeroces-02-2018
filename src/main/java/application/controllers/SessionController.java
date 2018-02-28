package application.controllers;


import application.dao.UserDAO;
import application.models.User;
import application.utils.requests.UserSignInRequest;
import application.utils.requests.UserSignUpRequest;
import application.utils.responses.Message;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;


@RestController
@CrossOrigin/*(origins = {"http://tf-sand-server.herokuapp.com/", "localhost"})*/
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
        String login = body.getLogin();
        if (usersDataBase.getUserByLogin(login) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Message("User with this login exists"));
        }
        final Long id = usersDataBase.addUser(login, body.getEmail(), body.getPassword(), body.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Message("User has signed up!"));
    }

    @PostMapping(path = "/signin", consumes = JSON, produces = JSON)
    public ResponseEntity<Message> signin(@RequestBody UserSignInRequest body, HttpSession httpSession) {

        final String login = body.getLogin();

        final User user = usersDataBase.getUserByLogin(login);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Message("User is not exist"));
        }
        final Long id = user.getId();
        if (!usersDataBase.checkPassword(id, body.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Message("Wrong password"));
        }
        // httpSession.setAttribute(USER_ID, id);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Set-Cookie", USER_ID + "=" + id.toString())
                .body(new Message("User has sighed in!"));
    }

    @PostMapping(path = "/signout", consumes = JSON, produces = JSON)
    public ResponseEntity<Message> signout(@RequestBody UserSignInRequest body, HttpSession httpSession) {

        final String login = body.getLogin();
        final User user = usersDataBase.getUserByLogin(login);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Message("User is not exist"));
        }
        final Long id = user.getId();
        // httpSession.setAttribute(USER_ID, id);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Set-Cookie", USER_ID + "=")
                .body(new Message("User logout!"));
    }




}
