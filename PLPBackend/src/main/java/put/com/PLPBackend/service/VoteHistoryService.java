package put.com.PLPBackend.service;

import org.springframework.stereotype.Service;
import put.com.PLPBackend.dto.EndVotingMessage;

import java.util.*;

@Service
public class VoteHistoryService {
    private final Map<String, List<EndVotingMessage>> history = new HashMap<>();

    public void addHistory(String room, EndVotingMessage message) {
        history.computeIfAbsent(room, k -> new ArrayList<>()).add(message);
    }

    public List<EndVotingMessage> getHistoryForRoom(String room) {
        return history.getOrDefault(room, Collections.emptyList());
    }
}