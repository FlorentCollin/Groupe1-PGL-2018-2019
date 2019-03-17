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
	
	
	public LandErosion(Board board) {
		super(board);
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
		modificatedCells.put(board.getActivePlayer(), affectedCells);
		changeToWaterCells();
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
		board.addModification(cell);
	}
	
	private void changeToWaterCells() {
		for(Cell cell : modificatedCells.get(board.getActivePlayer())) {
			board.changeToWaterCell(cell.getX(), cell.getY());
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
