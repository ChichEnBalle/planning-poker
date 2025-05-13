package put.com.PLPBackend.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="user_stories")
public class UserStory{
    @Id @GeneratedValue
    private long id;
    private String title;
    private String description;
    private Long admin;

    @ElementCollection
    private List<String> tasks = new ArrayList<>();
    @ElementCollection
    private List<Integer> votes = new ArrayList<>();

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

    public Long getAdminId(){
        return admin;
    }

    public List<String> getTasks(){
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }

    public void addTask(String task){
        tasks.add(task);
    }

    public List<Integer> getVotes(){
        return votes;
    }

    public void addVote(int vote){
        votes.add(vote);
    }

}