package put.com.PLPBackend.dto;

public class Task {
    private Long userStoryId;
    private String task;
    public Long getUserStoryId() {
        return userStoryId;
    }
    public void setUserStoryId(Long userStoryId) {
        this.userStoryId = userStoryId;
    }
    public String getTask() {
        return task;
    }
    public void setTask(String task) {
        this.task = task;
    }

    
}
