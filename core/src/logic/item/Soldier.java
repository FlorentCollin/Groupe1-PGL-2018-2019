package logic.item;

import logic.item.level.SoldierLevel;
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
	
	public void setLevel(SoldierLevel level) {
		this.level = level;
	}

	public boolean getHasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	
	public void improve() {
		switch(level) {
		case level1:
			level = SoldierLevel.level2;
			break;
		case level2:
			level = SoldierLevel.level3;
			break;
		case level3:
			level = SoldierLevel.level4;
			break;
		default:
			break;
		}
		hasMoved = false;
	}
	
	@Override
	public boolean canMove() {
		return !hasMoved;
	}
	
}
