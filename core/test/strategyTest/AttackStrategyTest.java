package strategyTest;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ac.umons.slay.g01.logic.item.Soldier;
import ac.umons.slay.g01.logic.item.level.SoldierLevel;
import ac.umons.slay.g01.logic.player.ai.strategy.AttackStrategy;

public class AttackStrategyTest extends StrategyTest{
	
	@Before
	public void init() {
		init = new InitStrategy(new AttackStrategy());
		init.initEnemy();
	}
	
	@Test
	public void testMove() {
		init.getDistrict().setGold(15);
		
		init.getAI().play();
		
		assertNull(init.getBoard().getCell(1, 1).getItem());
		assertNull(init.getBoard().getCell(2, 2).getItem());
		assertNull(init.getBoard().getCell(1, 2).getItem());
		assertTrue(init.getBoard().getCell(1, 3).getItem() instanceof Soldier);
		assertTrue(init.getBoard().getCell(2, 1).getItem() instanceof Soldier);
		assertSame(init.getBoard().getCell(1, 3).getDistrict().getPlayer(), init.getAI());
		assertTrue(init.getBoard().getCell(2, 1).getItem().getLevel() == SoldierLevel.level2 || init.getBoard().getCell(1, 3).getItem().getLevel() == SoldierLevel.level2);
	}
	
	@Test
	public void testBuy() {
		//Test1 destruction abre
		init.getDistrict().setGold(20);
		
		lockSoldiers();
		
		init.getAI().play();
		
		assertTrue(init.getBoard().getCell(2, 1).getItem() instanceof Soldier);
		
		//Test2 kill enemy
		init = new InitStrategy(new AttackStrategy());
		init.initEnemy();
		
		init.getDistrict().setGold(20);
		//Retrait arbre
		init.getBoard().getCell(2, 1).removeItem();
		
		lockSoldiers();
		
		init.getAI().play(); // car le joueur actif n'est pas une ia, il faut donc repasser la main à l'ia
		
		assertTrue(init.getBoard().getCell(1, 3).getItem() instanceof Soldier);
		
	}

}
