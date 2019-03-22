package logic.allianceController;

import logic.board.Board;
import logic.player.Player;

public class AllianceController {
	private boolean[][] alliances;
	private Board board;
	public AllianceController(Board board) {
		this.board = board;
		int size = board.getPlayers().size();
		alliances = new boolean[size][size];
		generateAlliances();
	}
	
	private void generateAlliances() {
		for(int i = 0; i < alliances.length; i++) {
			for(int j = 0; j < alliances[0].length; j++) {
				if(i==j) {
					alliances[i][j] = true;
				}
				else {
					alliances[i][j] = false;
				}
			}
		}
	}
	
	public void setAlliance(Player player1, Player player2) {
		int i = getIndex(player1);
		int j = getIndex(player2);
		alliances[i][j] = true;
	}
	
	public void breakAlliance(Player player1, Player player2) {
		int i = getIndex(player1);
		int j = getIndex(player2);
		alliances[i][j] = false;
	}
	
	private int getIndex(Player player) {
		return board.getPlayers().indexOf(player);
	}
	
	public boolean areAllied(Player player1, Player player2) {
		int i = board.getPlayers().indexOf(player1);
		int j = board.getPlayers().indexOf(player2);
		return alliances[i][j];
	}

}
