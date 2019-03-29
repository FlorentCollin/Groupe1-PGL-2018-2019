package ac.umons.slay.g01.logic.player.ai;

import java.util.ArrayList;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.District;
import ac.umons.slay.g01.logic.player.Player;
import ac.umons.slay.g01.logic.player.ai.strategy.Strategy;

public class AI extends Player{
	private transient Strategy strategy;
	private transient Board board;
	private transient ArrayList<District> districts;
	
	public AI(Strategy strategy, Board board) {
		this.strategy = strategy;
		this.board = board;
		districts = new ArrayList<>();
	}
	
	/**
	 * Permet à l'ia de jouer son tour en fonction de la stratégie qu'elle utilise
	 * */
	public void play() {
		strategy.play(board, districts);
		board.nextPlayer();
	}
	
	/**
	 * Permet d'ajouter un district à l'ia
	 * @param district le district à ajouter
	 */
	public void addDistrict(District district) {
		districts.add(district);
	}
	
	/**
	 * Permet d'enlever un district à l'ia
	 * @param district le district à enlever
	 */
	public void removeDistrict(District district) {
		districts.remove(district);
	}
	
	public ArrayList<District> getDistrcits(){
		return districts;
	}
}
