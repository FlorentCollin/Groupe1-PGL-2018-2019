package ac.umons.slay.g01.communication.Messages;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Message qui demande au serveur la liste des Waiting Rooms qui ne sont pas encore pleines
 */
public class ListRoomsMessage extends NetworkMessage {

    private ArrayList<String> roomsName;
    private ArrayList<Integer> nPlayer, nPlayerIn;
    private ArrayList<UUID> ids;

    public ListRoomsMessage(ArrayList<String> roomsName, ArrayList<UUID> ids, ArrayList<Integer> nPlayer, ArrayList<Integer> nPlayerIn) {
        this.roomsName = roomsName;
        this.nPlayer = nPlayer;
        this.nPlayerIn = nPlayerIn;
        this.ids = ids;
    }

    public ArrayList<String> getRoomsName() {
        return roomsName;
    }

    public ArrayList<Integer> getnPlayer() {
        return nPlayer;
    }

    public ArrayList<Integer> getnPlayerIn() {
        return nPlayerIn;
    }

    public ArrayList<UUID> getIds() {
        return ids;
    }
}
