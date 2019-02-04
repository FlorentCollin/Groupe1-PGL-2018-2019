package logic.item;

import logic.player.Player;

public class Soldier extends Item{
	private SoldierLevel level;
	private boolean hasMoved = false;
	
	public Soldier(Player player) {
		super(player);
		this.mode = Mode.all;
		this.level = SoldierLevel.level1;
	}
	
	public Soldier(Player player, SoldierLevel level) {
		super(player);
		this.level = level;
		this.mode = Mode.all;
		mode.setPrice(level.getPrice());
	}
	
	public SoldierLevel getLevel() {
		return this.level;
	}

	public boolean getHasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	
	
}
