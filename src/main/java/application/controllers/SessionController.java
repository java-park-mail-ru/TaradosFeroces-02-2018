package application.controllers;


import application.dao.UserDAO;
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

    private UserDAO usersDataBase;

    public SessionController(UserDAO usersDataBase) {
        this.usersDataBase = usersDataBase;
    }

    @PostMapping(path = "/signup", consumes = JSON, produces = JSON)
    public ResponseEntity signup(@RequestBody UserSignUpRequest body, HttpSession httpSession) {
        String login = body.getLogin();
        if (usersDataBase.getUserByLogin(login) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Message("User with this login exists."));
        }
        Long id = usersDataBase.addUser(login, body.getEmail(), body.getPassword(), body.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Message("User has signed up!"));
    }


}
