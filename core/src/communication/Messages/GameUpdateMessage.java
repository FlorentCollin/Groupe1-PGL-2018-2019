package communication.Messages;

import logic.board.District;
import logic.item.Item;
import logic.player.Player;
import logic.shop.Shop;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Message qui est envoyé par une room à un(des) client(s) pour signaler que le board à été modifié et que le board
 * des joueurs doit être mis à jour.
 */
public class GameUpdateMessage extends NetworkMessage {

    private CopyOnWriteArrayList<District> districts;
    private Item shopItem;
    private CopyOnWriteArrayList<Player> players;
    private Player winner;
    private int activePlayer;
    private Integer x, y; // Correspond à la position en x et y de la cellule sélectionné si elle existe

    //TODO Need refactoring about what is updated in the board because here all is pass every time something has changed
    public GameUpdateMessage(CopyOnWriteArrayList<District> districts, Item shopItem, CopyOnWriteArrayList<Player> players, Player winner, int activePlayer) {
        this.districts = districts;
        this.shopItem = shopItem;
        this.players = players;
        this.winner = winner;
        this.activePlayer = activePlayer;
    }

    public GameUpdateMessage(CopyOnWriteArrayList<District> districts, Item shopItem, CopyOnWriteArrayList<Player> players, Player winner, int activePlayer, int x, int y) {
        this(districts, shopItem, players, winner, activePlayer);
        this.x = x;
        this.y = y;
    }

    public CopyOnWriteArrayList<District> getDistricts() {
        return districts;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public CopyOnWriteArrayList<Player> getPlayers() {
        return players;
    }

    public Player getWinner() {
        return winner;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public Item getShopItem() {
        return shopItem;
    }
}
