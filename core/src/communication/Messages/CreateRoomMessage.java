package communication.Messages;

import java.util.ArrayList;

/**
 * Message qui demande au serveur de créer une partie
 */
public class CreateRoomMessage extends Message {

    private String worldName;
    private String roomName;
    private boolean isNaturalDisastersOn;
    private ArrayList<String> aiStrats;

    public CreateRoomMessage(String worldName, String roomName, boolean isNaturalDisastersOn, ArrayList<String> aiStrats) {
        this.worldName = worldName;
        this.roomName = roomName;
        this.isNaturalDisastersOn = isNaturalDisastersOn;
        this.aiStrats = aiStrats;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getRoomName() {
        return roomName;
    }

    public boolean isNaturalDisastersOn() {
        return isNaturalDisastersOn;
    }

    public ArrayList<String> getAiStrats() {
        return aiStrats;
    }
}
