package logic.item;

import logic.item.level.SoldierLevel;
import logic.player.Player;

public class TestItem {
	public static void main(String[] args) {
		Player p = new Player();
		Soldier s1 = new Soldier(p);
		Soldier s2 = new Soldier(p, SoldierLevel.level4);
		System.out.println(s1.getLevel().compareTo(s2.getLevel()));
	}
}
