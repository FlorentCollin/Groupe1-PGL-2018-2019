package logic.player.ai.strategy;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.District;

public interface Strategy {

	/**
	 * Permet de jouer le tour
	 * */
	public void play(Board board, ArrayList<District> districts);

}
