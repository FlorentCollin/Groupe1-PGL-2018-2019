package logic.naturalDisasters;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.board.cell.LavaCell;

public class VolcanicEruption extends NaturalDisasters{

	public VolcanicEruption(Board board) {
		super(board);
		setDuration(2);
		setMaxAffectedCells(6);
	}
	
	private void eruption() {
		affectedCells.clear();
		nAffectedCells = 0;
		destroy(getAnyCell());
		saveChanges();
	}
	
	@Override
	public void play() {
		cancel();
		if(mustHappen(getProba())) {
			eruption();
		}
	}
}
