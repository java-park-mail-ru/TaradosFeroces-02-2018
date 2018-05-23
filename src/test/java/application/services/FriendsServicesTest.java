package application.services;


import application.models.User;
import application.models.id.Id;
import application.utils.omgjava.Pair;
import application.utils.requests.AddFriend;
import application.utils.requests.SelectUsersByLoginPrefix;
import application.utils.requests.UserSignUpRequest;
import application.utils.responses.UserView;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class FriendsServicesTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FriendsServicesTest.class);

    @Autowired
    FriendsService friendsService;

    @Autowired
    AccountService accountService;

    private final UserSignUpRequest[] users = {
        new UserSignUpRequest(
                "alexander", "a@mail.ru", "ju4f2tr29rq83", "Alexander Kuz",
                "UltraMegaCoolAvatar"
        ),
        new UserSignUpRequest(
                "alexey", "a@gmail.com", "43t9xu2Q9rq83", "Alexey B",
                "UltraMegaCoolAvatar"
        ),
        new UserSignUpRequest(
                "alice", "a@omg.ru", "j___________83", "Alice ABC",
                "UltraMegaCoolAvatar"
        ),
        new UserSignUpRequest(
                "alone_outcast", "ao@yandex.ru", "NF", "trrrrrrrrr",
                "UltraMegaCoolAvatar"
        ),
        new UserSignUpRequest(
                "frontender", "f@mail.ru", "reactjspromise", "NotAlexander",
                "UltraMegaCoolAvatar"
        ),
        new UserSignUpRequest(
                "katerina", "k@rambler.ru", "yepwerememberrabler", "Katyshka",
                "UltraMegaCoolAvatar"
        ),
    };

    private Map<UserSignUpRequest, Long> idUserMap = null;

    @BeforeAll
    public void info() {
        LOGGER.info("FriendsServicesTest: accountService: " + accountService);
        LOGGER.info("FriendsServicesTest: friendsService: " + friendsService);
        LOGGER.info("FriendsServicesTest: starting Friends Service Tests");
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


    @Test
    public void simpleAddFriend() throws FriendsService.UserDoesNotExist {

        assertFalse(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[1])));

        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[2].getLogin()));

        assertTrue(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[2])));
    }

    @Test
    public void addMoreThanOneFriends() throws FriendsService.UserDoesNotExist {

        assertFalse(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[1])));

        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[1].getLogin()));
        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[2].getLogin()));
        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[3].getLogin()));

        assertTrue(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[1])));
        assertTrue(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[2])));
        assertTrue(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[3])));
    }

    @Test
    public void selectAllFriends() throws FriendsService.UserDoesNotExist {

        assertFalse(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[1])));

        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[1].getLogin()));
        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[2].getLogin()));
        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[3].getLogin()));
        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[4].getLogin()));
        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[5].getLogin()));


        assertTrue(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[1])));
        assertTrue(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[3])));
        assertTrue(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[4])));

        final List<UserView> friends =
                friendsService.getAllFriendsOfUser(idUserMap.get(users[0]), new SelectUsersByLoginPrefix("al"));


        assertNotNull(friends);
        assertEquals(friends.size(), 3);

        int i = 1;
        for (UserView userView: friends) {
            assertEquals(userView.getLogin(), users[i++].getLogin());
        }

        final List<UserView> friends2 =
                friendsService.getAllFriendsOfUser(idUserMap.get(users[0]), new SelectUsersByLoginPrefix("ale"));


        assertNotNull(friends2);
        assertEquals(friends2.size(), 1);

        i = 1;
        for (UserView userView: friends2) {
            assertEquals(userView.getLogin(), users[i++].getLogin());
        }

        final List<UserView> friends3 =
                friendsService.getAllFriendsOfUser(idUserMap.get(users[0]), new SelectUsersByLoginPrefix("kate"));


        assertNotNull(friends3);
        assertEquals(friends3.size(), 1);

        i = 5;
        for (UserView userView: friends3) {
            assertEquals(userView.getLogin(), users[i++].getLogin());
        }

        final List<UserView> friends4 =
                friendsService.getAllFriendsOfUser(idUserMap.get(users[0]), new SelectUsersByLoginPrefix("qu"));


        assertNotNull(friends4);
        assertEquals(friends4.size(), 0);
    }

    @Test
    public void selectFriendsByPrefix() throws FriendsService.UserDoesNotExist {

        assertFalse(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[1])));

        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[1].getLogin()));
        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[2].getLogin()));
        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[3].getLogin()));
        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[4].getLogin()));
        friendsService.addFriend(idUserMap.get(users[0]), new AddFriend(users[5].getLogin()));

        friendsService.addFriend(idUserMap.get(users[2]), new AddFriend(users[3].getLogin()));
        friendsService.addFriend(idUserMap.get(users[2]), new AddFriend(users[5].getLogin()));

        assertTrue(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[1])));
        assertTrue(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[2])));
        assertTrue(friendsService.areTheyFriends(idUserMap.get(users[0]), idUserMap.get(users[3])));

        final List<UserView> allFriends =
                friendsService.getAllFriendsOfUser(idUserMap.get(users[0]), new SelectUsersByLoginPrefix(""));


        assertNotNull(allFriends);

        int i = 1;
        for (UserView userView: allFriends) {
            assertEquals(userView.getLogin(), users[i++].getLogin());
        }
    }
}
