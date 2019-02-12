package unitTests;

import logic.item.Soldier;
import logic.item.level.SoldierLevel;
import logic.player.Player;

public class CellTest {
	public static void main(String[] args) {
		Player p = new Player();
		Soldier s = new Soldier(p, SoldierLevel.level2);
		for(SoldierLevel l : SoldierLevel.values()) {
			System.out.println(l.hashCode());
		}
		System.out.println(s.getLevel().hashCode());
	}
}
