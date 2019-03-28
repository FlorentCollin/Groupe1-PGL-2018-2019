import org.junit.Before;
import org.junit.Test;

import ac.umons.slay.g01.logic.player.ai.strategy.AdaptativeStrategy;


public class AdaptativeStrategyTest extends StrategyTest {

	@Before
	public void init() {
		init = new InitStrategy(new AdaptativeStrategy());
		init.initEnemy();
	}
	
	@Test
	public void changeToDefenseTest() {
		//Rien à tester en fait, y a juste un calcul dans la classe
	}

}
