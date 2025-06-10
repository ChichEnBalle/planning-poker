package put.com.PLPBackend.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import put.com.PLPBackend.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByName(String name); // Méthode pour trouver une room par son nom
    Optional<Room> findById(Long id); // Méthode pour trouver une room par son ID
}
