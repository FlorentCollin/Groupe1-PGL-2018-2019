package logic.item;

/* 
 * Cette classe sert � conna�tre le mode dans lequel se trouve un item.
 * C'est � dire, si il est possible de l'acheter, le d�placer ou encore l'am�liorer.
 * De cette fa�on il est plus ais� d'ajouter d'autres items.
 * Si on utilisait des classes on aurait besoin de trois Interface : 
 * 			Buyable
 * 			Movable
 * 			Improvable
 * Ceci rend plus compliqu� l'impl�mentation.
 * 
 * On pourrait changer par des superclasses � la limite en fait ... � voir demain*/
public enum Mode {
	onlyBuyable(true, false, false), onlyMovable(false, true, false), onlyImprovable(false, false, true),
	notByable(false, true, true), notMovable(true, false, true), notImprovable(true, true, false),
	all(true, true, true), nothing(false, false, false);
	
	private boolean buyable, movable, improvable;
	
	Mode(boolean buyable, boolean movable, boolean improvable){
		this.buyable = buyable;
		this.movable = movable;
		this.improvable = improvable;
	}
	
	public boolean isBuyable() {
		return this.buyable;
	}
	
	public boolean isMovable() {
		return this.movable;
	}
	
	public boolean isImprovable() {
		return this.improvable;
	}
}
