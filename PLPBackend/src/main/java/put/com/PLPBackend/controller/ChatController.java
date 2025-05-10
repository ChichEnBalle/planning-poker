package put.com.PLPBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import org.springframework.transaction.annotation.Transactional;


import put.com.PLPBackend.model.User;
import put.com.PLPBackend.repository.UserRepository;
import put.com.PLPBackend.model.Room;
import put.com.PLPBackend.dto.ChatMessage;
import put.com.PLPBackend.service.RoomService;

@Controller
public class ChatController {

    @Autowired
    private RoomService roomService;
    private final UserRepository userRepository ;

	public ChatController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

    // Gérer l'envoi de messages dans une room spécifique
    @MessageMapping("/chat.sendMessage/{room}")
    @SendTo("/topic/{room}")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable String room) {
        // Gérer l'envoi du message
        System.out.println("Message reçu pour la room " + room + " de la part de "+chatMessage.getSender()+": " + chatMessage.getContent());

        // Vous pouvez maintenant utiliser Room pour gérer les utilisateurs, etc.
        Room currentRoom = roomService.getOrCreateRoom(room);

        return chatMessage;
    }


    @Transactional
    @MessageMapping("/chat.addUser/{room}")
    @SendTo("/topic/{room}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, @DestinationVariable String room, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("User " + chatMessage.getSender() + " joined the room " + room);
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

        // Utiliser RoomService pour obtenir ou créer la room
        Room currentRoom = roomService.getOrCreateRoom(room);
        User u = new User();
        u.setName(chatMessage.getSender());
        userRepository.save(u);

        // Ajouter l'utilisateur à la room
        currentRoom.getUsers().add(u);
        //roomRepository.save(currentRoom); // Sauvegarder la room

        return new ChatMessage(chatMessage.getSender(), " joined the room ", room);
    }

}
