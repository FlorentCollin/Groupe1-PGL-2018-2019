package strategyTest;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ac.umons.slay.g01.logic.item.Item;
import ac.umons.slay.g01.logic.item.Soldier;
import ac.umons.slay.g01.logic.item.level.SoldierLevel;
import ac.umons.slay.g01.logic.player.ai.strategy.DefenseStrategy;

public class DefendStrategyTest extends StrategyTest{
	
	@Before
	public void initDefense() {
		init = new InitStrategy(new DefenseStrategy());
		init.initEnemy();
	}
	
	@Test
	public void moveTest() {
		init.getDistrict().setGold(0);
		init.getBoard().getCell(0, 2).setItem(new Soldier(SoldierLevel.level1));
		init.getBoard().getCell(2, 0).setItem(new Soldier(SoldierLevel.level1));
		
		Item soldier12 = init.getBoard().getCell(1, 2).getItem();
		Item soldier22 = init.getBoard().getCell(2, 2).getItem();
		init.getAI().play();
		
		assertSame(init.getBoard().getCell(1, 3).getDistrict().getPlayer(), init.getAI());
		assertSame(init.getBoard().getCell(1, 2).getItem(), soldier12);
		assertSame(init.getBoard().getCell(2, 2).getItem(), soldier22);
		assertTrue(init.getBoard().getCell(2, 1).getItem() instanceof Soldier);
		assertTrue(init.getBoard().getCell(0, 1).getItem() instanceof Soldier
				|| init.getBoard().getCell(1, 0).getItem() instanceof Soldier);
	}
	
	@Test
	public void buyTestForTree() {
		init.getDistrict().setGold(20);
		
		lockSoldiers();
		
		init.getAI().play();
		
		assertTrue(init.getBoard().getCell(2, 1).getItem() instanceof Soldier);
	}
	
	@Test
	public void buyTestForEnemy() {
		init.getBoard().getCell(2, 1).removeItem(); //Retrait de l'arbre
		
		lockSoldiers();
		
		init.getDistrict().setGold(20);
		
		init.getAI().play();
		
		assertSame(init.getBoard().getCell(1, 3).getDistrict().getPlayer(), init.getAI());
		assertTrue(init.getBoard().getCell(1, 3).getItem() instanceof Soldier);
	}
	
	@Test
	public void buyTestForDefense() {
		init.getBoard().getCell(2, 1).removeItem(); //Retrait de l'arbre
		init.getBoard().getCell(1, 3).setItem(new Soldier(SoldierLevel.level2)); //Mise en place d'un soldat de niveau supérieur à fin d'empêcher l'attaque du district
		
		lockSoldiers();
		
		init.getDistrict().setGold(20);
		
		init.getAI().play();
		
		assertTrue(init.getBoard().getCell(0, 1).getItem() instanceof Soldier
				|| init.getBoard().getCell(1, 0).getItem() instanceof Soldier
				|| init.getBoard().getCell(0, 3).getItem() instanceof Soldier
				|| init.getBoard().getCell(0, 4).getItem() instanceof Soldier
				|| init.getBoard().getCell(1, 4).getItem() instanceof Soldier);
	}

}
