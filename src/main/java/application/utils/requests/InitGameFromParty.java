package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class InitGameFromParty {

    @NotNull
    private final String leader;

    public InitGameFromParty(@JsonProperty("leader") @NotNull String leader) {
        this.leader = leader;
    }

    public String getLeader() {
        return leader;
    }
}
