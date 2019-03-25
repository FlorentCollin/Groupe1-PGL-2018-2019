import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import logic.item.Item;
import logic.item.Soldier;
import logic.item.level.SoldierLevel;
import logic.player.ai.strategy.DefenseStrategy;

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
	
		show();
		
		assertSame(init.getBoard().getCell(1, 3).getDistrict().getPlayer(), init.getAI());
		assertSame(init.getBoard().getCell(1, 2).getItem(), soldier12);
		assertSame(init.getBoard().getCell(2, 2).getItem(), soldier22);
		assertTrue(init.getBoard().getCell(2, 1).getItem() instanceof Soldier);
		assertTrue(init.getBoard().getCell(0, 1).getItem() instanceof Soldier
				|| init.getBoard().getCell(1, 0).getItem() instanceof Soldier);
	}
	
	private void show() {
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				if(init.getBoard().getCell(i, j).getItem() instanceof Soldier)
					System.out.println(i+", "+j+" "+init.getBoard().getCell(i, j).getItem()+" "+init.getBoard().getCell(i, j).getItem().getLevel()+" "+init.getBoard().getCell(i, j).getDistrict().getPlayer().getClass().getSimpleName());
			}
		}
	}

}
