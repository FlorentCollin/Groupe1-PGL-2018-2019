package logic.item;

import com.google.gson.annotations.SerializedName;
import logic.item.level.Level;
import logic.item.level.SoldierLevel;

public abstract class Item {
	protected SoldierLevel level;
    protected String t = getClass().getName();

	protected transient boolean movable = false;
	protected transient boolean buyable = false;
	protected transient boolean improvable = false;

	@SerializedName("m") //Permet de réduire considérablement la taille des messages envoyés par le serveur
	protected boolean hasMoved = false;
	protected transient boolean hasSalary = false;
	
	protected transient int maxMove = 0;
	
	public Item() {
		
	}
	
	public boolean isStronger(Item item) {
		if(this.isImprovable() && item.isImprovable()) {
			return this.level.compareTo(item) > 0;
		}
		return false;
	}
	
	/**
	 * Permet d'améliorer l'item
	 * */
	public void improve() {
		
	}

	public void update() {
		movable = false;
		buyable = false;
		improvable = false;
		hasMoved = false;
		hasSalary = false;
		maxMove = 0;
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
		this.t = type;
	}
}
