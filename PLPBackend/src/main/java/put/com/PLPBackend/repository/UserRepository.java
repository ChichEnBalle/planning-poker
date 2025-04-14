package put.com.PLPBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import put.com.PLPBackend.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
