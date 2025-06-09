package put.com.PLPBackend.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name="user_stories")
public class UserStory{
    @Id @GeneratedValue
    private long id;
    private String title;
    private String description;
    private Long admin;
    private int estimation;
    private String roomId;


    @ElementCollection
    @JsonIgnore
    private List<String> tasks = new ArrayList<>();
    @ElementCollection
    @JsonIgnore
    private List<Integer> votes = new ArrayList<>();

    public UserStory() {}

    public UserStory(String title, String description, String roomId) {
        this.title = title;
        this.description = description;
        this.roomId = roomId;
    }

    public UserStory(String title, String description, int estimation, String roomId) {
        this.title = title;
        this.description = description;
        this.estimation = estimation;
        this.roomId = roomId;
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

    public int getEstimation(){
        return estimation;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

}