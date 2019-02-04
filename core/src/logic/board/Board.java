package logic.board;

import java.util.ArrayList;

import logic.board.cell.Cell;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;
import logic.shop.Shop;

public class Board{
	private Cell[][] board;
	private int columns, rows, activePlayer;
	private Player[] players;
	private ArrayList<District> districts;
	private District activeDistrict;
	private Shop shop;
	private NaturalDisastersController naturalDisastersController;
	//utiliser dictionnaire {cell : district} pour connaître plus vite le district d'une cellule
	
	public Board(int columns, int rows, Player[] players,NaturalDisastersController naturalDisastersController, Shop shop){
		this.columns = columns;
		this.rows = rows;
		board = new Cell[rows][columns];
		this.players = players;
		this.naturalDisastersController = naturalDisastersController;
		this.districts = new ArrayList<District>();
		this.shop = shop;
	}
	
	/**
	 * Permet de placer un item sur une cellule du plateau
	 * @param item l'item à placer
	 * @param i la position en x que doit prendre l'item
	 * @param j la position en y que doit prendre l'item
	 * */
	public void placeNewItem(int i, int j){
		if(i >= 0 && i < rows && j >= 0 && j < columns) {
			Cell cell = board[i][j];
			if(shop.getSelectedITem() != null) {
				
			}
		}
	}
	
	/**
	 * Peremet de déplacer un item d'une cellule à l'autre
	 * @param fromCell la cellule de départ
	 * @param toCell la cellule de destination
	 * */
	public void move(Cell fromCell, Cell toCell) {
		
	}
	
	/**
	 * Permet de connaître les mouvements possibles d'un item
	 * @param cell la cellule où se trouve l'item
	 * @return les cellules sur lesquelles peut se déplacer l'item
	 * */
	public Cell[] possibleMove(Cell cell) {
		return null;
	}
	
	/**
	 *Permet de vérifier qu'une cellule est un choix possible
	 *@param possibleMove les déplacements possibles
	 *@param cell la cellule où souhaite se placer le joueur
	 *@return true si cell fait partie de possibleMove
	 *sinon false
	 **/
	private boolean isInPossibleMove(Cell[] possibleMove, Cell cell) {
		for(Cell c : possibleMove) {
			if(c == cell) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Permet de connaître les position possibles pour placer un nouveau soldat
	 * @param district le district d'où le soldat a été acheté
	 * @return les cellules sur lesquelles peut être placé le nouveau soldat
	 * */
	public Cell[] possibleMove(District district) {
		return null;
	}
	
	/**
	 * Permet de passer au joueur suivant
	 */
	public void nextPlayer() {
		activePlayer = (activePlayer + 1)%(players.length);
	}

	public District getActiveDistrict() {
		return activeDistrict;
	}

	public void setActiveDistrict(District activeDistrict) {
		this.activeDistrict = activeDistrict;
	}
}
