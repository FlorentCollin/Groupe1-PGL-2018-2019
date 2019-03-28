package ac.umons.slay.g01.communication.Messages;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import ac.umons.slay.g01.logic.player.Player;

/**
 * Message envoyé par le serveur aux clients pour leur indiquer qu'il y a eu une changement
 * dans la Waiting Room
 */
public class RoomUpdateMessage extends NetworkMessage {

    private CopyOnWriteArrayList<Player> players;
    private ArrayList<Boolean> playersReady;
    private String mapName;
    private String roomName;

    public RoomUpdateMessage(CopyOnWriteArrayList<Player> players, ArrayList<Boolean> playersReady, String mapName, String roomName) {
        this.players = players;
        this.playersReady = playersReady;
        this.mapName = mapName;
        this.roomName = roomName;
    }

    public CopyOnWriteArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Boolean> getPlayersReady() {
        return playersReady;
    }

    public String getMapName() {
        return mapName;
    }

    public String getRoomName() {
        return roomName;
    }
}
