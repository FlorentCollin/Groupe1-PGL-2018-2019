package communication.Messages;

import logic.board.District;
import logic.player.Player;
import logic.shop.Shop;

import java.util.ArrayList;

/**
 * Message qui est envoyé par une room à un(des) client(s) pour signaler que le board à été modifié et que le board
 * des joueurs doit être mis à jour.
 */
public class GameUpdateMessage extends NetworkMessage {

    private ArrayList<District> districts;
    private Shop shop;
    private ArrayList<Player> players;
    private int activePlayer;
    private Integer x, y; // Correspond à la position en x et y de la cellule sélectionné si elle existe

    //TODO Need refactoring about what is updated in the board because here all is pass every time something has changed
    public GameUpdateMessage(ArrayList<District> districts, Shop shop, ArrayList<Player> players, int activePlayer) {
        this.districts = districts;
        this.shop = shop;
        this.players = players;
        this.activePlayer = activePlayer;
    }

    public GameUpdateMessage(ArrayList<District> districts, Shop shop, ArrayList<Player> players, int activePlayer, int x, int y) {
        this.districts = districts;
        this.shop = shop;
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public Shop getShop() {
        return shop;
    }
}
