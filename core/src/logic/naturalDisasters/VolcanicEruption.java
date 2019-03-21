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
	
	private void destroy(Cell cell) {
		nAffectedCells ++;
		affectedCells.add(cell);
		if(cell.getDistrict() != null) {
			cell.getDistrict().removeCell(cell);
		}
		cell.removeDistrict();
		cell.removeItem();
		cell = new LavaCell(cell.getX(), cell.getY());
		board.setCell(cell);
		board.addModification(cell);
		board.checkSplit(cell);
		if(nAffectedCells < getMaxAffectedCells() && mustHappen(50)) {
			destroy(getOneFrom(board.getNeighbors(cell)));
		}
	}
	
	@Override
	public void play() {
		cancel();
		if(mustHappen(getProba())) {
			eruption();
		}
	}
}
