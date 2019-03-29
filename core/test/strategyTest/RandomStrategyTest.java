package strategyTest;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ac.umons.slay.g01.logic.item.Soldier;
import ac.umons.slay.g01.logic.item.level.SoldierLevel;
import ac.umons.slay.g01.logic.player.ai.strategy.RandomStrategy;

public class RandomStrategyTest extends StrategyTest{
	
	@Before
	public void initRandom() {
		init = new InitStrategy(new RandomStrategy());
	}

	@Test
	public void randomMove() {
		init.getDistrict().setGold(0);
		
		Soldier soldier = new Soldier(SoldierLevel.level1);
		init.getBoard().getCell(1, 1).setItem(soldier);

		init.getAI().play();

		assertNull(init.getBoard().getCell(1, 1).getItem());
		assertSame(init.getBoard().getCell(2, 1).getItem(), soldier);
	}
	
	@Test
	public void randomBuy() {
		init.getDistrict().setGold(15);
		
		lockSoldiers();
		
		init.getAI().play();
		
		assertTrue(init.getBoard().getCell(2, 1).getItem() instanceof Soldier);
		assertSame(init.getBoard().getCell(2, 1).getItem().getLevel(), SoldierLevel.level1);
	}

}
