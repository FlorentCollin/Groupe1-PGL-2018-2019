package logic.item;

/* 
 * Cette classe sert à connaître le mode dans lequel se trouve un item.
 * C'est à dire, si il est possible de l'acheter, le déplacer ou encore l'améliorer.
 * De cette façon il est plus aisé d'ajouter d'autres items.
 * Si on utilisait des classes on aurait besoin de trois Interface : 
 * 			Buyable
 * 			Movable
 * 			Improvable
 * Ceci rend plus compliqué l'implémentation.
 * 
 * On pourrait changer par des superclasses à la limite en fait ... à voir demain*/
public enum Mode {
	onlyBuyable(true, false, false), onlyMovable(false, true, false), onlyImprovable(false, false, true),
	notByable(false, true, true), notMovable(true, false, true), notImprovable(true, true, false),
	all(true, true, true), nothing(false, false, false);
	
	private boolean buyable, movable, improvable;
	private int price = 0;
	private int maxDep = 0;
	
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
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public int getPrice() {
		return this.price;
	}

	public int getMaxDep() {
		return maxDep;
	}

	public void setMaxDep(int maxDep) {
		this.maxDep = maxDep;
	}
}
