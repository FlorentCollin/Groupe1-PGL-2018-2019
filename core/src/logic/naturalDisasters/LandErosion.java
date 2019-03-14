package logic.naturalDisasters;

import java.util.ArrayList;
import java.util.Random;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.board.cell.LandCell;
import logic.board.cell.WaterCell;

public class LandErosion extends NaturalDisasters{
	private Random rand;
	private Board board;
	
	public LandErosion(Board board) {
		rand = new Random();
		this.board = board;
	}
	
	private void erosion() {
		affectedCells.clear();
		ArrayList<Cell> waterCells = board.getWaterCells();
		int nWaterCells;
		double prob;
		for(Cell waterCell : waterCells) {
			for(Cell neigbour : board.getNeighbors(waterCell)) {
				if(neigbour instanceof LandCell) {
					nWaterCells = 0;
					for(Cell cell : board.getNeighbors(neigbour)) {
						if(cell instanceof WaterCell) {
							nWaterCells ++;
						}
					}
					prob = calcProb(nWaterCells);
					if(rand.nextInt(101) <= prob) {
						affectedCells.add(neigbour);
					}
				}
			}
		}
	}
	
	private double calcProb(int n) {
		return 40*Math.log10(n+1);
	}
	
	@Override
	public ArrayList<Cell> getAffectedCells() {
		return affectedCells;
	}
	
	@Override
	public void play() {
		erosion();
	}
}
