package put.com.PLPBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import put.com.PLPBackend.model.UserStory;

public interface UserStoryRepository extends JpaRepository<UserStory, Long> {
    List<UserStory> findByRoomId(String roomId);
}