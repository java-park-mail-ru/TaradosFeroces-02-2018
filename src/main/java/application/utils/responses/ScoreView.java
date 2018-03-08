package application.utils.responses;


import com.fasterxml.jackson.annotation.JsonProperty;


public class ScoreView {

    @JsonProperty("score")
    private final Score score;

    @JsonProperty("user")
    private final UserInfo userInfo;

    public ScoreView(Score score, UserInfo userInfo) {
        this.score = score;
        this.userInfo = userInfo;
    }

    public Score getScore() {
        return score;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
