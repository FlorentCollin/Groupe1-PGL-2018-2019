package unitTests;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;

public class BoardTest {
	Board board;
	
	@Before
	public void init() {
		Player p1 = new Player();
		Player p2 = new Player();
		Player[] players = new Player[2];
		players[0] = p1;
		players[1] = p2;
		NaturalDisastersController naturalDisastersController = new NaturalDisastersController();
		board = new Board(5,5, players, naturalDisastersController);
		
	}
	
	@Test
	public void testMove() {
		board.move();
	}
	
	@Test
	public void testPossibleMove() {
		
	}

}
