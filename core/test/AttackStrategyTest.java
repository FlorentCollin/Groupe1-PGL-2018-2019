import org.junit.Before;

import logic.board.Board;
import logic.board.District;
import logic.player.Player;
import logic.player.ai.strategy.AttackStrategy;

public class AttackStrategyTest {
	InitStrategy init;
	@Before
	public void init() {
		init = new InitStrategy(new AttackStrategy());
		Board board = init.getBoard();
		Player enemy = new Player();
		District enemyDistrict = new District(enemy);
		for(int i = 0; i < 3; i++) {
			for(int j = 3; j < 5; j++) {
				board.getCell(i, j).setDistrict(enemyDistrict);
				enemyDistrict.addCell(board.getCell(i, j));
			}
		}
	}

}
