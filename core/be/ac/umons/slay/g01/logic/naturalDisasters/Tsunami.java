package ac.umons.slay.g01.logic.naturalDisasters;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.cell.Cell;

public class Tsunami extends NaturalDisasters{

	public Tsunami(Board board) {
		super(board);
		setDuration(10);
		setMaxAffectedCells(2);
	}
	
	private void tsunami() {
		affectedCells.clear();
		for(Cell cell : getNeighboursWaterCells()) {
			nAffectedCells = 0;
			destroy(cell);
		}
		saveChanges();
	}
	
	@Override
	public void play() {
		cancel();
		if(mustHappen(getProba())) {
			tsunami();
		}
	}
}
