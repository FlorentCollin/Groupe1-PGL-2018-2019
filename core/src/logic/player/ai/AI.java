package logic.player.ai;

import logic.board.Board;
import logic.board.District;
import logic.player.Player;
import logic.player.ai.strategy.Strategy;

import java.util.ArrayList;

public class AI extends Player{
	private transient Strategy strategy;
	private transient Board board;
	private transient ArrayList<District> districts;
	
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
		board.nextPlayer();
	}
	
	public void addDistrict(District district) {
		districts.add(district);
	}
	
	public void removeDistrict(District district) {
		districts.remove(district);
	}
	
	public ArrayList<District> getDistrcits(){
		return districts;
	}
}
