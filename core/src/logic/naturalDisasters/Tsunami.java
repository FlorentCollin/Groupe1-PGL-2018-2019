package logic.naturalDisasters;

import logic.board.Board;
import logic.board.cell.Cell;

public class Tsunami extends NaturalDisasters{

	public Tsunami(Board board) {
		super(board);
		setDuration(10);
		setMaxAffectedCells(3);
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
