package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class ScoreRequest {

    @NotNull
    private final int position;
    @NotNull
    private final int count;

    public ScoreRequest(@JsonProperty(value = "position", defaultValue = "0") @NotNull int position,
                        @JsonProperty("count") @NotNull int count) {
        this.position = position;
        this.count = count;
    }
}
