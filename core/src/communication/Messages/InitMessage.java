package communication.Messages;

import logic.board.Board;

import java.util.ArrayList;

/**
 * Message contenant l'état actuel du Board.
 * Ce message est utilisé lorsqu'un nouveau joueur se connecte à une room ou lors de la création de celle-ci.
 * La room lui envoie se message pour que le joueur puisse se synchroniser avec les autres joueurs.
 */
public class InitMessage extends NetworkMessage {

    private Board board;
    private ArrayList<Integer> playerNumber;

    public InitMessage(Board board, ArrayList<Integer> playerNumber) {
        this.board = board;
        this.playerNumber = playerNumber;
    }

    public Board getBoard() {
        return board;
    }

    public ArrayList<Integer> getPlayerNumber() {
        return playerNumber;
    }
}
