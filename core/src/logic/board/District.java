package logic.board;

import logic.board.cell.Cell;
import logic.item.Capital;

public class District {
	private Player player;
	private int gold,
				numbCell,
				numbTree;
	private Capital capital;
	private Cell[] cells;
	
	public District() {
		
	}
	
	public void addCell(Cell cell) {
		
	}
	
	public void addTree() {
		this.numbTree += 1;
	}
	
	public void setCapital(Capital capital) {
		this.capital = capital;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void calculateGold() {
		
	}
	
}
