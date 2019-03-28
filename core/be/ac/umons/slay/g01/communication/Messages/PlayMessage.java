package ac.umons.slay.g01.communication.Messages;

/**
 * Message envoyé par un client pour signaler à une room qu'il souhaite effectué l'action board.play(Cell cell)
 * sur une cellule. La cellule est retrouvé par le serveur grâce à son identifiant unique qui est sa position
 * sur le board en (x,y) où x est le numéro de colonne et y son numéro de ligne.
 * Le serveur appelle donc la méthode board.play(board.getCell(x,y))
 */
public class PlayMessage extends Message {

    //Position de la cellule sur laquelle la méthode play doit être appelé.
    private int x, y;

    public PlayMessage(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
