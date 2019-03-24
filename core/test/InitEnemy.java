import logic.board.Board;
import logic.board.District;
import logic.player.Player;

public class InitEnemy {
	private Player player;
	private District district;
	
	public InitEnemy(Board board) {
		player = new Player();
		District district = new District(player);
		for(int i = 0; i < 3; i++) {
			for(int j = 3; j < 5; j++) {
				board.getCell(i, j).setDistrict(district);
				district.addCell(board.getCell(i, j));
			}
		}
	}

}
