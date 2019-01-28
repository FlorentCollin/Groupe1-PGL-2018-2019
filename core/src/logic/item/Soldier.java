package logic.item;

public class Soldier extends Item{
	private SoldierLevel level;
	
	public Soldier() {
		this.mode = Mode.all;
		this.level = SoldierLevel.level1;
	}
	
	public Soldier(SoldierLevel level) {
		this.level = level;
	}
}
