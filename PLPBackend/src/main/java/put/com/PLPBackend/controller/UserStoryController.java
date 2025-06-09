package put.com.PLPBackend.controller;

import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import put.com.PLPBackend.model.UserStory;
import put.com.PLPBackend.service.UserStoryService;


@RestController
public class UserStoryController {

    private final UserStoryService userStoryService;

    public UserStoryController(UserStoryService userStoryService) {
        this.userStoryService = userStoryService;
    }

    @GetMapping("/api/userstories/{roomId}")
    public List<UserStory> getUserStoriesForRoom(@PathVariable String roomId) {
        return userStoryService.getUserStoriesForRoom(roomId);
    }

    @MessageMapping("/play.addUserStory/{room}")
    @SendTo("/topic/userStory/{room}")
    public UserStory createUserStory(@Payload UserStory userStory, @DestinationVariable String room) {
        userStory.setRoomId(room);
        return userStoryService.saveUserStory(userStory);
    }

    @MessageMapping("/play.updateUserStory/{room}")
    @SendTo("/topic/userStory/{room}")
    public UserStory updateUserStory(@Payload UserStory userStory, @DestinationVariable String room) {
        userStory.setRoomId(room);
        return userStoryService.modifyUserStory(userStory.getId(), userStory.getTitle(), userStory.getDescription());
    }
        

    @MessageMapping("/play.deleteUserStory/{room}")
    @SendTo("/topic/userStory/{room}")
    public UserStory deleteUserStory(@Payload UserStory userStory, @DestinationVariable String room) {
        Long id = userStory.getId();
        UserStory deletedStory = new UserStory();
        deletedStory.setId(id);
        deletedStory.setTitle(null);
        userStoryService.deleteUserStory(id);


        return deletedStory;
        
    }
}