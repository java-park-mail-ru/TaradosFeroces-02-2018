package application.dao;

import application.models.Mob;
import application.models.id.Id;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public interface MobDAO {

    @NotNull
    Id<Mob> addMob(@NotNull String name, long level, @Nullable String description) throws Exception;

    @Nullable
    Mob getMobById(@NotNull Id<Mob> id) throws Exception;

    @Nullable
    Mob getMobByName(@NotNull String name) throws Exception;

    @Nullable
    List<Mob> selectMobsByNamePrefix(@NotNull String namePrefix);
}
