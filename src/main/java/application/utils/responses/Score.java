package application.utils.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Score {

    @JsonProperty("points")
    private final long points;

    public Score(Score score) {
        this.points = score.getPoints();
    }

    public long getPoints() {
        return points;
    }
}
