package logic.board.cell;

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
		if(this.item == null) {
			this.item = item;
		}
		else if(this.item.getClass().getSimpleName()=="Item") {
			/* to do */
		}
	}
	
}
