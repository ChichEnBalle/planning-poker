package put.com.PLPBackend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import put.com.PLPBackend.service.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import put.com.PLPBackend.model.User;
import put.com.PLPBackend.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

	private final UserRepository userRepository;
	private final UserService userService;
	private final SimpMessagingTemplate messagingTemplate;



	public UserController(UserRepository userRepository, UserService userService, SimpMessagingTemplate messagingTemplate) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.messagingTemplate = messagingTemplate;
	}

	private void broadcastUsers(String roomId) {
		List<User> users = userService.getUsersByRoom(roomId);
		messagingTemplate.convertAndSend("/topic/users/" + roomId, users);
	}

	@GetMapping("{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		Optional<User> user = userRepository.findById(id);
		return user.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user, @RequestParam String roomId) {
		user.setRoomId(roomId);
		boolean exists = userRepository.existsByNameAndRoomId(user.getName(), roomId);
		if (exists) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
		}
		User savedUser = userRepository.save(user);
		broadcastUsers(roomId);
    	return ResponseEntity.ok(savedUser);
	}

	@GetMapping("/room/{roomId}")
    public List<User> getUsersByRoom(@PathVariable String roomId) {
        return userService.getUsersByRoom(roomId);
    }

	@DeleteMapping()
	public ResponseEntity<?> deleteUser(@RequestParam Long userId, @RequestParam String roomId) {
		if (userRepository.existsById(userId)) {
			userRepository.deleteById(userId);
			broadcastUsers(roomId);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}

