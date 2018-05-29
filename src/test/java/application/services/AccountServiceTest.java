package application.services;


import application.models.User;
import application.models.id.Id;
import application.utils.omgjava.Pair;
import application.utils.requests.AddFriend;
import application.utils.requests.SelectUsersByLoginPrefix;
import application.utils.requests.UserSignUpRequest;
import application.utils.responses.ScoreData;
import application.utils.responses.UserView;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AccountServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceTest.class);


    @Autowired
    AccountService accountService;

    private final UserSignUpRequest[] users = {
            new UserSignUpRequest(
                    "alexander", "a@mail.ru", "ju4f2tr29rq83", "Alexander Kuz",
                    "UltraMegaCoolAhru9rh24vatar"
            ),
            new UserSignUpRequest(
                    "alexey", "a@gmail.com", "43t9xu2Q9rq83", "Alexey B",
                    "UltraMegar24ioCoolAvatar"
            ),
            new UserSignUpRequest(
                    "alice", "a@omg.ru", "j___________83", "Alice ABC",
                    "UltraM32e23eegaCoolAvatar"
            ),
            new UserSignUpRequest(
                    "alone_outcast", "ao@yandex.ru", "NF", "trrrrrrrrr",
                    "Ultraee3ee3MegaCoolAvatar"
            ),
            new UserSignUpRequest(
                    "frontender", "f@mail.ru", "reactjspromise", "NotAlexander",
                    "UltraMegaCoole3e3e3e3Avatar"
            ),
            new UserSignUpRequest(
                    "katerina", "k@rambler.ru", "yepwerememberrabler", "Katyshka",
                    "Ultre3e3e3eaMegaCoolAvatar"
            ),
    };

    private Map<UserSignUpRequest, Long> idUserMap = null;

    @BeforeAll
    public void info() {
        LOGGER.info("created accountService: " + accountService);
        LOGGER.info("----------->: starting Friends Service Tests");
    }

    @Before
    public void fill() {

        idUserMap = new HashMap<>();

        for (UserSignUpRequest user: users) {
            Pair<AccountService.UpdateStatus, Id<User>> updateStatusIdPair =
                    accountService.addUser(user);

            assertEquals(updateStatusIdPair.getArg1(), AccountService.UpdateStatus.SUCCESS);
            idUserMap.put(user, updateStatusIdPair.getArg2().asLong());
        }
    }

    void checkConsistancy(@NotNull UserSignUpRequest expected, User actual) {
        assertNotNull(actual);

        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getLogin(), actual.getLogin());
        assertEquals(expected.getAvatar(), actual.getAvatar());
    }

    @Test
    public void simpleGetById(){

        UserSignUpRequest user = users[0];

        final User userById = accountService.getUserById(idUserMap.get(user));

        checkConsistancy(user, userById);
    }

    @Test
    public void simpleGetByLogin(){

        UserSignUpRequest user = users[3];

        final User userByLogin = accountService.getUserByLogin(user.getLogin());

        checkConsistancy(user, userByLogin);
    }


    @Test
    public void checkPassword(){

        for (UserSignUpRequest user: users) {
            final Pair<AccountService.AuthCheckStatus, Id<User>> authCheckStatusIdPair =
                    accountService.checkSignin(user.getLogin(), user.getPassword());

            assertEquals(AccountService.AuthCheckStatus.SUCCESS, authCheckStatusIdPair.getArg1());
            assertEquals(Id.of(idUserMap.get(user)), authCheckStatusIdPair.getArg2());

            final Pair<AccountService.AuthCheckStatus, Id<User>> wrongAuthCheckStatusIdPair =
                    accountService.checkSignin(user.getLogin(), "Not a password");

            assertEquals(AccountService.AuthCheckStatus.WRONG_PASSWORD, wrongAuthCheckStatusIdPair.getArg1());
            assertNull(wrongAuthCheckStatusIdPair.getArg2());
        }

        final Pair<AccountService.AuthCheckStatus, Id<User>> authCheckStatusIdPair =
                accountService.checkSignin("Not a login", "pass");

        assertEquals(AccountService.AuthCheckStatus.USER_NOT_EXISTS, authCheckStatusIdPair.getArg1());
        assertNull(authCheckStatusIdPair.getArg2());

    }

    @Test
    public void userDoesNotExist(){

        Long noId = idUserMap.values().stream().reduce(0L, (lhs, rhs) -> (lhs + rhs));

        final User userById = accountService.getUserById(noId);

        assertNull(userById);
    }

    @Test
    public void getTopUsers(){

        for (UserSignUpRequest userSignUpRequest: users) {
            User user = accountService.getUserByLogin(userSignUpRequest.getLogin());
            assertNotNull(user);

            assert user.getName() != null;
            accountService.updateScore(user, (long) userSignUpRequest.getName().length());

            user = accountService.getUserByLogin(userSignUpRequest.getLogin());
            assertNotNull(user);

            assertEquals((long) userSignUpRequest.getName().length(), user.getPoints());
        }

        final ScoreData topUsers = accountService.getTopUsers(3, 1);

        assertEquals(3, topUsers.getArrayData().size());
    }


}
