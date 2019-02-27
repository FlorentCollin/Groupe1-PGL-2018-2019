package communication;

public class TextMessage extends Message {

    private String message;

    public TextMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
