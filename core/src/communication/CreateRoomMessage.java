package communication;

public class CreateRoomMessage extends Message {

    private String roomName;

    public CreateRoomMessage(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }
}
