package put.com.PLPBackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import put.com.PLPBackend.model.User;
import put.com.PLPBackend.model.UserStory;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();
    Optional<User> findByName(String name);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByName(String name);
    List<User> findByRoomId(String roomId);
    boolean existsByNameAndRoomId(String name, String roomId);
}

