package put.com.PLPBackend.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import org.springframework.transaction.annotation.Transactional;


import put.com.PLPBackend.model.User;
import put.com.PLPBackend.repository.UserRepository;
import put.com.PLPBackend.model.Room;
import put.com.PLPBackend.dto.ShowVotesMessage;
import put.com.PLPBackend.dto.VoteRequest;
import put.com.PLPBackend.service.RoomService;
import put.com.PLPBackend.service.UserService;
import put.com.PLPBackend.service.VoteService;

@Controller
public class VoteController {

    @Autowired
    private RoomService roomService;
    private VoteService voteService;
    private final UserService userService;
    private final UserRepository userRepository ;
    private final SimpMessagingTemplate messagingTemplate;

	public VoteController(RoomService roomService, UserRepository userRepository, VoteService voteService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.roomService = roomService;
        this.userService = userService;
		this.userRepository = userRepository;
        this.voteService = voteService;
        this.messagingTemplate = messagingTemplate;
	}

    private void broadcastUsers(String roomName) {
		List<User> users = userService.getUsersByRoom(roomName);
		messagingTemplate.convertAndSend("/topic/users/" + roomName, users);
	}

    // Gérer l'envoi de messages dans une room spécifique
    @MessageMapping("/play.sendVote/{room}")
    @SendTo("/topic/{room}")
    public VoteRequest sendVote(@Payload VoteRequest vote, @DestinationVariable String room) {
        // Gérer l'envoi du message

        String name =  userRepository.findById(vote.getUserId())
                .map(User::getUsername)
                .orElse("Unknown User");
        System.out.println("Message reçu pour la room " + room + " de la part de "+name+": " + vote.getValue());

        // Vous pouvez maintenant utiliser Room pour gérer les utilisateurs, etc.
        Room currentRoom = roomService.getOrCreateRoom(room);
        voteService.saveVote(vote);
        

        return vote;
    }


    @Transactional
    @MessageMapping("/play.addUser/{room}")
    
    public void addUser(User u, @DestinationVariable String room, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("User " + u.getUsername() + " joined the room " + room);
        

        // Utiliser RoomService pour obtenir ou créer la room
        Room currentRoom = roomService.getOrCreateRoom(room);
      
        // Ajouter l'utilisateur à la room
        currentRoom.getUsers().add(u);
        roomService.saveRoom(currentRoom); // Sauvegarder la room
        broadcastUsers(room);
        System.out.println("Users in room " + room + ": " + currentRoom.getUsers());
        
        
    }

    @MessageMapping("/play.unvote/{room}")
    @SendTo("/topic/{room}")
    public VoteRequest handleUnvote(@Payload Map<String, Object> payload, @DestinationVariable String room) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        Long storyId = Long.valueOf(payload.get("storyId").toString());

        voteService.deselectionnerVote(userId, storyId);

        System.out.println("User " + userId + " a désélectionné le vote pour l'histoire " + storyId + " dans la room " + room);

        // Notifier les clients qu’un vote a été retiré
        VoteRequest unvoteNotification = new VoteRequest();
        unvoteNotification.setUserId(userId);
        unvoteNotification.setStoryId(storyId);
        unvoteNotification.setValue(null); // null signifie suppression du vote côté frontend

        return unvoteNotification;
    }

    @MessageMapping("/play.showVotes/{room}")
    @SendTo("/topic/showVotes/{room}")
    public ShowVotesMessage showVotes(@DestinationVariable String room, @Payload ShowVotesMessage message) throws AccessDeniedException {
        Room r = roomService.getOrCreateRoom(room); 
        Long adminId = r.getAdminId();
        Long senderId = Long.valueOf(message.getUserId()); 

        if (!adminId.equals(senderId)) {
            throw new AccessDeniedException("User is not admin.");
        }
        message.setRoom(room);
        return message;
    }



}
