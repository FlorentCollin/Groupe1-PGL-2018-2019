package logic.item;

import logic.player.Player;

public class Soldier extends Item{
	private SoldierLevel level;
	
	public Soldier(Player player) {
		super(player);
		this.mode = Mode.all;
		this.level = SoldierLevel.level1;
	}
	
	public Soldier(Player player, SoldierLevel level) {
		super(player);
		this.level = level;
	}
}
