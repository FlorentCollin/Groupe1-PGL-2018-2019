package logic.board.cell;

import logic.board.District;
import logic.item.Item;

public class Cell {
	private Item item;
	private District district;
	private int numberOfAdjacentWaterCell;
	private boolean actifIncome;
	protected boolean accessible;

	public Cell() {
		
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
}
