package communication;

public class CreateRoomMessage extends Message {

    private String worldName;
    private boolean isNaturalDisastersOn;

    public CreateRoomMessage(String worldName, boolean isNaturalDisastersOn) {
        this.worldName = worldName;
        this.isNaturalDisastersOn = isNaturalDisastersOn;
    }

    public String getWorldName() {
        return worldName;
    }

    public boolean isNaturalDisastersOn() {
        return isNaturalDisastersOn;
    }
}
