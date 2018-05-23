package application.utils.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class FriendshipResponse {

    public enum Answers {
        ACCEPT,
        DECLINE,
        IGNORE
    }

    @NotNull
    private final Long requestId;

    @NotNull
    private final Answers answer;

    public FriendshipResponse(@JsonProperty("request_id") @NotNull Long requestId,
                              @JsonProperty("answer") @NotNull String answer) {
        this.requestId = requestId;

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

    public Long getRequestId() {
        return requestId;
    }

    public Answers getAnswer() {
        return answer;
    }
}
