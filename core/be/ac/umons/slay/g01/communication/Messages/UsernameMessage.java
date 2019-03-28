package ac.umons.slay.g01.communication.Messages;

/**
 * Message utilisé par un client pour indiquer son username au serveur
 */
public class UsernameMessage extends Message {
    String username;
    int numberOfPlayer;

    public UsernameMessage(String username, int numberOfPlayer) {
        this.username = username;
        this.numberOfPlayer = numberOfPlayer;
    }

    public String getUsername() {
        return username;
    }

    public int getNumberOfPlayer() {
        return numberOfPlayer;
    }
}
