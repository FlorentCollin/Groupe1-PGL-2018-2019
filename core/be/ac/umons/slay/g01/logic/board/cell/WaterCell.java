package ac.umons.slay.g01.logic.board.cell;

/**
 * Cellule d'eau qui n'est pas accessible
 */
public class WaterCell extends Cell{
	public WaterCell(int x, int y) {
		super(x,y);
		accessible = false;
	}
}
