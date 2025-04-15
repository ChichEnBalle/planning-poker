package put.com.PLPBackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import put.com.PLPBackend.dto.VoteRequest;
import put.com.PLPBackend.model.Vote;
import put.com.PLPBackend.service.VoteService;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "http://localhost:5173")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public ResponseEntity<Vote> vote(@RequestBody VoteRequest request) {
        Vote savedVote = voteService.saveVote(request);
        return ResponseEntity.ok(savedVote);
    }

    @GetMapping
    public ResponseEntity<List<Vote>> getVotes() {
        List<Vote> votes = voteService.getAllVotes(); 
        return ResponseEntity.ok(votes);
    }

    @DeleteMapping
    public ResponseEntity<Void> deselectionnerVote(@RequestParam Long userId, @RequestParam Long storyId) {
        voteService.deselectionnerVote(userId, storyId);
        return ResponseEntity.noContent().build();
    }
}
