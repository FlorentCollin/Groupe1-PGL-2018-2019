package communication.Messages;

import logic.player.Player;

import java.util.ArrayList;

public class RoomUpdateMessage extends NetworkMessage {

    private ArrayList<Player> players;
    private ArrayList<Boolean> playersReady;
    private String mapName;

    public RoomUpdateMessage(ArrayList<Player> players, ArrayList<Boolean> playersReady, String mapName) {
        this.players = players;
        this.playersReady = playersReady;
        this.mapName = mapName;
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
}
