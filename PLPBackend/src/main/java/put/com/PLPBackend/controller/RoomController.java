package put.com.PLPBackend.controller;

import org.springframework.web.bind.annotation.*;
import put.com.PLPBackend.model.Room;

import put.com.PLPBackend.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/{name}")
    public Room getRoomByName(@PathVariable String name) {
        return roomService.getOrCreateRoom(name);

    }
}