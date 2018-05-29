package application.services;


import application.dao.MobDAO;

import application.models.Mob;
import application.models.id.Id;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class MobService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobService.class);

    @NotNull
    private final MobDAO mobsDB;

    public MobService(@NotNull MobDAO mobsDB) {
        this.mobsDB = mobsDB;
    }

    @PostConstruct
    private void init() {
        LOGGER.info("created MobService object");
    }

    public enum AddStatus {
        SUCCESS,
        NAMES_CONFLICT,
        DB_ERROR
    }

    public enum SearchStatus {
        SUCCESS,
        NOT_FOUND,
        DB_ERROR
    }

    public static class OperationResult<S, R> {
        private final S status;
        private R result;

        public OperationResult(S status, R result) {
            this.status = status;
            this.result = result;
        }

        public S status() {
            return status;
        }

        public R result() {
            return result;
        }

        public void setResult(R result) {
            this.result = result;
        }

        public static <S, R> OperationResult<S, R> make(S status, R result) {
            return new OperationResult<>(status, result);
        }
    }


    public OperationResult<AddStatus, Id<Mob>> addMob(@NotNull String name, @Nullable String description, long level) {

        final Mob mobNameConflict;
        try {
            mobNameConflict = mobsDB.getMobByName(name);
        } catch (Exception e) {
            return OperationResult.make(AddStatus.DB_ERROR, null);
        }

        if (mobNameConflict != null) {
            return OperationResult.make(AddStatus.NAMES_CONFLICT, null);
        }

        try {
            final Id<Mob> mobId = mobsDB.addMob(name, level, description);
            LOGGER.info("addUser: mob has been successfully added to collection: " + mobId);
            return OperationResult.make(AddStatus.SUCCESS, mobId);

        } catch (Exception e) {
            LOGGER.error("addUser: error: " + e.getMessage());
            return OperationResult.make(AddStatus.DB_ERROR, null);
        }
    }

    public OperationResult<SearchStatus, Mob> getMobById(long id) {
        try {
            final Mob mob = mobsDB.getMobById(Id.of(id));
            if (mob == null) {
                LOGGER.info("getMobById: mob not found: id=" + id);
                return OperationResult.make(SearchStatus.NOT_FOUND, null);
            } else {
                LOGGER.info("getMobById: mob.name='" + mob.getName() + "'");
                return OperationResult.make(SearchStatus.SUCCESS, mob);
            }
        } catch (Exception e) {
            LOGGER.error("getMobById: error: " + e.getMessage());
            return OperationResult.make(SearchStatus.DB_ERROR, null);
        }
    }

    public OperationResult<SearchStatus, Mob> getMobByName(@NotNull String name) {
        try {
            final Mob mob = mobsDB.getMobByName(name);
            if (mob == null) {
                LOGGER.info("getMobByName: mob not found: name='" + name + "'");
                return OperationResult.make(SearchStatus.NOT_FOUND, null);
            } else {
                LOGGER.info("getMobByName: mob.id='" + mob.getId().asLong() + "'");
                return OperationResult.make(SearchStatus.SUCCESS, mob);
            }
        } catch (Exception e) {
            LOGGER.error("getMobByName: error: " + e.getMessage());
            return OperationResult.make(SearchStatus.DB_ERROR, null);
        }
    }


    public OperationResult<SearchStatus, List<Mob>> getMobsByNamePrefix(@NotNull String namePrefix) {
        try {
            final List<Mob> mobs = mobsDB.selectMobsByNamePrefix(namePrefix);
            if (mobs == null) {
                LOGGER.info("getMobsByNamePrefix: mobs not found: namePrefix='" + namePrefix + "'");
                return OperationResult.make(SearchStatus.NOT_FOUND, null);
            } else {
                LOGGER.info("getMobsByNamePrefix: mobs.size=" + mobs.size());
                return OperationResult.make(SearchStatus.SUCCESS, mobs);
            }
        } catch (Exception e) {
            LOGGER.error("getMobsByNamePrefix: error: " + e.getMessage());
            return OperationResult.make(SearchStatus.DB_ERROR, null);
        }
    }

}