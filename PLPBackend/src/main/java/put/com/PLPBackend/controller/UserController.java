package put.com.PLPBackend.controller;


import java.util.Map;
import java.util.List;
import java.util.Optional;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import put.com.PLPBackend.service.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import lombok.RequiredArgsConstructor;
import put.com.PLPBackend.model.User;
import put.com.PLPBackend.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
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

	@GetMapping("/{id:[0-9]+}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
		return this.userService.getUserById(id);
	}
	
	@GetMapping("/current")
	public User getCurrentUser(@RequestHeader("Authorization") String authHeader) {
		// Delete "Bearer " prefixe of the token
		String token = authHeader.replace("Bearer ", "");
		return userService.getUserFromToken(token);
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
		String username = payload.get("username");
        String password = payload.get("password");
        try {
            User user = userService.register(username, password);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
	}

	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        try {
            String token = userService.login(username, password);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
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

