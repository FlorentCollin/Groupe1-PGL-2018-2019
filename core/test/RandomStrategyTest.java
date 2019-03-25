import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Before;
import org.junit.Test;

import logic.board.Board;
import logic.board.District;
import logic.item.Capital;
import logic.item.Soldier;
import logic.item.Tree;
import logic.item.level.SoldierLevel;
import logic.player.Player;
import logic.player.ai.AI;
import logic.player.ai.strategy.RandomStrategy;
import logic.shop.Shop;

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
		init.getDistrict().setGold(10);
		
		init.getAI().play();
		
		assertTrue(init.getBoard().getCell(2, 1).getItem() instanceof Soldier);
		assertSame(init.getBoard().getCell(2, 1).getItem().getLevel(), SoldierLevel.level1);
	}

}
