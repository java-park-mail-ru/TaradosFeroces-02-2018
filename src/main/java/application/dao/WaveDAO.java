package application.dao;

import application.models.Mob;
import application.models.Wave;
import application.models.id.Id;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface WaveDAO {

    @NotNull
    Id<Wave> addWave(@NotNull Id<Wave> parentId,
                     @NotNull Id<Wave> subparentId,
                     @NotNull ArrayList<Mob> mobs,
                     @NotNull ArrayList<Mob.MobStartState> mobStartStates);

    @Nullable
    Wave getWaveById(@NotNull Id<Wave> id);

    @Nullable
    List<Wave> getChildWaves(@NotNull Id<Wave> parent, boolean asSubParent);

}