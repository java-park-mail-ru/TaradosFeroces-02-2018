package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class PartyInviteResponse {

    public enum Answers {
        ACCEPT,
        DECLINE,
    }

    @NotNull
    private final Answers answer;

    @NotNull
    private final String leader;

    public PartyInviteResponse(@JsonProperty("answer") @NotNull String answer,
                               @JsonProperty("leader") @NotNull String leader) {

        this.leader = leader;

        switch (answer) {
            case "accept":
                this.answer = Answers.ACCEPT;
                break;

            case "decline":
                this.answer = Answers.DECLINE;
                break;

            default:
                this.answer = Answers.DECLINE;
                break;
        }
    }

    public String getLeader() {
        return leader;
    }

    public Answers getAnswer() {
        return answer;
    }
}
