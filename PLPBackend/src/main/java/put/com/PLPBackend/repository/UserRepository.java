package put.com.PLPBackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import put.com.PLPBackend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByUsername(String username);
    //List<User> findByRoomId(String roomId);
    
}

