package put.com.PLPBackend.controller;

import org.springframework.web.bind.annotation.*;
import put.com.PLPBackend.model.Room;

import put.com.PLPBackend.service.RoomService;

import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/{name}")
    public Room getRoomByName(@PathVariable String name) {
        Room room = roomService.getOrCreateRoom(name);
        System.out.println("Admin de la room " +room.getName()+" est " +room.getAdminId());
        return room;

    }

    @PostMapping
    public Room createRoom(@RequestBody Map<String, Object> payload) {
        String roomName = (String) payload.get("name");
        Long adminId = Long.valueOf(payload.get("adminId").toString());
        return roomService.createRoom(roomName, adminId);
    }

}