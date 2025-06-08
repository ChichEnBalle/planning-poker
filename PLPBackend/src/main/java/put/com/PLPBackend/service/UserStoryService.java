package put.com.PLPBackend.service;

import org.springframework.stereotype.Service;
import put.com.PLPBackend.model.UserStory;
import put.com.PLPBackend.repository.UserStoryRepository;

import java.util.List;

@Service
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;

    public UserStoryService(UserStoryRepository userStoryRepository) {
        this.userStoryRepository = userStoryRepository;
    }

    public List<UserStory> getAllUserStories() {
        return userStoryRepository.findAll();
    }

    public UserStory createUserStory(UserStory userStory) {
        return userStoryRepository.save(userStory);
    }

    public void deleteUserStory(Long id) {
        userStoryRepository.deleteById(id);
    }

    public UserStory addTaskToUserStory(Long id, String task) {
        UserStory userStory = userStoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User story not found with ID: " + id));
        userStory.addTask(task);
        return userStoryRepository.save(userStory);
    }

    public UserStory modifyUserStory(Long id, String newTitle, String newDescription){
        UserStory userStory = userStoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User story not found with ID: " + id));
        userStory.setTitle(newTitle);
        userStory.setDescription(newDescription);
        return userStoryRepository.save(userStory);
    }

    public List<UserStory> getUserStoriesForRoom(String roomId) {
        return userStoryRepository.findByRoomId(roomId);
    }

    public UserStory saveUserStory(UserStory userStory) {
        return userStoryRepository.save(userStory);
    }
}