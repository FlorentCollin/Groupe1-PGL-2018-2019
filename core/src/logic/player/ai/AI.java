package logic.player.ai;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Soldier;
import logic.player.Player;

public class AI extends Player{
	private Strategy strategy;
	private Board board;
	private ArrayList<District> districts;
	
	public AI(Strategy strategy, Board board) {
		this.strategy = strategy;
		this.board = board;
		districts = new ArrayList<>();
	}
	
	/* 
	 * Permet à l'ia de jouer son tour en fonction de la stratégie qu'elle utilise
	 * */
	public void play() {
		strategy.play(board, districts);
//		board.nextPlayer();
	}
	
	public void addDistrict(District district) {
		districts.add(district);
	}
	
	public void removeDistrict(District district) {
		districts.remove(district);
	}
}
