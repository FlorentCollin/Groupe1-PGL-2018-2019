package ac.umons.slay.g01.logic.naturalDisasters;

import ac.umons.slay.g01.logic.board.Board;

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
