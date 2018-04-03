package application.utils.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;


public class ScoreData {

    @JsonProperty("data")
    private final List<ScoreView> arrayData;

    public ScoreData(List<ScoreView> arrayData) {
        this.arrayData = arrayData;
    }

    public ScoreData() {
        this.arrayData = new ArrayList<ScoreView>();
    }

    public void addScoreView(ScoreView scoreView) {
        arrayData.add(scoreView);
    }
}
