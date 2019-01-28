package logic.item;

import logic.player.Player;

public abstract class Item {
	protected Mode mode;
	protected Player player; //afin de comparer pour Cell
	
	public Mode getMode() {
		return this.mode;
	}
	
}
