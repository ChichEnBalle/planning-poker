package put.com.PLPBackend.service;


import org.hibernate.Hibernate;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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

    @Transactional
    public UserStory addTaskToUserStory(Long id, String task) {
        UserStory userStory = userStoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User story not found with ID: " + id));
        userStory.addTask(task);
        return userStoryRepository.save(userStory);
    }

    
    @Transactional
    public UserStory modifyUserStory(Long id, String newTitle, String newDescription){
        UserStory userStory = userStoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User story not found with ID: " + id));
        userStory.setTitle(newTitle);
        userStory.setDescription(newDescription);
        UserStory saved = userStoryRepository.save(userStory);
        Hibernate.initialize(saved.getTasks()); 
        return saved;
    }

    public List<UserStory> getUserStoriesForRoom(String roomId) {
        return userStoryRepository.findByRoomId(roomId);
    }

    public UserStory saveUserStory(UserStory userStory) {
        return userStoryRepository.save(userStory);
    }
}