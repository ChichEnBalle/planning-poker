package put.com.PLPBackend;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class User {
    @Id @GeneratedValue
    private long id;
    private String name;
    private int selectedCard;
    private boolean hasVoted;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSelectedCard() {
        return selectedCard;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelectedCard(int selectedCard) {
        this.selectedCard = selectedCard;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }
}
