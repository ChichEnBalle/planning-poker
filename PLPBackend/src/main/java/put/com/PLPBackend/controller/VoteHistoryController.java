package put.com.PLPBackend.controller;

import org.springframework.web.bind.annotation.*;
import put.com.PLPBackend.dto.EndVotingMessage;
import put.com.PLPBackend.service.VoteHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/vote-history")
public class VoteHistoryController {

    private final VoteHistoryService voteHistoryService;

    public VoteHistoryController(VoteHistoryService voteHistoryService) {
        this.voteHistoryService = voteHistoryService;
    }

    @GetMapping("/api/vote-history/{room}")
    public List<EndVotingMessage> getVoteHistory(@PathVariable String room) {
        return voteHistoryService.getHistoryForRoom(room);
    }
}