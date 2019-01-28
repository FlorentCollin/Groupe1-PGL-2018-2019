package logic.player.ai;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.item.Soldier;

public class AI {
	private Strategy strategy;
	private Board board;
	private Cell ownCells[];
	private Soldier selectedSoldier;
	
	public AI(Strategy strategy) {
		this.strategy = strategy;
	}
	
	/* 
	 * Permet � l'ia de jouer son tour en fonction de la strat�gie qu'elle utilise
	 * */
	public void play() {
		/* TO DO */
	}
}
