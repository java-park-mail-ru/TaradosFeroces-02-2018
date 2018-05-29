package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class SelectUsersByLoginPrefix {

    @NotNull
    private final String prefix;

    @JsonCreator
    public SelectUsersByLoginPrefix(@JsonProperty("prefix") @NotNull String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
