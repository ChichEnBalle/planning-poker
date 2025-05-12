package put.com.PLPBackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import put.com.PLPBackend.model.UserStory;
import put.com.PLPBackend.service.UserStoryService;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


@RestController
@RequestMapping("/api/user-stories")
@CrossOrigin(origins = "http://localhost:5173")
public class UserStoryController {

    private final UserStoryService userStoryService;

    public UserStoryController(UserStoryService userStoryService) {
        this.userStoryService = userStoryService;
    }

    @GetMapping
    public ResponseEntity<List<UserStory>> getAllUserStories() {
        return ResponseEntity.ok(userStoryService.getAllUserStories());
    }

    @PostMapping
    public ResponseEntity<UserStory> createUserStory(@RequestBody UserStory userStory) {
        return ResponseEntity.ok(userStoryService.createUserStory(userStory));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserStory(@PathVariable Long id) {
        userStoryService.deleteUserStory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<UserStory> addTaskToUserStory(@PathVariable Long id, @RequestBody String task) {
        UserStory updatedStory = userStoryService.addTaskToUserStory(id, task);
        return ResponseEntity.ok(updatedStory);
    }

    @PutMapping("/{id}")
        public ResponseEntity<UserStory> modifyUserStory(@PathVariable Long id, @RequestBody UserStory updatedUserStory) {
        UserStory modifiedStory = userStoryService.modifyUserStory(
                id,
                updatedUserStory.getTitle(),
                updatedUserStory.getDescription()
        );
        return ResponseEntity.ok(modifiedStory);
    }

}