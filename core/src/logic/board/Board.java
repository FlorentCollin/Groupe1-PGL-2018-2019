package logic.board;

import java.util.ArrayList;
import java.util.Random;

import logic.board.cell.Cell;
import logic.item.Capital;
import logic.item.Item;
import logic.item.Tree;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;
import logic.shop.Shop;

public class Board{
	private Cell[][] board;
	private int columns, rows, activePlayer;
	private Player[] players;
	private ArrayList<District> districts;
	private Shop shop;
	private NaturalDisastersController naturalDisastersController;
	private Cell selectedCell;
	private final int PROBA;
	//Variable qui est utilisé dans la méthode getNeighbors
	//Elle contient toutes les directions possibles pour les cellules adjacentes
	private final int[][][] directions = {
			{{+1,  0}, {+1, -1}, { 0, -1}, {-1, -1}, {-1,  0}, { 0, +1}}, //Colonne Pair
			{{+1, +1}, {+1,  0}, { 0, -1}, {-1,  0}, {-1, +1}, { 0, +1}}}; //Colonne Impair

	public Board(int columns, int rows, Player[] players,NaturalDisastersController naturalDisastersController, Shop shop){
		this.columns = columns;
		this.rows = rows;
		board = new Cell[columns][rows];
		this.players = players;
		this.naturalDisastersController = naturalDisastersController;
		this.districts = new ArrayList<District>();
		this.shop = shop;
		fullIn();
		activePlayer = 0;
		PROBA = 100;
	}
	
	private void fullIn() {
		for(int i = 0; i<columns; i++) {
			for(int j = 0; j<rows; j++) {
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
					} else if(toCell.getItem() instanceof Tree || toCell.getItem().getLevel().compareTo(selectedCell.getItem()) <= 0){
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
		for(Cell c : getNeighbors(cell)) {
			if(possible.indexOf(c) == -1) {
				if(canGoOn(c, cell.getItem())) {
					possible.add(c);
				}
			}
		}
//		for(int i = 0; i<4; i++) { // Car un soldat peut se d�placer de max 4cases et la premi�re ligne permet d�j� le d�placement de 1 case
//			for(Cell c : possible) {
//				if(!(c.getItem() instanceof Tree)) {
//					for(Cell c : getNeighbors(c)) {
//						if(possible.getIndex(c) == -1) {
//							if(canGoOn(c, cell.getItem())) {
//								possible.add(c);
//							}
//						}
//					}
//				}
//			}
//		}
		return possible;
	}
	
	/**
	 * Permet de conna�tre la distance s�parant deux cellules
	 * @param from la cellule de d�part
	 * @param to la cellule de destination
	 * @return la distance entre from et to
	 * */
	private int getDistance(Cell from, Cell to) {
		int[] fromPosition = getPosition(from);
		int[] toPosition = getPosition(to);
		return 0;
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
			for(Cell c1 : getNeighbors(c)) {
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
	private ArrayList<Cell> getNeighbors(Cell cell){
		ArrayList<Cell> around = new ArrayList<Cell>();
		int x = getPosition(cell)[0];
		int y = getPosition(cell)[1];
		int parity = x & 1;
		for (int direction = 0; direction < 6; direction++) { //6 car un  hexagone possède 6 voisins
			int[] dir = directions[parity][direction];
			int neighborX = x + dir[0];
			int neighborY = y + dir[1];
			if(neighborX>=0 && neighborX<columns && neighborY>=0 && neighborY<rows) {
				around.add(board[neighborX][neighborY]);
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
			} else if(cell.getItem() instanceof Tree) {
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
		for(int i = 0; i<columns; i++) {
			for(int j = 0; j<rows; j++) {
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

	public void resetSelectedCell() {
		this.selectedCell = null;
	}
	
	public Player getActivePlayer() {
		return players[activePlayer];
	}
	
	/**
	 * Vérifie si deux district doivent fusionner
	 * @param cell la cellule venant d'être ajoutée à un district pouvant provoquer une fusion
	 * */
	public void checkMerge(Cell cell) {
		ArrayList<Cell> cells = getNeighbors(cell); //On considère que la cellule est un district pour obtenir toutes les cellules se trouvant au tour
		for(Cell c : cells) {
			if(c.getDistrict() != cell.getDistrict() && c.getDistrict() != null) {
				if(c.getDistrict().getPlayer() == cell.getDistrict().getPlayer()) {
					if(cell.getDistrict().getCells().size() >= c.getDistrict().getCells().size()) {						
						merge(cell.getDistrict(), c.getDistrict());
					}
					else {
						merge(c.getDistrict(), cell.getDistrict());
					}
				}
			}
		}
	}
	
	private void merge(District greather, District smaller) {
		greather.addGold(smaller.getGold());
		smaller.removeCapital();
		greather.addAllCell(smaller);
		for(Cell c : smaller.getCells()) {
			c.setDistrict(greather);
		}
		districts.remove(smaller);
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
		for(int i=0; i<columns; i++) {
			for(int j = 0; j<rows; j++) {
				nTrees = 0;
				if(board[i][j].getItem() == null) {
					for(Cell c : getNeighbors(board[i][j])) { // on considère que la cellule est un district pour obtenir toutes les cellules l'entourant
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
		//On r�cup�re une cellule du district al�atoirement
		Cell cell = district.getCells().get(rand.nextInt(district.getCells().size()));
		district.addCapital(cell);
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
		if(cell.getDistrict() != null) {
			cell.getDistrict().removeCell(cell);
		}
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

	public Cell[][] getBoard() {
		return board;
	}

	public int getColumns() {
		return columns;
	}

	public int getRows() {
		return rows;
	}

	public Player[] getPlayers() {
		return players;
	}

	public Shop getShop() {
		return shop;
	}
	
	public void play(Cell cell) {
		if(selectedCell != null) {
			if(shop.getSelectedItem() != null) {
				placeNewItem(cell);
			}
			else {
				move(cell);
			}
			selectedCell = null;
		}
		else if(cell.getDistrict() != null &&cell.getDistrict().getPlayer() == players[activePlayer] && cell.getItem() != null && cell.getItem().getMode().isMovable()){
			if(cell.getItem().canMove()) {				
				selectedCell = cell;
			}
		}
	}
}
