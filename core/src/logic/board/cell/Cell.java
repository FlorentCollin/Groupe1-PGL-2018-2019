package logic.board.cell;

import logic.board.District;
import logic.item.Item;

public class Cell {
	private int x,y;
	private Item item; // si null alors il n'y a pas d'item actuellement sur la cellule
	private transient District district; // si null alors n'appartient actuellement à aucun district

//	private int numberOfAdjacentWaterCell; //?
//	private boolean actifIncome; //?
	protected boolean accessible;
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Cell(District district) {
		this.district = district;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public Item getItem() {
		return this.item;
	}
	
	public void setDistrict(District district) {
		this.district = district;
	}
	
	public District getDistrict() {
		return this.district;
	}
	
	public void removeDistrict() {
		district = null;
	}
	
	public void removeItem() {
		item = null;
	}
	
	public boolean isAccessible() {
		return accessible;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Integer[] getId() {
	    return new Integer[] {x, y};
    }

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof Cell)) {
			return false;
		}
		Cell other = (Cell) obj;
		return this.x == other.getX() && this.y == other.getY();
	}
}
