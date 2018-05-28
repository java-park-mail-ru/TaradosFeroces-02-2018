package application.services;


import application.models.Mob;
import application.models.id.Id;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MobServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobServiceTest.class);

    @Autowired
    MobService mobService;

    private static class MobInfo {

        @NotNull
        private final String name;

        @Nullable
        private final String description;

        private final long level;

        MobInfo(@NotNull String name, @Nullable String description, long level) {
            this.name = name;
            this.description = description;
            this.level = level;
        }

        @NotNull
        public String getName() {
            return name;
        }

        @Nullable
        public String getDescription() {
            return description;
        }

        public long getLevel() {
            return level;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            MobInfo mobInfo = (MobInfo) object;
            return level == mobInfo.level
                    && Objects.equals(name, mobInfo.name)
                    && Objects.equals(description, mobInfo.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, level);
        }

        @Override
        public String toString() {
            return "MobInfo{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", level=" + level +
                    '}';
        }
    }

    private  MobInfo[] mobs = {
            new MobInfo("Alexander", "Evil", 42),

            new MobInfo("Alexey", "Evil", 11),

            new MobInfo("Anton", "Evil", 11),

            new MobInfo("Antonio", "Lol magic", 10),
    };

    private Map<MobInfo, Id<Mob>> mobIdMap = null;

    @BeforeAll
    public void info() {
        LOGGER.info("created mobService: " + mobService);
        LOGGER.info("starting MobService Tests");
    }

    @Before
    public void fill() {

        mobIdMap = new HashMap<>();

        for (MobInfo mobInfo: mobs) {
            LOGGER.info("inserting " + mobInfo);

            final MobService.OperationResult<MobService.AddStatus, Id<Mob>> result =
                    mobService.addMob(mobInfo.getName(), mobInfo.getDescription(), mobInfo.getLevel());

            assertEquals(MobService.AddStatus.SUCCESS, result.status());
            mobIdMap.put(mobInfo, result.result());
        }
    }


    @Test
    public void simpleSelections() {

        mobIdMap.forEach((mobInfo, id) -> {
            final MobService.OperationResult<MobService.SearchStatus, Mob> result = mobService.getMobById(id.asLong());

            assertEquals(result.status(), MobService.SearchStatus.SUCCESS);

            assertEquals(result.result().getName(), mobInfo.getName());
            assertEquals(result.result().getLevel(), mobInfo.getLevel());
            assertEquals(result.result().getDescription(), mobInfo.getDescription());
        });

        mobIdMap.forEach((mobInfo, id) -> {
            final MobService.OperationResult<MobService.SearchStatus, Mob> result =
                    mobService.getMobByName(mobInfo.getName());

            assertEquals(result.status(), MobService.SearchStatus.SUCCESS);

            assertEquals(result.result().getId(), id);
            assertEquals(result.result().getName(), mobInfo.getName());
            assertEquals(result.result().getLevel(), mobInfo.getLevel());
            assertEquals(result.result().getDescription(), mobInfo.getDescription());
        });
    }

    @Test
    public void conflictAdding() {

        final MobInfo conflictMobInfo = new MobInfo("Alexey", "NotEvil", 10);

        final MobService.OperationResult<MobService.AddStatus, Id<Mob>> result =
                mobService.addMob(
                        conflictMobInfo.getName(),
                        conflictMobInfo.getDescription(),
                        conflictMobInfo.getLevel()
                );

        assertEquals(result.status(), MobService.AddStatus.NAMES_CONFLICT);
        assertNull(result.result());
    }
}
