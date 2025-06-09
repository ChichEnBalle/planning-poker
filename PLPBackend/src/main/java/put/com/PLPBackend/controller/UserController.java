package put.com.PLPBackend.controller;

import java.util.Map;

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

import lombok.RequiredArgsConstructor;
import put.com.PLPBackend.model.User;
import put.com.PLPBackend.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
	
	private final UserService userService;
	
	// @GetMapping("{id}")
	// public ResponseEntity<User> getUserById(@PathVariable Long id) {
	// 	return this.userService.getUserById(id);
	// }
	
	@GetMapping("/current")
	public User getCurrentUser(@RequestHeader("Authorization") String authHeader) {
		// Delete "Bearer " prefixe of the token
		System.out.println("Authorization Header : " + authHeader);
		String token = authHeader.replace("Bearer ", "");
		System.out.println("Token reçu : " + token);
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

	@DeleteMapping()
	public ResponseEntity<?> deleteUser(@RequestParam Long userId) {
		return this.userService.deleteUser(userId);
	}
}

