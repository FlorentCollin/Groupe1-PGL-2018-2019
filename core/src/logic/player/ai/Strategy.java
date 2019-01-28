package logic.player.ai;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.item.Soldier;
import logic.player.Player;

public abstract class Strategy {

	public Strategy() {

	}

	/*
	 * Permet de déplacer un soldat sur la carte
	 * @param soldier le soldat à déplacer
	 * */
	public void move(Soldier soldier) {
		/* TO DO */
	}

	/*
	 * Permet d'acheter un soldat
	 * @return un nouveau soldat
	 * */
	public Soldier buy() {
		/* TO DO */
		return null;
	}

	/*
	 * Permet de placer un soldat nouvellement acheter sur le plateau de jeu
	 * @param soldier le soldat à placer
	 * */
	public void placeNewSoldier(Soldier soldier) {
		/* TO DO */
	}

	/*
	 * Permet d'attaquer un enemi ciblé
	 * @param soldier le soldat avec lequel attaquer
	 * @param enemy le joueur à attaquer
	 * */
	public void attack(Soldier soldier, Player enemy) {
		/* to do */
	}

	/*
	 * Permet de se défendre
	 * @param soldier le soldat avec lequel défendre*/
	public void defend(Soldier soldier) {
		/* to do */
	}

	/*
	 * Permet de séléctionner l'enemy à attaquer
	 * @return le joueur à attaquer
	 * */
	public Player selectEnemy() {
		/* to do */
		return null;
	}

	/*
	 * Permet de jouer le tour
	 * */
	public void play(Board board, ArrayList<Cell> cells, ArrayList<Soldier> soldiers) {
		/* to do */
	}

}
