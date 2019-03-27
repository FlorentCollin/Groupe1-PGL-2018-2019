package logic.naturalDisasters;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.board.cell.DroughtCell;
import logic.item.Capital;
import logic.item.Item;

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
