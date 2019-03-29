package ac.umons.slay.g01.logic.naturalDisasters;

import java.util.ArrayList;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.board.cell.LandCell;
import ac.umons.slay.g01.logic.board.cell.WaterCell;
import ac.umons.slay.g01.logic.item.Capital;

public class LandErosion extends NaturalDisasters{
	
	
	public LandErosion(Board board) {
		super(board);
	}

	private void erosion() {
		affectedCells.clear();
		int nWaterCells;
		double prob;
		for(Cell neighbour : getNeighboursWaterCells()) {
			if(neighbour instanceof LandCell) {
				nWaterCells = 0;
				for(Cell cell : board.getNeighbors(neighbour)) {
					if(cell instanceof WaterCell) {
						nWaterCells ++;
					}
				}
				prob = calcProb(nWaterCells);
				if(rand.nextInt(101) <= prob) {
					affectedCells.add(neighbour);
					erode(neighbour);
				}
			}
		}
		saveChanges();
	}
	
	/**
	 * Permet d'éroder une cellule
	 * @param cell la cellule à éroder
	 */
	private void erode(Cell cell) {
		if(cell.getDistrict() != null) {
			cell.getDistrict().removeCell(cell);
		}
		if(cell.getItem() != null) {
			if(cell.getItem() instanceof Capital) {
				cell.getDistrict().removeCapital();
			}
			else if(cell.getItem().isMovable()) {
				for(Cell c : board.getNeighbors(cell)) {
					if(board.canGoOn(c, cell.getItem()) && hasGoodNeighbour(cell)) {
						c.setDistrict(cell.getDistrict());
						c.setItem(cell.getItem());
						board.addModification(c);
						break;
					}
				}
			}
		}
		cell = new WaterCell(cell.getX(), cell.getY());
		board.setCell(cell);
		board.checkDistricts();
		board.checkSplit(cell);
		board.addModification(cell);
	}
	
	/**
	 * Permet de savoir si la cellule sur laquelle on souhaite se placer a dans son voisinage 
	 * @param cell
	 * @return
	 */
	private boolean hasGoodNeighbour(Cell cell) {
		for(Cell nb : board.getNeighbors(cell)) {
			if(nb.getDistrict() == cell.getDistrict()) {
				return true;
			}
		}
		return false;
	}
	
	private double calcProb(int n) {
		return 40*Math.log10(n+1);
	}
	
	@Override
	protected void cancel() {
		ArrayList<Integer> keysToDelete = new ArrayList<>();
		for(int key : modificatedCells.keySet()) {
			if(board.getTurn() - key > getDuration()) {
				keysToDelete.add(key);
				for(Cell cell : modificatedCells.get(key)) {
					cell = new LandCell(cell.getX(), cell.getY());
					board.setCell(cell);
					board.addModification(cell);
				}
			}
		}
		for(int key : keysToDelete) {
			modificatedCells.remove(key);
		}
	}
	
	@Override
	public void play() {
		cancel();
		if(mustHappen(getProba())) {
			erosion();
		}
	}
}
