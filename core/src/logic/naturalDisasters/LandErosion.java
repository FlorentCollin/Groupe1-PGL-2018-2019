package logic.naturalDisasters;

import java.util.ArrayList;
import java.util.HashMap;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.board.cell.LandCell;
import logic.board.cell.WaterCell;
import logic.item.Capital;
import logic.player.Player;

public class LandErosion extends NaturalDisasters{
	private HashMap<Player, ArrayList<Cell>> newWaterCells;
	
	
	public LandErosion(Board board) {
		super(board);
		newWaterCells = new HashMap<>();
		// TODO Auto-generated constructor stub
	}

	private void erosion() {
		affectedCells.clear();
		ArrayList<Cell> waterCells = board.getWaterCells();
		int nWaterCells;
		double prob;
		for(Cell waterCell : waterCells) {
			for(Cell neighbour : board.getNeighbors(waterCell)) {
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
		}
		newWaterCells.put(board.getActivePlayer(), affectedCells);
	}
	
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
					if(board.canGoOn(c, cell.getItem())) {
						c.setDistrict(cell.getDistrict());
						c.setItem(cell.getItem());
						break;
					}
				}
			}
		}
		cell.removeDistrict();
		cell.removeItem();
		board.changeToWaterCell(cell.getX(), cell.getY());
	}
	
	private void cancel() {
		ArrayList<Cell> toCancel = newWaterCells.get(board.getActivePlayer());
		for(Cell c : toCancel) {
			board.changeToLandCell(c.getX(), c.getY());
		}
	}
	
	private double calcProb(int n) {
		return 40*Math.log10(n+1);
	}
	
	@Override
	public void play() {
		cancel();
		erosion();
	}
}
