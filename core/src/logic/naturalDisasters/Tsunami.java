package logic.naturalDisasters;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.board.cell.WaterCell;

public class Tsunami extends NaturalDisasters{

	public Tsunami(Board board) {
		super(board);
		setDuration(10);
		setMaxAffectedCells(3);
	}
	
	private void tsunami() {
		if(mustHappen()) {
			affectedCells.clear();
			for(Cell cell : getNeighboursWaterCells()) {
				nAffectedCells = 0;
				destroy(cell);
			}
			saveChanges();
		}
	}
	
	private void destroy(Cell cell) {
		nAffectedCells ++;
		affectedCells.add(cell);
		if(cell.getDistrict() != null) {
			cell.getDistrict().removeCell(cell);
		}
		cell = new WaterCell(cell.getX(), cell.getY());
		board.addModification(cell);
		board.setCell(cell);
		board.checkSplit(cell);
		if(nAffectedCells < getMaxAffectedCells() && ok(50)) {
			destroy(getOneFrom(board.getNeighbors(cell)));
		}
	}
	
	@Override
	public void play() {
		cancel();
		tsunami();
	}
}
