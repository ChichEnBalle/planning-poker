package put.com.PLPBackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import put.com.PLPBackend.model.Room;
import put.com.PLPBackend.repository.RoomRepository;
import put.com.PLPBackend.model.User;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    // Méthode pour obtenir ou créer une room
    public Room getOrCreateRoom(String roomName) {
        Room room = roomRepository.findByName(roomName);
        if (room == null) {
            room = new Room();
            room.setName(roomName);
            roomRepository.save(room);  // Sauvegarder la room nouvellement créée
        }
        return room;
    }

    // Ajouter un utilisateur à la room
    public Room addUserToRoom(String roomName, User user) {
        Room room = getOrCreateRoom(roomName);  // Obtenir ou créer la room
        String username = user.getName();
        user.setName(username);
        room.getUsers().add(user);  // Ajouter l'utilisateur à la room
        roomRepository.save(room);  // Sauvegarder la room mise à jour
        return room;
    }

    public Room createRoom(String roomName, Long adminId) {
        Room existing = roomRepository.findByName(roomName);
        if (existing != null) {
            throw new IllegalArgumentException("Room already exists");
        }
        Room room = new Room();
        room.setName(roomName);
        room.setAdminId(adminId);
        return roomRepository.save(room);
}
}
