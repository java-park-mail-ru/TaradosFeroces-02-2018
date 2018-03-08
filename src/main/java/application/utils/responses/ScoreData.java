package application.utils.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;


public class ScoreData {

    @JsonProperty("data")
    private final ArrayList<ScoreView> arrayData;

    public ScoreData(ArrayList<ScoreView> arrayData) {
        this.arrayData = arrayData;
    }
}
