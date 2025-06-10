package put.com.PLPBackend.service;

import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import put.com.PLPBackend.model.User;
import put.com.PLPBackend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecretKey jwtSecret = Keys.hmacShaKeyFor("la_sécurité_est_primordiale_avant_tout_donc_il_faut_que_elle_ait_une_sacrée_taille".getBytes(StandardCharsets.UTF_8));
    private final long jwtExpirationMs = 86400000; // 24h

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<User> getUserById(long id){
        Optional<User> user = userRepository.findById(id);
		return user.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> deleteUser(@RequestParam Long userId) {
		if (userRepository.existsById(userId)) {
			userRepository.deleteById(userId);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

    public User register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return generateJwtToken(userOpt.get());
    }
    
    private String generateJwtToken(User user) {
    return Jwts.builder()
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(jwtSecret, SignatureAlgorithm.HS512)
            .compact();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject(); // "subject" = username
        } catch (Exception e) {
            System.err.println("Error when getting username from token : " + e.getMessage());
            throw e;
        }
    }

    public User getUserFromToken(String token) {
        try {
            String username = getUsernameFromToken(token);
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } catch (Exception e) {
            System.err.println("Error when getting username from repository : " + e.getMessage());
            throw e;
        }
    }
}
