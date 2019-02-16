package logic.player.ai;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.item.Soldier;
import logic.player.Player;

public interface Strategy {

	/**
	 * Permet de dÈplacer un soldat sur la carte
	 * @param soldier le soldat ‡ dÈplacer
	 * */
	public void move(Soldier soldier);

	/**
	 * Permet d'acheter un soldat
	 * @return un nouveau soldat
	 * */
	public Soldier buy();

	/**
	 * Permet de placer un soldat nouvellement acheter sur le plateau de jeu
	 * @param soldier le soldat ‡†placer
	 * */
	public void placeNewSoldier(Soldier soldier);

	/**
	 * Permet d'attaquer un enemi ciblÈ
	 * @param soldier le soldat avec lequel attaquer
	 * @param enemy le joueur ‡ attaquer
	 * */
	public void attack(Soldier soldier, Player enemy);

	/**
	 * Permet de se d√©fendre
	 * @param soldier le soldat avec lequel d√©fendre*/
	public void defend(Soldier soldier);

	/**
	 * Permet de s√©l√©ctionner l'enemy √† attaquer
	 * @return le joueur √† attaquer
	 * */
	public Player selectEnemy();

	/**
	 * Permet de jouer le tour
	 * */
	public void play(Board board, ArrayList<Cell> cells, ArrayList<Soldier> soldiers);

}
