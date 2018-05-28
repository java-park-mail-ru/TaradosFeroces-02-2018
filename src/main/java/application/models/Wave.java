package application.models;

import application.models.id.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;


@Document(collection = "waves")
public class Wave {

    @NotNull
    @JsonIgnore
    @org.springframework.data.annotation.Id
    private final Id<Wave> id;

    @NotNull
    @JsonIgnore
    private final Id<Wave> parentId;

    @NotNull
    @JsonIgnore
    private final Id<Wave> subparentId;

    @NotNull
    @JsonProperty("created")
    private final Date created;

    @NotNull
    @JsonProperty("mob_start_points")
    private final ArrayList<Mob.MobStartState> mobStartStates;

    @NotNull
    @JsonProperty("mobs")
    private final ArrayList<Mob> mobs;

    public Wave(@NotNull Id<Wave> id,
                @NotNull Id<Wave> parentId,
                @NotNull Id<Wave> subparentId,
                @NotNull Date created,
                @NotNull ArrayList<Mob> mobs,
                @NotNull ArrayList<Mob.MobStartState> mobStartStates) {
        this.id = id;
        this.parentId = parentId;
        this.subparentId = subparentId;
        this.created = created;
        this.mobStartStates = mobStartStates;
        this.mobs = mobs;
    }

    @NotNull
    public Id<Wave> getId() {
        return id;
    }

    @NotNull
    public Id<Wave> getParentId() {
        return parentId;
    }

    @NotNull
    public Id<Wave> getSubparentId() {
        return subparentId;
    }

    @NotNull
    public Date getCreated() {
        return created;
    }

    @NotNull
    public ArrayList<Mob.MobStartState> getMobStartStates() {
        return mobStartStates;
    }

    @NotNull
    public ArrayList<Mob> getMobs() {
        return mobs;
    }
}
