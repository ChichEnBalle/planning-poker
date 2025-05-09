package put.com.PLPBackend.model;

import jakarta.persistence.*;

@Entity
@Table(name="user_stories")
public class UserStory{
    @Id @GeneratedValue
    private long id;
    private String title;
    private String description;

    public UserStory() {}

    public UserStory(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}