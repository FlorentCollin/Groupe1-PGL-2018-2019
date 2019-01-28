package logic.player.ai;

import logic.item.Soldier;
import logic.player.Player;

public abstract class Strategy {
	
	public Strategy() {
		
	}
	
	public void move(Soldier soldier) {
		/* TO DO */
	}
	
	public Soldier buy() {
		/* TO DO */
		return null;
	}
	
	public void placeNewSoldier(Soldier soldier) {
		/* TO DO */
	}
	
	public void attack(Soldier soldier, Player enemy) {
		/* to do */
	}
	
	public void defend(Soldier soldier) {
		/* to do */
	}
	
	public Player selectEnemy() {
		/* to do */
		return null;
	}
	
}
