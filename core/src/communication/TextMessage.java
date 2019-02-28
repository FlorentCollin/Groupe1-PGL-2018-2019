package communication;

/**
 * Message représentant du texte. Peut être utilisé dans plusieurs cas.
 * Par exemple pour appeler la méthode nextPlayer sur le board
 */
public class TextMessage extends Message {

    private String message;

    public TextMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
