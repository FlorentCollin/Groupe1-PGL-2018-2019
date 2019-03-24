import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import logic.item.Soldier;
import logic.item.level.SoldierLevel;
import logic.player.ai.strategy.AttackStrategy;

public class AttackStrategyTest {
	InitStrategy init;
	
	@Before
	public void init() {
		init = new InitStrategy(new AttackStrategy());
		init.initEnemy();
	}
	
	@Test
	public void testMove() {
		init.getDistrict().setGold(0);
		//Récupération des soldat ne devant pas bouger
		//soldierxy où xy sont les coordonnées de la cellule où se situe le soldat
		Soldier soldier12 = (Soldier)init.getBoard().getCell(1, 2).getItem();
		Soldier soldier01 = (Soldier)init.getBoard().getCell(0, 1).getItem();
		
		init.getAI().play();
		
		assertSame(init.getBoard().getCell(1, 3).getItem(), soldier12);
		assertNull(init.getBoard().getCell(1, 1).getItem());
		assertNull(init.getBoard().getCell(2, 2));
		assertTrue(init.getBoard().getCell(1, 1).getItem() instanceof Soldier);
		assertTrue(init.getBoard().getCell(2, 1).getItem() instanceof Soldier);
		assertSame(init.getBoard().getCell(2, 1).getItem().getLevel(), SoldierLevel.level2);
	}

}
