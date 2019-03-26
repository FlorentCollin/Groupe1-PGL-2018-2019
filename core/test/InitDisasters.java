import java.util.concurrent.CopyOnWriteArrayList;

import logic.board.Board;
import logic.naturalDisasters.naturalDisasterscontroller.NaturalDisastersController;
import logic.player.Player;
import logic.shop.Shop;

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
