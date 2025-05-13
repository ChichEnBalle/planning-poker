package put.com.PLPBackend.dto;

public class ChatMessage {
    private String sender;
    private String content;
    private String room;

    // Constructor with parameters to initialize fields
    public ChatMessage(String sender, String content, String room) {
        this.sender = sender;
        this.content = content;
        this.room = room;
    }

    // Getter methods
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public String getRoom() { return room; }

    // Setter methods
    public void setSender(String sender) { this.sender = sender; }
    public void setContent(String content) { this.content = content; }
    public void setRoom(String room) { this.room = room; }

    // Override toString for easy debugging and logging
    @Override
    public String toString() {
        return "ChatMessage{" +
               "sender='" + sender + '\'' +
               ", content='" + content + '\'' +
               ", room='" + room + '\'' +
               '}';
    }
}
