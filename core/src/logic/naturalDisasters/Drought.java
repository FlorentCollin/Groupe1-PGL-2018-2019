package logic.naturalDisasters;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.board.cell.DroughtCell;
import logic.board.cell.LandCell;
import logic.item.Capital;
import logic.item.Item;

public class Drought extends NaturalDisasters{
	int nDroughtCells;

	public Drought(Board board) {
		super(board);
	}
	
	private void drought() {
		nDroughtCells = 0;
		affectedCells.clear();
		int i = rand.nextInt(board.getColumns());
		int j = rand.nextInt(board.getRows());
		Cell cell = board.getCell(i, j);
		if(cell instanceof LandCell) {
			droughtFrom(cell);
		}
		modificatedCells.put(board.getActivePlayer(), affectedCells);
	}
	
	private void droughtFrom(Cell cell) {
		nDroughtCells ++;
		affectedCells.add(cell);
		Item item = cell.getItem();
		District district = cell.getDistrict();
		cell = new DroughtCell(cell.getX(), cell.getY());
		cell.setItem(item);
		cell.setDistrict(district);
		district.addCell(cell);
		if(item instanceof Capital) {
			district.addCapital(cell);
		}
		if(nDroughtCells < 10) {
			for(Cell neighbour : board.getNeighbors(cell)) {
				if(neighbour instanceof LandCell) {
					droughtFrom(neighbour);
				}
			}
		}
	}
	
	@Override
	public void play() {
		cancel();
		drought();
	}
}
