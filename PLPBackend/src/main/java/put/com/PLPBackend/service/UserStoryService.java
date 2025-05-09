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
}