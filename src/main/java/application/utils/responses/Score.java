package application.utils.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Score {

    @JsonProperty("points")
    private final long points;

    public Score(long points) {
        this.points = points;
    }

    public long getPoints() {
        return points;
    }
}
