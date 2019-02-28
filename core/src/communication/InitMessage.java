package communication;

import logic.board.Board;

/**
 * Message contenant l'état actuel du Board.
 * Ce message est utilisé lorsqu'un nouveau joueur se connecte à une room ou lors de la création de celle-ci.
 * La room lui envoie se message pour que le joueur puisse se synchroniser avec les autres joueurs.
 */
public class InitMessage extends NetworkMessage {

    private Board board;

    public InitMessage(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }
}
