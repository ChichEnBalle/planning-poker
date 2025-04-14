package put.com.PLPBackend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import put.com.PLPBackend.dto.VoteRequest;
import put.com.PLPBackend.model.Vote;
import put.com.PLPBackend.repository.VoteRepository;

@Service
public class VoteService {

    private final VoteRepository voteRepository;

    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public Vote saveVote(VoteRequest request) {
        Vote vote = new Vote(request.getUserId(), request.getStoryId(), request.getValue());
        return voteRepository.save(vote);
    }

    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }
}
