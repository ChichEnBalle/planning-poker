package put.com.PLPBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import put.com.PLPBackend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAll();
}
