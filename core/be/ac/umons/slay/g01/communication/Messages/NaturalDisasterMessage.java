package ac.umons.slay.g01.communication.Messages;

public class NaturalDisasterMessage extends Message {

    private String naturalName;
    private int pourcent;

    public NaturalDisasterMessage(String naturalName, int pourcent) {
        this.naturalName = naturalName;
        this.pourcent = pourcent;
    }

    public String getNaturalName() {
        return naturalName;
    }

    public int getPourcent() {
        return pourcent;
    }
}
