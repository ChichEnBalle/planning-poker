package put.com.PLPBackend.dto;


public class ShowVotesMessage {
    private String room;
    private boolean showVotes;
    private Long userId;

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public boolean isShowVotes() { return showVotes; }
    public void setShowVotes(boolean showVotes) { this.showVotes = showVotes; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}