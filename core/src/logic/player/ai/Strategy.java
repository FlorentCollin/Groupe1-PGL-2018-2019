package logic.player.ai;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.item.Soldier;
import logic.player.Player;

public interface Strategy {

	/**
	 * Permet de d�placer un soldat sur la carte
	 * @param soldier le soldat � d�placer
	 * */
	public void move(Soldier soldier);

	/**
	 * Permet d'acheter un soldat
	 * @return un nouveau soldat
	 * */
	public Soldier buy();

	/**
	 * Permet de placer un soldat nouvellement acheter sur le plateau de jeu
	 * @param soldier le soldat �placer
	 * */
	public void placeNewSoldier(Soldier soldier);

	/**
	 * Permet d'attaquer un enemi cibl�
	 * @param soldier le soldat avec lequel attaquer
	 * @param enemy le joueur � attaquer
	 * */
	public void attack(Soldier soldier, Player enemy);

	/**
	 * Permet de se défendre
	 * @param soldier le soldat avec lequel défendre*/
	public void defend(Soldier soldier);

	/**
	 * Permet de séléctionner l'enemy à attaquer
	 * @return le joueur à attaquer
	 * */
	public Player selectEnemy();

	/**
	 * Permet de jouer le tour
	 * */
	public void play(Board board, ArrayList<Cell> cells, ArrayList<Soldier> soldiers);

}
