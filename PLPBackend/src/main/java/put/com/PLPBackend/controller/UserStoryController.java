package put.com.PLPBackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import put.com.PLPBackend.model.User;
import put.com.PLPBackend.model.UserStory;
import put.com.PLPBackend.service.UserStoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


@RestController
public class UserStoryController {

    private final UserStoryService userStoryService;

    public UserStoryController(UserStoryService userStoryService) {
        this.userStoryService = userStoryService;
    }

    /* @MessageMapping("/play.getUserStories/{room}")
    @SendTo("/topic/{room}")
    public List<UserStory> getAllUserStories() {
        return userStoryService.getAllUserStories();
    }
 */

    @MessageMapping("/play.addUserStory/{room}")
    @SendTo("/topic/userStory/{room}")
    public UserStory createUserStory(@Payload UserStory userStory, @DestinationVariable String room) {
        System.out.println("Received user story: title = " + userStory.getTitle() + ", descr = " + userStory.getDescription()+ "id = " + userStory.getId());

        return userStoryService.createUserStory(userStory);
    }

    @MessageMapping("/play.deleteUserStory/{room}")
    @SendTo("/topic/userStory/{room}")
    public UserStory deleteUserStory(UserStory userStory, @DestinationVariable String room) {
        Long id = userStory.getId();
        
        System.out.println("id = " + id);
        UserStory deletedStory = new UserStory();
        deletedStory.setId(id);
        deletedStory.setTitle(null);
        userStoryService.deleteUserStory(id);

        System.out.println("Deleted user story with ID: " + deletedStory.getId());
        System.out.println("Deleted user story: title = " + deletedStory.getTitle() + ", descr = " + deletedStory.getDescription());
        return deletedStory;
        
    }

    /* @PostMapping("/{id}/tasks")
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
    } */

}