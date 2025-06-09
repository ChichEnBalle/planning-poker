package put.com.PLPBackend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import put.com.PLPBackend.dto.EndVotingMessage;
import put.com.PLPBackend.service.VoteHistoryService;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final VoteHistoryService voteHistoryService;

    public WebSocketController(SimpMessagingTemplate messagingTemplate, VoteHistoryService voteHistoryService) {
        this.messagingTemplate = messagingTemplate;
        this.voteHistoryService = voteHistoryService;
    }

    @MessageMapping("/endVoting/{room}")
    public void endVoting(@DestinationVariable String room, EndVotingMessage message) {
        voteHistoryService.addHistory(room, message);
        messagingTemplate.convertAndSend("/topic/endVoting/" + room, message);
    }
}