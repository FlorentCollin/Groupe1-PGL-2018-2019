package logic.board;

import logic.board.cell.Cell;
import logic.item.Capital;
import logic.item.Item;
import logic.item.Tree;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;
import logic.shop.Shop;

import java.util.ArrayList;
import java.util.Random;

public class Board{
	private Cell[][] board;
	private int columns, rows, activePlayer;
	private Player[] players;
	private ArrayList<District> districts;
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
	 * @parm cell la cellule sur laquelle placer le nouvel item
	 * */
	public void placeNewItem(Cell cell){
		// Pour placer une nouvel item il faut d'abord séléctionner une cellule du district depuis lequel on souhaite acheter
		// Ensuite il faut séléctionner la cellule sur laquelle on souhaite placer l'item
		if(selectedCell != null && shop.getSelectedItem() != null) {
			if(isInPossibleMove(possibleMove(selectedCell.getDistrict()), cell)) {
				if(cell.getItem() == null) { // Si cell ne contient aucun item on peut tjrs placer dessus
					conquerForNewItem(cell);
				}
				else if(isOnOwnTerritory(cell)) { // Si la cellule appartient déjà au joueur
					// Il faut que l'item soit de même instance que celui à ajouter
					// Mais aussi de même niveau et non maxé
					// Ainsi on peut améliorer
					if(cell.getItem().getMode().isImprovable() && isSameItem(cell, shop.getSelectedItem()) && cell.getItem().getLevel().isNotMax()) {
						fusionForNewItem(cell);
					}
				}
				// Le joueur souhaite se placer sur une case ennemie non vide
				// L'item de la case doit être de niveau inférieur ou égal à celui que l'on souhaite placer
				else {
					if(cell.getItem() instanceof Capital) {
						generateCapital(cell.getDistrict());
						conquerForNewItem(cell);
					}
					else if(cell.getItem().getLevel().compareTo(shop.getSelectedItem()) <= 0) {
						conquerForNewItem(cell);
					}
				}
			}
		}
	}
	
	private void conquerForNewItem(Cell cell) {
		cell.setDistrict(selectedCell.getDistrict());
		selectedCell.getDistrict().addCell(cell);
		updateCellForNewItem(cell);
		checkMerge(cell);
	}
	
	private void updateCellForNewItem(Cell cell) {
		cell.setItem(shop.getSelectedItem());
		shop.getSelectedItem().setHasMoved(true);
		shop.buy(cell.getDistrict());
		selectedCell = null;
	}
	
	/**
	 * Permet de déplacer un item d'une cellule à l'autre
	 * @param toCell la cellule de destination
	 * */
	public void move(Cell toCell) {
		if(selectedCell != null && selectedCell.getItem() != null && selectedCell.getItem().getMode().isMovable() && selectedCell.getItem().canMove()) {
			if(isInPossibleMove(possibleMove(selectedCell), toCell)) {
				if(toCell.getItem() == null) {
					conquer(toCell);
				}
				else if(isOnOwnTerritory(toCell)) {
					if(isSameItem(toCell, selectedCell.getItem())) {
						if(hasSameLevel(toCell, selectedCell.getItem()) && toCell.getItem().getLevel().isNotMax()) {
							fusion(toCell);
						}
					}
					else if(toCell.getItem() instanceof Tree) { // à modifier si on veut ajouter d'autre item procurant de l'argent
						updateCell(toCell);
						toCell.getDistrict().addGold(3);
					}
				}
				else {
					if(toCell.getItem() instanceof Capital) {
						generateCapital(toCell.getDistrict());
						conquer(toCell);
					}
					else if(toCell.getItem().getLevel().compareTo(selectedCell.getItem()) <= 0){
						conquer(toCell);
					}
				}
			}
		}
	}
	
	/**
	 * Permet de fusionner des items
	 * @param cell la cellule surlaquelle la fusion doit être faîte
	 * */
	private void fusion(Cell cell) {
		cell.getItem().improve();
		selectedCell.removeItem();
	}
	
	private void fusionForNewItem(Cell cell) {
		cell.getItem().improve();
		shop.buy(cell.getDistrict());
	}
	
	/**
	 * Permet de savoir si une cellule appartient à un joueur
	 * @param cell la cellule à tester
	 * @return true si la cellule appartient au joueuru qui est entrain de jouer le tour
	 * 			false sinon
	 * */
	private boolean isOnOwnTerritory(Cell cell) {
		if(isNeutral(cell)) {
			return false;
		}
		else if(cell.getDistrict().getPlayer() != getActivePlayer()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Permet de savoir si une cellule n'appartient à aucune joueur
	 * @param cell la cellule à tester
	 * @return true si la cellule n'appartient à personne
	 * 			false sinon
	 * */
	private boolean isNeutral(Cell cell) {
		if(cell.getDistrict() == null) {
			return true;
		}
		if(cell.getDistrict().getPlayer() == null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Permet de savoir si la cellule sur laquelle on souhaite se placer possède un item identique
	 * @param cell la cellule à tester
	 * @param isNewItem détermine si l'item qu'on souhaite ajouter sur la cellule provient du shop
	 * @return true si les items sont identiques
	 * 			false sinon
	 * */
	private boolean isSameItem(Cell cell, Item item) {
		if(cell.getItem().getClass().isInstance(item)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Permet de savoir si deux items ont le même niveau
	 * @param cell la cellule sur laquelle on souhaite se placer
	 * @param item l'item de la cellule de départ
	 * @return true si le niveau est identique
	 * 			false sinon*/
	private boolean hasSameLevel(Cell cell, Item item) {
		return cell.getItem().getLevel() == item.getLevel();
	}
	
	/**
	 * Permet de connaître les mouvements possibles d'un item
	 * @param cell la cellule où se trouve l'item
	 * @return les cellules sur lesquelles peut se déplacer l'item
	 * */
	public ArrayList<Cell> possibleMove(Cell cell) {
		ArrayList<Cell> possible = new ArrayList<Cell>();
		for(Cell c : getAround(cell)) {
			if(possible.indexOf(c) == -1) {
				if(canGoOn(c, cell.getItem())) {
					possible.add(c);
				}
			}
		}
		return possible;
	}
	
	/**
	 * Permet de connaître les position possibles pour placer un nouveau soldat
	 * @param district le district d'où le soldat a été acheté
	 * @return les cellules sur lesquelles peut être placé le nouvel item
	 * */
	public ArrayList<Cell> possibleMove(District district){
		ArrayList<Cell> possible = new ArrayList<Cell>();
		for(Cell c : district.getCells()) {
			if(possible.indexOf(c) == -1) {
				possible.add(c);
			}
			for(Cell c1 : getAround(c)) {
				if(canGoOn(c1, shop.getSelectedItem()) && possible.indexOf(c1) == -1) {
					possible.add(c1);
				}
			}
		}
		return possible;
	}
	
	/**
	 * Permet de récupérer les cellules autour d'une autre cellule sur lesquelles il est possible de placer un nouvel item
	 * @param cell la cellule de base
	 * @param item l'item que l'on souhaite placer
	 * @return la liste des cellules autour de cell pour lesquels c'est possible
	 * */
	private ArrayList<Cell> getAround(Cell cell){
		ArrayList<Cell> around = new ArrayList<Cell>();
		int x = getPosition(cell)[0];
		int y = getPosition(cell)[1];
		for(int i=x-1; i<x+2; i++) {
			for(int j=y-1; j<y+2; j++) {
				if(i>=0 && i<rows && j>=0 && j<columns) {
					if(board[i][j] != cell) { // On ne veut que les cellules autours
						if(i==x-1 && j != y) {
							if(y%2 == 0) {								
								around.add(board[i][j]);
							}
						}
						else if(i == x+1 && j != y) {
							if(y%2 != 0) {								
								around.add(board[i][j]);
							}
						}
						else {
							around.add(board[i][j]);
						}
					}
				}
			}
		}
		return around;
	}
	
	/**
	 * Permet de vérifier s'il est possible de placer un item acheté sur une certaine case
	 * @param cell la cellule à tester
	 * @param item l'item que l'on souhaite y placer
	 * @return true si c'est possible
	 * 			false sinon
	 * */
	private boolean canGoOn(Cell cell, Item item) {
		if(cell.getItem() == null) {
			return true;
		}
		else if(isOnOwnTerritory(cell)) {
			if(isSameItem(cell, item) && item.getMode().isImprovable() && item.getLevel().isNotMax()) {
				return true;
			}
		}
		else {
			if(!cell.getItem().getMode().isImprovable()) {
				return true;
			}
			else if(cell.getItem().getLevel().compareTo(item) <= 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 *Permet de vérifier qu'une cellule est un choix possible
	 *@param possibleMoves les déplacements possibles
	 *@param cell la cellule où souhaite se placer le joueur
	 *@return true si cell fait partie de possibleMove
	 *sinon false
	 **/
	private boolean isInPossibleMove(ArrayList<Cell> possibleMoves, Cell cell) {
		return possibleMoves.indexOf(cell) > -1;
	}
	
	/**
	 * Permet de passer au joueur suivant
	 */
	public void nextPlayer() {
		activePlayer = (activePlayer + 1)%(players.length);
		generateTree();
		for(District district : districts) {
			if(district.getPlayer() == getActivePlayer()) {
				district.calculateGold();
				if(district.getGold() < 0) {
					district.remove();
				}
				else {
					for(Cell c : district.getCells()) {
						if(c.getItem() != null && c.getItem().getMode().isMovable()) {
							c.getItem().setHasMoved(false);
						}
					}
				}
			}
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
	public int[] getPosition(Cell c) {
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

	public Cell getSelectedCell() {
		return selectedCell;
	}
	
	public void setSelectedCell(Cell selectedCell) {
		if(selectedCell.getDistrict().getPlayer() == getActivePlayer()) {
			this.selectedCell = selectedCell;
		}
	}
	
	private Player getActivePlayer() {
		return players[activePlayer];
	}
	
	/**
	 * Vérifie si deux district doivent fusionner
	 * @param cell la cellule venant d'être ajoutée à un district pouvant provoquer une fusion
	 * */
	private void checkMerge(Cell cell) {
		ArrayList<Cell> cells = getAround(cell); //On considère que la cellule est un district pour obtenir toutes les cellules se trouvant au tour
		for(Cell c : cells) {
			if(c.getDistrict() != cell.getDistrict() && c.getDistrict() != null) {
				if(c.getDistrict().getPlayer() == cell.getDistrict().getPlayer()) {
					cell.getDistrict().addAllCell(c.getDistrict());
					for(Cell c1 : c.getDistrict().getCells()) {
						c1.setDistrict(cell.getDistrict());
					}
					districts.remove(c.getDistrict());
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
					for(Cell c : getAround(board[i][j])) { // on considère que la cellule est un district pour obtenir toutes les cellules l'entourant
						if(c.getItem() instanceof Tree) {
							nTrees += 1;
						}
					}
					if(rand.nextInt(101) <= calculateProb(nTrees)*100) {
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
	private double calculateProb(int n) {
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
		Random rand = new Random();
		district.getCells().get(rand.nextInt(district.getCells().size())).setItem(new Capital());
	}
	
	/**
	 * Gère le placement d'un item sur les cellules
	 * @param la cell à mettre à jour
	 * */
	// revoir la documentation
	private void updateCell(Cell cell) {
		selectedCell.getItem().setHasMoved(true);
		cell.setItem(selectedCell.getItem());
		selectedCell.removeItem();
		selectedCell = null; //peut être faire une méthode forgotSelection ?
	}
	
	/**
	 * Gère la conquête d'une nouvelle cellule
	 * @param cell la cellule conquise
	 * */
	private void conquer(Cell cell) {
		cell.setDistrict(selectedCell.getDistrict());
		selectedCell.getDistrict().addCell(cell);
		updateCell(cell);
		checkMerge(cell);
	}
	
	public void addDistrict(District district) {
		districts.add(district);
	}

	public ArrayList<District> getDistricts() {
		return districts;
	}
}
