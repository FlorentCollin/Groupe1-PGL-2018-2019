import static org.junit.Assert.assertTrue;

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
import logic.player.ai.RandomStrategy;
import logic.shop.Shop;

public class RandomStrategyTest {
	private AI ai;
	private District district;
	private Board board;
	
	@Before
	public void init() {
		ai = new AI(new RandomStrategy(), board);
		Player[] players = new Player[1];
		players[0] = ai;
		district = new District(ai);
		board = new Board(4, 3, players, new Shop());
		for(int i=0; i<2; i++) {
			for(int j=0; j<3; j++) {
				board.getCell(i, j).setDistrict(district);
				district.addCell(board.getCell(i, j));
			}
		}
	}
	
	@Test
	public void testMoveInTree() {
		board.getCell(0, 0).setItem(new Capital());
		board.getCell(0, 1).setItem(new Tree());
		board.getCell(1, 0).setItem(new Soldier(SoldierLevel.level1));
		
		ai.play();
		
		assertTrue(board.getCell(1, 0).getItem() == null);
	}

}
