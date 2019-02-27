package communication;

import logic.board.Board;

public class InitMessage extends Message {

    private Board board;

    public InitMessage(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }
}
