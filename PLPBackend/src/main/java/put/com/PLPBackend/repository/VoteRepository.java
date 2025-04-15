package put.com.PLPBackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import put.com.PLPBackend.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findAll();
}
