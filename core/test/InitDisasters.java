import java.util.concurrent.CopyOnWriteArrayList;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.naturalDisasters.naturalDisasterscontroller.NaturalDisastersController;
import ac.umons.slay.g01.logic.player.Player;
import ac.umons.slay.g01.logic.shop.Shop;

public class InitDisasters {
	private Board board;
	
	public InitDisasters() {
		CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();
		players.add(new Player());
		board = new Board(5, 5, players, true, new Shop());
	}
	
	public NaturalDisastersController getController() {
		return board.getNaturalDisastersController();
	}
	
	public Board getBoard() {
		return board;
	}

}
