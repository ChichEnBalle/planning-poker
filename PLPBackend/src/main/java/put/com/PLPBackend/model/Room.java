package put.com.PLPBackend.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Long adminId;

    @ManyToMany
    @JoinTable(
        name = "room_users", 
        joinColumns = @JoinColumn(name = "room_id"), 
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();

    public Long getId() { return id; }
    public String getName() { return name; }
    public Set<User> getUsers() { return users; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setUsers(Set<User> users) { this.users = users; }
}
