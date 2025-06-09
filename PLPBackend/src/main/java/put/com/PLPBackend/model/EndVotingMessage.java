package put.com.PLPBackend.model;

import java.util.List;

public class EndVotingMessage {
    private String type;
    private Long storyId;
    private List<VoteDto> votes;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public List<VoteDto> getVotes() { return votes; }
    public void setVotes(List<VoteDto> votes) { this.votes = votes; }

    public static class VoteDto {
        private Long userId;
        private String value;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}
