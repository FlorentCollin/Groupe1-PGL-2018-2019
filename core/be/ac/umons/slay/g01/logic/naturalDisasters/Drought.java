package ac.umons.slay.g01.logic.naturalDisasters;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.cell.Cell;

public class Drought extends NaturalDisasters{

	public Drought(Board board) {
		super(board);
		setMaxAffectedCells(10);
	}
	
	
	private void drought() {
		nAffectedCells = 0;
		affectedCells.clear();
		Cell cell = getAnyCell();
		if(cell != null) {
			destroy(cell);
			saveChanges();
		}
	}
	
	@Override
	public void play() {
		cancel();
		if(mustHappen(getProba())) {
			drought();
		}
	}
}
