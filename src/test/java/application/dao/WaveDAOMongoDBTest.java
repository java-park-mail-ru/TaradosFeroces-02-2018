package application.dao;


import application.dao.implementations.mongodb.WaveDAOMongoDB;

import application.mechanics.base.Coordinates;
import application.models.Mob;
import application.models.Wave;
import application.models.id.Id;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;



@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class WaveDAOMongoDBTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaveDAOMongoDBTest.class);

    @Autowired
    private WaveDAOMongoDB waveDAOMongoDB;

    private  Mob[] mobs = {
            new Mob(2, 4, "Mob_1", "Evil"),

            new Mob(3, 6, "Mob_2", "Evil"),

            new Mob(5, 1, "Mob_3", "Evil"),

            new Mob(7, 14, "Mob_4", "Evil"),

            new Mob(11, 121, "Mob_5", "Evil"),

            new Mob(13, 1, "Mob_6", "Evil"),
    };

    private ArrayList<Mob> mobsArray = new ArrayList<>();

    @PostConstruct
    void init() {
        mobsArray.addAll(Arrays.asList(mobs));
    }

    @Test
    public void simpleInsertingTest() {
        waveDAOMongoDB.save(new Wave(
                Id.of(42L),
                Id.of(23L),
                Id.of(19L),
                new Date(),
                mobsArray,
                Stream.concat(
                        mobsArray.stream()
                                .map(mob -> mob.getMobStartState(new Coordinates(0.0,0.0), 3)),
                        mobsArray.stream()
                                .map(mob -> mob.getMobStartState(new Coordinates(mob.getLevel(),0.0), 41))
                ).collect(Collectors.toCollection(ArrayList::new))
        ));

        final Optional<Wave> waveOptional = waveDAOMongoDB.findById(Id.of(42L));

        assertTrue(waveOptional.isPresent());
        final Wave wave = waveOptional.get();

        assertEquals(Id.of(23L), wave.getParentId());
        assertEquals(mobsArray.size() * 2, wave.getMobStartStates().size());
        assertEquals(mobsArray.size(), wave.getMobs().size());

        assertEquals(mobsArray.get(0).getId(), wave.getMobs().get(0).getId());
    }
}
