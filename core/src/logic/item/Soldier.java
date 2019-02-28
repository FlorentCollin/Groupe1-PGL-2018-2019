package logic.item;

import logic.item.level.SoldierLevel;

public class Soldier extends Item{
	private SoldierLevel level;
	
	public Soldier(SoldierLevel level) {
		this.level = level;
		buyable = true;
		movable = true;
		improvable = true;
		hasSalary = true;
		maxMove = 4;
	}
	
	public SoldierLevel getLevel() {
		return this.level;
	}
	
	public void setLevel(SoldierLevel level) {
		this.level = level;
	}

	public boolean getHasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	
	public void improve() {
		int index = level.getIndex();
		level = SoldierLevel.values()[index+1];
		hasMoved = false;
	}
	
	@Override
	public boolean canMove() {
		return !hasMoved;
	}
	
	@Override
	public int getSalary() {
		return level.getSalary();
	}
	
	@Override
	public int getPrice() {
		return level.getPrice();
	}
	
}
