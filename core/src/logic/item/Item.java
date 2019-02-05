package logic.item;

import logic.item.level.Level;
import logic.player.Player;

public abstract class Item {
	protected Mode mode;
	protected Player player; //afin de comparer pour Cell
	
	public Item(Player player) {
		this.player = player;
	}
	
	public Item() {
		
	}
	
	/**
	 * Permet de savoir si un item peut être achetable, déplaçable ou améliorable
	 * @return le mode dans lequel se trouve l'item
	 * */
	public Mode getMode() {
		return this.mode;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void improve() {
		
	}
	
	public Level getLevel() {
		return null;
	}
}
