package logic.item;

import logic.player.Player;

public abstract class Item {
	protected Mode mode;
	protected Player player; //afin de comparer pour Cell
	
	public Item(Player player) {
		this.player = player;
	}
	
	public Item() {
		
	}
	public Mode getMode() {
		return this.mode;
	}
	
}
