package logic.player.ai;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.player.Player;

public class AI extends Player{
	private Strategy strategy;
	private Board board;
	private ArrayList<Cell> ownCells;
	
	public AI(Strategy strategy, Board board) {
		this.strategy = strategy;
		this.board = board;
		ownCells = new ArrayList<Cell>();
	}
	
	/* 
	 * Permet � l'ia de jouer son tour en fonction de la strat�gie qu'elle utilise
	 * */
	public void play() {
		this.strategy.play(board, ownCells, soldiers);
	}
}
