package logic.item;

import logic.item.level.Level;
import logic.item.level.SoldierLevel;

public abstract class Item {
	protected SoldierLevel level;
    protected String type = getClass().getName();
	
	protected boolean movable = false;
	protected boolean buyable = false;
	protected boolean improvable = false;
	
	protected boolean hasMoved = false; //A remplacer par canMove
	protected boolean hasSalary = false;
	
	protected int maxMove = 0;
	
	public Item() {
		
	}
	
	public boolean isStronger(Item item) {
		if(this.isImprovable() && item.isImprovable()) {
			return this.level.compareTo(item) >= 0;
		}
		return false;
	}
	
	/**
	 * Permet d'améliorer l'item
	 * */
	public void improve() {
		
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	
	public boolean canMove() {
		return !hasMoved;
	}
	
	public int getSalary() {
		return 0;
	}
	
	public boolean isMovable() {
		return movable;
	}
	
	public boolean isBuyable() {
		return buyable;
	}
	
	public boolean isImprovable() {
		return improvable && level.isNotMax();
	}
	
	public boolean hasSalary() {
		return hasSalary;
	}
	
	public int getMaxMove() {
		return maxMove;
	}
	
	public int getPrice() {
		return 0;
	}

	public void setType(String type) {
		this.type = type;
	}
}
