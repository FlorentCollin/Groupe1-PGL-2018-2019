package item;

public class Soldier extends Item{
	private SoldierLevel level;
	
	public Soldier() {
		this.level = SoldierLevel.level1;
	}
	
	public Soldier(SoldierLevel level) {
		this.level = level;
	}
}
