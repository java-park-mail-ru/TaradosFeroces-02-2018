package application.dao.implementations.postgres;


import application.dao.MobDAO;
import application.models.Mob;
import application.models.id.Id;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Transactional
public class MobDAOPostgres implements MobDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobDAOPostgres.class);

    private static class Names {
        private static final String TABLE = "mobs";

        private static final String ID = "id";
        private static final String LEVEL = "level";
        private static final String NAME = "name";
        private static final String DESCRIPTION = "description";

        private static final String TABLE_INFO_LND = TABLE
                + "(" + LEVEL
                + ", " + NAME
                + ", " + DESCRIPTION
                + ")";
    }

    private static final RowMapper<Mob> MOB_MAPPER = (res, num) ->
            new Mob(
                    res.getLong(Names.ID),
                    res.getLong(Names.LEVEL),
                    res.getString(Names.NAME),
                    res.getString(Names.DESCRIPTION)
            );

    @Autowired
    private JdbcTemplate template;



    @Override
    public @NotNull Id<Mob> addMob(@NotNull String name, long level, @Nullable String description) throws Exception {
        final String query = "INSERT INTO " + Names.TABLE_INFO_LND + " VALUES(?,?,?) RETURNING " + Names.ID;

        try {
            return new Id<>(
                    template.queryForObject(query, Long.class,
                            level, name, (description == null ? "" : description)
                    )
            );

        } catch (DuplicateKeyException ex) {
            LOGGER.warn("DuplicateKeyException: " + ex.getMessage());
            throw new PostgresException(ex);

        } catch (BadSqlGrammarException ex) {
            LOGGER.warn("BadSqlGrammarException: " + ex.getMessage());
            throw new PostgresException(ex);
        }
    }

    protected @Nullable <T> T getMobByCondition(@NotNull String condition,
                                                @NotNull RowMapper<T> rowUserMapper,
                                                @Nullable Object... args) throws PostgresException {

        final String query = "SELECT * FROM " + Names.TABLE + " WHERE " + condition + ";";

        try {
            LOGGER.info("getMobByCondition: condition = " + condition);

            if (args != null) {
                LOGGER.info("getMobByCondition: args: length=" + args.length);
            } else {
                LOGGER.info("getMobByCondition: args=null");
            }

            final List<T> results = template.query(query, rowUserMapper, args);

            if (results.size() == 0) {
                LOGGER.info("getMobByCondition: result is null");
                return null;
            } else {
                final T result = results.get(0);
                LOGGER.info("getMobByCondition: result: " + result.toString());
                return result;
            }
        } catch (DataAccessException ex) {
            LOGGER.warn("getMobByCondition( " + query + " ) -> DataAccessException: " + ex.getMessage());
            LOGGER.info("getMobByCondition: result is null");
            throw new PostgresException(ex);
        }
    }

    @Override
    public @Nullable Mob getMobById(@NotNull Id<Mob> id) throws PostgresException {
        LOGGER.info("getUserById: args: " + id);
        return getMobByCondition(Names.ID + " = ?", MOB_MAPPER, id.asLong());
    }

    @Override
    public @Nullable Mob getMobByName(@NotNull String name) throws PostgresException {
        LOGGER.info("getUserById: args: " + name);
        return getMobByCondition("LOWER(" + Names.NAME + ") " + " = LOWER(?)", MOB_MAPPER, name);
    }

    @Override
    public @Nullable List<Mob> selectMobsByNamePrefix(@NotNull String namePrefix) {
        LOGGER.info("selectMobsByNamePrefix: loginPrefix='" + namePrefix + "'");

        try {
            final String queryWithoutPrefix =
                    "SELECT * FROM " + Names.TABLE + " ORDER BY LOWER(" + Names.NAME + ");";

            final String queryWithPrefix =
                    "SELECT * FROM " + Names.TABLE + " WHERE LOWER(" + Names.NAME + ") "
                            + "LIKE '" + namePrefix + "%' ORDER BY LOWER(" + Names.NAME + ");";

            List<Map<String, Object>> maps = null;

            if (namePrefix.equals("")) {
                LOGGER.info("selectMobsByNamePrefix: prefix is null");
                maps = template.queryForList(queryWithoutPrefix);
            } else {
                LOGGER.info("selectMobsByNamePrefix: prefix='" + namePrefix + "'");
                maps = template.queryForList(queryWithPrefix);
            }

            LOGGER.info("selectMobsByNamePrefix: found " + maps.size() + " mob(s)");

            List<Mob> mobs = maps.stream()
                    .map(map -> new Mob(
                            (Long) map.get(Names.ID),
                            (Long) map.get(Names.LEVEL),
                            (String) map.get(Names.NAME),
                            (String) map.getOrDefault(Names.DESCRIPTION, "")
                    ))
                    .collect(Collectors.toCollection(ArrayList::new));

            LOGGER.info("selectMobsByNamePrefix: mobs="
                    + mobs.stream()
                    .map(Mob::getId)
                    .collect(Collectors.toCollection(ArrayList::new)).toString()
            );

            return mobs;

        } catch (DataAccessException e) {
            LOGGER.error("selectMobsByNamePrefix: error: " + e.getMessage());
            return null;
        }
    }
}
