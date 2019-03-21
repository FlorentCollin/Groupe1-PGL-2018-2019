package communication.Messages;

/**
 * Message utilisé par un client pour indiquer son username au serveur
 */
public class UsernameMessage extends Message {
    String username;

    public UsernameMessage(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
