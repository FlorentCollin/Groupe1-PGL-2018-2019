package communication.Messages;

public class UsernameMessage extends Message {
    String username;

    public UsernameMessage(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
