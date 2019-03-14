package communication.Messages;

import logic.player.Player;

import java.util.ArrayList;

public class RoomUpdateMessage extends NetworkMessage {

    private ArrayList<Player> players;
    private ArrayList<Boolean> playersReady;
    private String mapName;
    private String roomName;

    public RoomUpdateMessage(ArrayList<Player> players, ArrayList<Boolean> playersReady, String mapName, String roomName) {
        this.players = players;
        this.playersReady = playersReady;
        this.mapName = mapName;
        this.roomName = roomName;
    }

    public ArrayList<Player> getPlayers() {
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
