package put.com.PLPBackend.model;

import jakarta.persistence.*;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long storyId;
    private String value;

    public Vote() {}

    public Vote(Long userId, Long storyId, String value) {
        this.userId = userId;
        this.storyId = storyId;
        this.value = value;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getStoryId() { return storyId; }
    public void setStoryId(Long storyId) { this.storyId = storyId; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
