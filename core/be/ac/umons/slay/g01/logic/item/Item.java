package ac.umons.slay.g01.logic.item;

import com.google.gson.annotations.SerializedName;

import ac.umons.slay.g01.logic.item.level.Level;
import ac.umons.slay.g01.logic.item.level.SoldierLevel;

public abstract class Item {
	protected SoldierLevel level;
    protected String t = getClass().getName();

    @SerializedName("m")
	protected  boolean movable = false;
    @SerializedName("b")
	protected  boolean buyable = false;
    @SerializedName("i")
	protected  boolean improvable = false;

	@SerializedName("hm") //Permet de réduire considérablement la taille des messages envoyés par le serveur
	protected boolean hasMoved = false;
	@SerializedName("hs")
	protected boolean hasSalary = false;

	@SerializedName("mm")
	protected int maxMove = 0;
	
	public Item() {
		
	}
	
	/**
	 * Permet de savoir si l'item est supérieur à un autre
	 * @param item l'autre item
	 * @return true si l'item est supérieur
	 * 			false sinon
	 */
	public boolean isStronger(Item item) {
		if(this.isImprovable() && item.isImprovable()) {
			return this.level.compareTo(item) > 0;
		}
		return false;
	}
	
	/**
	 * Permet de savoir si l'item est égal à un autre item
	 * @param item l'autre item
	 * @return true si les deux items sont égaux
	 * 			false sinon
	 */
	public boolean isEqual(Item item) {
		if(this.isImprovable() && item.isImprovable()) {
			return this.level.compareTo(item) == 0; 
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
		this.t = type;
	}
}
