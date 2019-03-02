package memory;

import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Item;
import logic.player.Player;

public class Memory {
	private Cell cell;
	private Item lastItem;
	private District lastDistrict;
	private Cell lastCell;
	
	public Memory(Cell cell, Item lastItem, District district, Cell lastCell) {
		this.setCell(cell);
		this.setLastItem(lastItem);
		this.setLastDistrict(district);
		this.setLastCell(lastCell);
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	public Item getLastItem() {
		return lastItem;
	}

	public void setLastItem(Item lastItem) {
		this.lastItem = lastItem;
	}

	public District getLastDistrict() {
		return lastDistrict;
	}

	public void setLastDistrict(District lastDistrict) {
		this.lastDistrict = lastDistrict;
	}

	public Cell getLastCell() {
		return lastCell;
	}

	public void setLastCell(Cell lastCell) {
		this.lastCell = lastCell;
	}

}
