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
	 * Permet de d�placer un soldat sur la carte
	 * @param soldier le soldat � d�placer
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
	 * @param soldier le soldat � placer
	 * */
	public void placeNewSoldier(Soldier soldier) {
		/* TO DO */
	}
	
	/*
	 * Permet d'attaquer un enemi cibl�
	 * @param soldier le soldat avec lequel attaquer
	 * @param enemy le joueur � attaquer
	 * */
	public void attack(Soldier soldier, Player enemy) {
		/* to do */
	}
	
	/*
	 * Permet de se d�fendre
	 * @param soldier le soldat avec lequel d�fendre*/
	public void defend(Soldier soldier) {
		/* to do */
	}
	
	/*
	 * Permet de s�l�ctionner l'enemy � attaquer
	 * @return le joueur � attaquer
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
