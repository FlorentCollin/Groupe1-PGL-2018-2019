package ac.umons.slay.g01.logic.player.ai.strategy;

import java.util.ArrayList;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.District;

public interface Strategy {

	/**
	 * Permet de jouer le tour
	 * */
	void play(Board board, ArrayList<District> districts);

}
