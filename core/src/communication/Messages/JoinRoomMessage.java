package communication.Messages;

import java.util.UUID;

/**
 * Message utilisé par un client pour signaler qu'il veut se connecter à une GameRoom.
 */
public class JoinRoomMessage extends Message {

    private UUID id;

    public JoinRoomMessage(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
