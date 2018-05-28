package application.controllers;


import application.mechanics.services.GameInitService;
import application.models.User;
import application.party.Party;
import application.services.AccountService;
import application.services.PartyService;
import application.utils.requests.InitGameFromParty;
import application.utils.responses.Message;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "*", allowCredentials = "true")
@RequestMapping(
        path = BaseController.API_PATH + "/game",
        produces = BaseController.JSON
)
public class GameController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @NotNull
    private GameInitService gameInitService;

    @NotNull
    private AccountService accountService;

    @NotNull
    private PartyService partyService;


    public GameController(@NotNull GameInitService gameInitService,
                          @NotNull AccountService accountService,
                          @NotNull PartyService partyService) {
        this.gameInitService = gameInitService;
        this.accountService = accountService;
        this.partyService = partyService;
    }


    @PostConstruct
    void init() {
        LOGGER.info("Created!");
    }

    @PostMapping(path = "/party", consumes = BaseController.JSON)
    public ResponseEntity initGameFromParty(@RequestBody InitGameFromParty body, HttpSession httpSession) {

        final Long id = (Long) httpSession.getAttribute(USER_ID);
        LOGGER.info("/api/game/party");

        if (id == null) {
            LOGGER.warn(LOG_TAB_1 + ": httpSession is not exist");

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

        final Party party = partyService.getPartyWithLeader(leader.getId());
        if (party == null) {
            LOGGER.warn(LOG_TAB_1 + ": party not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Message("Leader is not authorized"));
        }

        LOGGER.info(LOG_TAB_1 + ": party: "
                + party.getAllIds().stream()
                .sorted()
                .map(String::valueOf)
                .reduce("", (line, additional) -> (line + additional))
        );

        gameInitService.startInitializationGameFor(party);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new Message("User -> party"));
    }
}
