package communication;

import logic.board.District;
import logic.player.Player;
import server.Client;

import java.util.ArrayList;

public class UpdateMessage extends Message {

    private ArrayList<Client> clients;

    private ArrayList<District> districts;
    private Player[] players;
    private int activePlayer;
    private Integer x, y; // Correspond à la position en x et y de la cellule sélectionné si elle existe

    //TODO Need refactoring about what is updated in the board because here all is pass every time something has changed
    public UpdateMessage(ArrayList<District> districts, Player[] players, int activePlayer) {
        this.districts = districts;
        this.players = players;
        this.activePlayer = activePlayer;
    }

    public UpdateMessage(ArrayList<District> districts, Player[] players, int activePlayer, int x, int y) {
        this.districts = districts;
        this.players = players;
        this.activePlayer = activePlayer;
        this.x = x;
        this.y = y;
    }

    public ArrayList<District> getDistricts() {
        return districts;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }
}
