package logic.item;

import logic.item.level.SoldierLevel;

public class Soldier extends Item{


	public Soldier(SoldierLevel level) {
		this.level = level;
		improvable = true;
		movable = true;
		buyable = true;
		hasSalary = true;
		maxMove = 4;
	}

	@Override
	public void update() {
		improvable = true;
		movable = true;
		buyable = true;
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
		SoldierLevel soldierLevel = (SoldierLevel) level;
		return soldierLevel.getSalary();
	}
	
	@Override
	public int getPrice() {
		SoldierLevel soldierLevel = (SoldierLevel) level;
		return soldierLevel.getPrice();
	}
	
}
