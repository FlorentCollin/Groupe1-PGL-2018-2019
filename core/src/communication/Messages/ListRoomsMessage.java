package communication.Messages;

import roomController.WaitingRoom;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListRoomsMessage extends NetworkMessage {

    private ArrayList<String> roomsName;
    private ArrayList<Integer> nPlayer, nPlayerIn;

    public ListRoomsMessage(ArrayList<String> roomsName, ArrayList<Integer> nPlayer, ArrayList<Integer> nPlayerIn ) {
        this.roomsName = roomsName;
        this.nPlayer = nPlayer;
        this.nPlayerIn = nPlayerIn;
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
}
