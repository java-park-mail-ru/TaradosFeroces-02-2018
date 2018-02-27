package application.controllers;


import application.dao.UserDAO;
import application.utils.requests.UserSignInRequest;
import application.utils.responses.Message;

import org.springframework.http.MediaType;
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
    public Message signup(@RequestBody UserSignInRequest body, HttpSession httpSession) {
        Long id = usersDataBase.addUser(body.getLogin(), body.getEmail(), body.getPassword(), body.getName());

        System.out.println(id);
        System.out.println(usersDataBase.getUserByLogin(body.getLogin()).getEmail());

        return new Message("Created user " + usersDataBase.getUserById(id).getLogin());
    }
}
