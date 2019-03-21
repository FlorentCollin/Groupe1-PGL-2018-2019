package logic.naturalDisasters;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.board.cell.DroughtCell;
import logic.board.cell.LandCell;
import logic.item.Capital;
import logic.item.Item;

public class Drought extends NaturalDisasters{

	public Drought(Board board) {
		super(board);
	}
	
	private void drought() {
		nAffectedCells = 0;
		affectedCells.clear();
		int i = rand.nextInt(board.getColumns());
		int j = rand.nextInt(board.getRows());
		Cell cell = board.getCell(i, j);
		if(cell instanceof LandCell) {
			droughtFrom(cell);
		}
		modificatedCells.put(board.getTurn(), affectedCells);
	}
	
	private void droughtFrom(Cell cell) {
		nAffectedCells ++;
		affectedCells.add(cell);
		Item item = cell.getItem();
		District district = cell.getDistrict();
		cell = new DroughtCell(cell.getX(), cell.getY());
		cell.setItem(item);
		cell.setDistrict(district);
		if(district != null) {
			district.addCell(cell);			
		}
		if(item instanceof Capital) {
			district.removeCapital();
			district.addCapital(cell);
		}
		if(nAffectedCells < getMaxAffectedCells() && mustHappen(50)) { //random car au maximum 10 cellules affectées
			droughtFrom(getOneFrom(board.getNeighbors(cell)));
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
