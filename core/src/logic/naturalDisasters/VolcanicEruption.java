package logic.naturalDisasters;

import logic.board.Board;
import logic.board.cell.Cell;

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
	
//	@Override
//	protected void destroy(Cell cell) {
//		
//	}
	
	
	@Override
	public void play() {
		cancel();
		if(mustHappen(getProba())) {
			eruption();
		}
	}
}
