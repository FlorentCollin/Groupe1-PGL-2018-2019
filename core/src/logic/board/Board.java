package logic.board;

import java.util.ArrayList;
import java.util.Random;

import logic.board.cell.Cell;
import logic.item.Capital;
import logic.item.Soldier;
import logic.item.Tree;
import logic.item.level.SoldierLevel;
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
	private Cell selectedCell;
	private final int PROBA;
	//utiliser dictionnaire {cell : district} pour connaître plus vite le district d'une cellule
	
	public Board(int columns, int rows, Player[] players,NaturalDisastersController naturalDisastersController, Shop shop){
		this.columns = columns;
		this.rows = rows;
		board = new Cell[rows][columns];
		this.players = players;
		this.naturalDisastersController = naturalDisastersController;
		this.districts = new ArrayList<District>();
		this.shop = shop;
		fullIn();
		activePlayer = 0;
		PROBA = 100;
	}
	
	private void fullIn() {
		for(int i = 0; i<rows; i++) {
			for(int j = 0; j<columns; j++) {
				board[i][j] = new Cell();
			}
		}
	}
	
	/**
	 * Permet de placer un item sur une cellule du plateau
	 * @param i la position en x que doit prendre l'item
	 * @param j la position en y que doit prendre l'item
	 * */
	public void placeNewItem(int i, int j){
		if(i >= 0 && i < rows && j >= 0 && j < columns) {
			Cell cell = board[i][j];
			//On vérifie qu'un item à été sélectionné dans le shop
			if(shop.getSelectedItem() != null && activeDistrict.getPlayer() == players[activePlayer]) {
				//On vérifie que la case où souhaite se placer le joueur est un choix possible
				if(isInPossibleMove(possibleMove(activeDistrict), cell)) {
					//Cas où le joueur place l'item sur son district
					if(cell.getDistrict() == activeDistrict) {
						//la cellule ne contient pas d'item
						if(cell.getItem() == null) {
							cell.setItem(shop.getSelectedItem());
							shop.buy(activeDistrict);
						}
						else if(cell.getItem().getClass().isInstance(shop.getSelectedItem())) {
							if(cell.getItem().getMode().isImprovable()) {
								cell.getItem().improve();
								shop.buy(activeDistrict);
							}
						}
						//la cellule contient un arbre
						else if(cell.getItem() instanceof Tree) {
							activeDistrict.setGold(activeDistrict.getGold()+3);
							cell.setItem(shop.getSelectedItem());
							shop.buy(activeDistrict);
						}
					}
					else {
						// la cellule n'appartient à personne
						if(cell.getDistrict() == null) {
							cell.setItem(shop.getSelectedItem());
							shop.buy(activeDistrict);
							activeDistrict.addCell(cell);
							cell.setDistrict(activeDistrict);
						}
						//la cellule appartient à un joueur enemi
						else {
							//Il y a un soldat sur la cellule
							if(cell.getItem() instanceof Soldier) {
								//On vérifie que le soldat qu'on veut ajouter est de niveau égual ou supérieur
								if(((Soldier)shop.getSelectedItem()).getLevel().compareTo((SoldierLevel) cell.getItem().getLevel()) >= 0) {
									cell.setItem(shop.getSelectedItem());
									shop.buy(activeDistrict);
									activeDistrict.addCell(cell);
									cell.setDistrict(activeDistrict);
								}
							}
							else if(cell.getItem() instanceof Capital) {
								generateCapital(cell.getDistrict());
								cell.setItem(shop.getSelectedItem());
								//TO DO
							}
							//Il a tout autre chose sur la cellule
							else {
								cell.setItem(shop.getSelectedItem());
								shop.buy(activeDistrict);
								activeDistrict.addCell(cell);
								cell.setDistrict(activeDistrict);
							}
							//Ajouter le fait d'attérir sur une tour par exemple
						}
					}
				}
			}
		}
	}
	
	/**
	 * Permet de déplacer un item d'une cellule à l'autre
	 * @param toCell la cellule de destination
	 * */
	public void move(Cell toCell) {
		if(selectedCell != null && selectedCell.getItem() != null && selectedCell.getItem().getMode().isMovable()) {
			if(isInPossibleMove(possibleMove(selectedCell), toCell)) {
				if(toCell.getItem() instanceof Capital) {
					generateCapital(toCell.getDistrict());
				}
				if(toCell.getDistrict().getPlayer() == players[activePlayer]) {
					if(toCell.getItem().getClass().isInstance(selectedCell.getItem())) {
						SoldierFusion(toCell);						
					}
				}
				toCell.setDistrict(activeDistrict);
				activeDistrict.addCell(toCell);
				toCell.setItem(selectedCell.getItem());
				selectedCell.removeItem();
				checkMerge(toCell);
			}
		}
	}
	
	/**
	 * Permet de fusionner des items
	 * @param cell la cellule surlaquelle la fusion doit être faîte
	 * */
	private void SoldierFusion(Cell cell) {
		cell.getItem().improve();
	}
	
	/**
	 * Permet de savoir si une cellule appartient à un joueur
	 * @param cell la cellule à tester
	 * */
	private boolean isOnOwnTerritory(Cell cell) {
		if(cell.getDistrict() == null) {
			return false;
		}
		else if(cell.getDistrict().getPlayer() == null) {
			return false;
		}
		else if(cell.getDistrict().getPlayer() != players[activePlayer]) {
			return false;
		}
		return true;
	}
	
	/**
	 * Permet de savoir si la cellule sur lqauelle on souhaite se placer possède un item identique
	 * @param cell la cellule à tester
	 * @param isNewItem détermine si l'item qu'on souhaite ajouter sur la cellule provient du shop
	 * @return true si les items sont identiques
	 * 			false sinon
	 * */
	private boolean isSameItem(Cell cell, boolean isNewItem) {
		if(isNewItem) {
			if(cell.getItem().getClass().isInstance(shop.getSelectedItem())) {
				return true;
			}
		}
		else {
			if(cell.getItem().getClass().isInstance(selectedCell.getItem())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Permet de connaître les mouvements possibles d'un item
	 * @param cell la cellule où se trouve l'item
	 * @return les cellules sur lesquelles peut se déplacer l'item
	 * */
	public ArrayList<Cell> possibleMove(Cell cell) {
		ArrayList<Cell> possible = new ArrayList<Cell>();
		possible.add(cell);
		for(Cell c : getAround(cell, false)) {
			if(possible.indexOf(c) == -1) {
				possible.add(c);
			}
		}
		return possible;
	}
	
	/**
	 * Permet de récupérer les cellules surlesquelles il est possible
	 * de se placer autour d'une cellule cible
	 * @param cell la cellule cible
	 * @param isDistrict permet de savoir si on cherche pour une cellule ou pour un district
	 * @return les cellules autour*/
	private ArrayList<Cell> getAround(Cell cell, boolean isDistrict){
		ArrayList<Cell> around = new ArrayList<Cell>();
		int i = getPosition(cell)[0];
		int j = getPosition(cell)[1];
		if(getTop(i,j) != null && canDoIt(cell, getTop(i,j), isDistrict)) {
			around.add(getTop(i,j));
		}
		if(getBottom(i,j) != null && canDoIt(cell, getBottom(i,j), isDistrict)) {
			around.add(getBottom(i,j));
		}
		if(getRight(i,j) != null && canDoIt(cell, getRight(i,j), isDistrict)) {
			around.add(getRight(i,j));
		}
		if(getLeft(i,j) != null && canDoIt(cell, getLeft(i,j), isDistrict)) {
			around.add(getLeft(i,j));
		}
		if(getTopRight(i,j) != null && canDoIt(cell, getTopRight(i,j), isDistrict)) {
			around.add(getTopRight(i,j));
		}
		if(getTopLeft(i,j) != null && canDoIt(cell, getTopLeft(i,j), isDistrict)) {
			around.add(getTopLeft(i,j));
		}
//		if(getBottomRight(i,j) != null && canDoIt(cell, getBottomRight(i,j), district)) {
//			around.add(getBottomRight(i,j));
//		}
//		if(getBottomLeft(i,j) != null && canDoIt(cell, getBottomLeft(i,j), district)) {
//			around.add(getBottomLeft(i,j));
//		}
		return around;
	}
	
	/**
	 * Permet de savoir si le mouvement est possible 
	 * au niveau des items présents sur les cellules
	 * @param c1 la cellule de départ
	 * @param c2 la cellule de destination
	 * @param isDistrict permet de savoir si on cherche pour un district ou une cellule
	 * @return true si il est possible de se placer sur la cellule c2
	 * 			false sinon*/
	private boolean canDoIt(Cell c1, Cell c2, boolean isDistrict) {
		if(isDistrict) {
			return true;
		}
		else {
			if(c2.getItem() instanceof Soldier) {
				if(((Soldier)c2.getItem()).getLevel().compareTo(((Soldier)c1.getItem()).getLevel()) > 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 *Permet de vérifier qu'une cellule est un choix possible
	 *@param possibleMoves les déplacements possibles
	 *@param cell la cellule où souhaite se placer le joueur
	 *@return true si cell fait partie de possibleMove
	 *sinon false
	 **/
	private boolean isInPossibleMove(ArrayList<Cell> possibleMoves, Cell cell) {
		/*for(Cell c : possibleMoves) {
			if(c == cell) {
				return true;
			}
		}
		return false;*/
		return possibleMoves.indexOf(cell) > -1;
	}
	
	/**
	 * Permet de connaître les position possibles pour placer un nouveau soldat
	 * @param district le district d'où le soldat a été acheté
	 * @return les cellules sur lesquelles peut être placé le nouveau soldat
	 * */
	public ArrayList<Cell> possibleMove(District district) {
		ArrayList<Cell> possible = district.getCells();
		ArrayList<Cell> toAdd = new ArrayList<Cell>();
		for(Cell c : possible) {
			toAdd.addAll(getAround(c, true));
		}
		//Permet d'éviter les doublons
		for(Cell c : toAdd) {
			if(possible.indexOf(c) == -1) {
				possible.add(c);
			}
		}
		return possible;
	}
	
	/**
	 * Permet de passer au joueur suivant
	 */
	public void nextPlayer() {
		activePlayer = (activePlayer + 1)%(players.length);
		generateTree();
	}

	public District getActiveDistrict() {
		return activeDistrict;
	}

	public void setActiveDistrict(District activeDistrict) {
		if(activeDistrict.getPlayer() == players[activePlayer]) {
			this.activeDistrict = activeDistrict;
		}
	}
	
	public Cell getCell(int i, int j) {
		return board[i][j];
	}
	
	/**
	 * Peremt de récupérer la positon d'une cellule
	 * @param c la cellule dont on souhaite connaître la position
	 * @return la position de cette cellule
	 * */
	private int[] getPosition(Cell c) {
		int[] position = new int[2];
		for(int i = 0; i<rows; i++) {
			for(int j = 0; j<columns; j++) {
				if(board[i][j] == c) {
					position[0] = i;
					position[1] = j;
				}
			}
		}
		return position;
	}
	
	/**
	 * Permet d'obtenir la cellule au dessus
	 * @param i la position en x
	 * @param j la position en y
	 * @return la cellule du dessus*/
	private Cell getTop(int i, int j) {
		if(i > 0) {
			return board[i-1][j];
		}
		return null;
	}
	
	/**
	 * Permet d'obtenir la cellule en dessous
	 * @param i la position en x
	 * @param j la position en y
	 * @return la cellule du dessous
	 * */
	private Cell getBottom(int i, int j) {
		if(i < rows-1) {
			return board[i+1][j];
		}
		return null;
	}
	
	/**
	 * Permet d'obtenir la cellule à droite
	 * @param i la position en x
	 * @param j la position en y
	 * @return la cellule à droite
	 * */
	private Cell getRight(int i, int j) {
		if(j < columns-1) {
			return board[i][j+1];
		}
		return null;
	}
	
	/**
	 * Permet d'obtenir la cellule à gauche
	 * @param i la position en x
	 * @param j la position en y
	 * @return la cellule à gauche*/
	private Cell getLeft(int i, int j) {
		if(j > 0) {
			return board[i][j-1];
		}
		return null;
	}
	
	/**
	 * Permet d'obtenir la cellule en haut à droite
	 * @param i la position en x
	 * @param j la position en y
	 * @return la cellule en haut à droite*/
	private Cell getTopRight(int i, int j) {
		if(i > 0 && j < columns-1) {
			return board[i-1][j+1];
		}
		return null;
	}
	
	/**
	 * Permet d'obtenir la cellule sitée en haut à gauche
	 * @param i la position en x
	 * @param j la position en y
	 * @return la cellule au dessus à gauche*/
	private Cell getTopLeft(int i, int j) {
		if(i > 0 && j > 0) {
			return board[i-1][j-1];
		}
		return null;
	}
	
	/**
	 * Permet d'obtenir la cellule en dessous à droite
	 * @param i la position en x
	 * @param j la position en y
	 * @return la cellule en dessous à droite*/
//	private Cell getBottomRight(int i, int j) {
//		if(i < rows-1 && j < columns-1) {
//			return board[i+1][j+1];
//		}
//		return null;
//	}
	/**
	 * Permet d'obtenir la cellule sitée en dessous à gauche
	 * @param i la position en x
	 * @param j la position en y
	 * @return la cellule en dessous à gauche
	 * */
//	private Cell getBottomLeft(int i, int j) {
//		if(i < rows-1 && j > 0) {
//			return board[i+1][j-1];
//		}
//		return null;
//	}

	public Cell getSelectedCell() {
		return selectedCell;
	}
	
	public void setSelectedCell(Cell selectedCell) {
		if(selectedCell.getDistrict().getPlayer() == players[activePlayer]) {
			this.selectedCell = selectedCell;
			setActiveDistrict(selectedCell.getDistrict());
		}
	}
	
	public Player getActivePlayer() {
		return players[activePlayer];
	}
	
	/**
	 * Vérifie si deux district doivent fusionner
	 * @param cell la cellule venant d'être ajoutée à un district pouvant provoquer une fusion
	 * */
	private void checkMerge(Cell cell) {
		ArrayList<Cell> cells = getAround(cell, true); //On considère que la cellule est un district pour obtenir toutes les cellules se trouvant au tour
		for(Cell c : cells) {
			if(c.getDistrict() != cell.getDistrict() && c.getDistrict() != null) {
				if(c.getDistrict().getPlayer() == cell.getDistrict().getPlayer()) {
					cell.getDistrict().addAllCell(c.getDistrict());
					for(Cell c1 : c.getDistrict().getCells()) {
						c1.setDistrict(cell.getDistrict());
					}
					c.getDistrict().remove();
				}
			}
		}
	}
	
	private void checkSplit() {
		// TO DO
	}
	
	/**
	 * Génère aléatoirement des arbres sur la carte
	 * */
	private void generateTree() {
		Random rand = new Random();
		int nTrees;
		for(int i=0; i<rows; i++) {
			for(int j = 0; j<columns; j++) {
				nTrees = 0;
				if(board[i][j].getItem() == null) {
					for(Cell c : getAround(board[i][j],true)) { // on considère que la cellule est un district pour obtenir toutes les cellules l'entourant
						if(c.getItem() instanceof Tree) {
							nTrees += 1;
						}
					}
					if(rand.nextInt(101) <= calculProb(nTrees)*100) {
						board[i][j].setItem(new Tree());
					}
				}
			}
		}
	}
	
	/**
	 * Calcule la probabilité qu'un abre apparaisse sur une cellule
	 * @param n le nombre d'arbres autour de la cellule
	 * @return la probabilité qu'un arbre apparaisse sur la cellule
	 * */
	private double calculProb(int n) {
		if(n >= 0 && n <= 6) { // le nombre d'arbres au tour de la case doit être compris entre 0 et 6
			return 1/100+(n*Math.log10(n+1))/10;
		}
		return 0;
	}
	
	/**
	 * Permet de créer une nouvelle capital sur un district
	 * @param district le district ayant besoin d'une nouvelle capitale
	 * */
	private void generateCapital(District district) {
		
	}
	
	public static void main(String[] args) {
		Player[] p = new Player[1];
		p[0] = new Player();
		Board board = new Board(5, 5, p, new NaturalDisastersController(), new Shop());
	}
}
