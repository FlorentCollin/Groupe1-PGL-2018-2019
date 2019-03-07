package communication;

import java.util.ArrayList;

public class CreateRoomMessage extends Message {

    private String worldName;
    private boolean isNaturalDisastersOn;
    private ArrayList<String> aiStrats;

    public CreateRoomMessage(String worldName, boolean isNaturalDisastersOn, ArrayList<String> aiStrats) {
        this.worldName = worldName;
        this.isNaturalDisastersOn = isNaturalDisastersOn;
        this.aiStrats = aiStrats;
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean isNaturalDisastersOn() {
        return isNaturalDisastersOn;
    }

    public ArrayList<String> getAiStrats() {
        return aiStrats;
    }
}
