package put.com.PLPBackend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import put.com.PLPBackend.model.User;
import put.com.PLPBackend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsersByRoom(String roomId) {
        return userRepository.findByRoomId(roomId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
