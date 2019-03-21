package logic.board.cell;

/**
 * Cellule neutre que chaque joueur peut atteindre et capturer
 */
public class LandCell extends Cell{
	public LandCell(int x, int y) {
		super(x,y);
		accessible = true;
	}
}
