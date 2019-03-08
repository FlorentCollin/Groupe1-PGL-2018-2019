package logic.board;

import java.util.ArrayList;
import java.util.Random;

import logic.board.cell.Cell;
import logic.board.cell.LandCell;
import logic.board.cell.WaterCell;
import logic.item.DestroyableItem;
import logic.item.Item;
import logic.item.Tree;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;
import logic.player.ai.AI;
import logic.player.ai.strategy.Strategy;
import logic.shop.Shop;



public class Board{
	private Cell[][] board;
	private int columns, rows, activePlayer;
	private ArrayList<Player> players;
	private volatile ArrayList<District> districts;
	private Shop shop;
	private NaturalDisastersController naturalDisastersController;
	private volatile Cell selectedCell, firstCell;
	private final int PROBA = 1; //plus PROBA augmente plus la génération d'arbre est lente et inversement (base : PROBA = 1)
	private ArrayList<Cell> visited = new ArrayList<>(); // Eviter de boucler indéfiniment pour numberOfWayToCapital
    private boolean hasChanged;
	private Player winner;
	//Variable qui est utilisé dans la méthode getNeighbors
	//Elle contient toutes les directions possibles pour les cellules adjacentes
	private final int[][][] directions = {
			{{+1,  0}, {+1, -1}, { 0, -1}, {-1, -1}, {-1,  0}, { 0, +1}}, //Colonne Pair
			{{+1, +1}, {+1,  0}, { 0, -1}, {-1,  0}, {-1, +1}, { 0, +1}}}; //Colonne Impair
	private ArrayList<Cell> neutralCells;

	public Board(int columns, int rows, ArrayList<Player> players,NaturalDisastersController naturalDisastersController, Shop shop){
		this.columns = columns;
		this.rows = rows;
		board = new Cell[columns][rows];
		this.players = players;
		this.naturalDisastersController = naturalDisastersController;
		this.districts = new ArrayList<>();
		this.shop = shop;
		fullIn();
		neutralCells = new ArrayList<>();
	}
	
	public Board(int columns, int rows, ArrayList<Player> players, Shop shop) {
		this.columns = columns;
		this.rows = rows;
		board = new Cell[columns][rows];
		this.players = players;
		this.districts = new ArrayList<>();
		this.shop = shop;
		fullIn();
		neutralCells = new ArrayList<>();
	}
	
	/**
	 * Remplit le tableau de jeu
	 * */
	private void fullIn() {
		for(int i = 0; i<columns; i++) {
			for(int j = 0; j<rows; j++) {
				board[i][j] = new LandCell(i,j);
			}
		}
	}
	
	public void changeToAI(int nPlayer, Strategy strategy) {
		AI ai = new AI(strategy, this);
		for(District district : districts) {
			if(district.getPlayer() == players.get(nPlayer)) {
				ai.addDistrict(district);
				district.setPlayer(ai);
			}
		}
		players.set(nPlayer, ai);
	}
	
	/**
	 * Permet de placer de l'eau sur la carte
	 * @param i la position en x
	 * @param j la position en y
	 * */
	public void changeToWaterCell(int i, int j) {
		board[i][j] = new WaterCell(i,j);
	}

	public void setShopItem(Item item) {
		if(selectedCell != null) {
			shop.setSelectedItem(item, selectedCell.getDistrict());
		}
	}
	
	/**
	 * Permet de placer un item sur une cellule du plateau
	 * @parm cell la cellule sur laquelle placer le nouvel item
	 * */
	public void placeNewItem(Cell cell){
		//TO REDO !!!!!!!!!!!!
		if(isInPossibleMove(possibleMove(selectedCell.getDistrict()), cell)) {
			if(cell.getItem() == null) { // Si cell ne contient aucun item on peut toujours se placer dessus
				conquerForNewItem(cell);
			}
			if(cell.getItem() instanceof Tree) {
				conquerForNewItem(cell);
				cell.getDistrict().addGold(3);
			}
			else if(isOnOwnTerritory(cell)) { // Si la cellule appartient déjà au joueur
				// Il faut que l'item soit de même instance que celui à ajouter
				// Mais aussi de même niveau et non maxé
				// Ainsi on peut améliorer
				if(cell.getItem().isImprovable() && sameInstance(cell.getItem(), shop.getSelectedItem()) && cell.getItem().getLevel().isNotMax()) {
					fusionForNewItem(cell);
				}
			}
			// Le joueur souhaite se placer sur une case ennemie non vide
			// L'item de la case doit être de niveau inférieur ou égal à celui que l'on souhaite placer
			else {
				// Si la cellule enemi contient une capitale il faut regénérer une capitale pour le district de cette cellule
				if(cell.getItem().getLevel() == null) {
					conquerForNewItem(cell);
				}
				// La cellule contient un arbre ou l'item est de niveau inférieur à celui que va être placé
				else if(cell.getItem().getLevel().compareTo(shop.getSelectedItem()) <= 0) {
					conquerForNewItem(cell);
				}
			}
		}
	}
	
	/**
	 * Permet de conquérir une cellule lors de l'ajout d'un nouvel item
	 * @param cell la cellule à conquérir
	 * */	
	private void conquerForNewItem(Cell cell) {
		if(cell.getDistrict() != null) {
			cell.getDistrict().removeCell(cell);
		}
		selectedCell.getDistrict().addCell(cell); //On ajoute la cellule conquise au district présélectionné
		updateCellForNewItem(cell); 
		checkMerge(cell);
		checkSplit(cell);
	}
	
	/**
	 * Met à jour une cellule lors du placement d'un nouvel item
	 * @param cell la cellule à mettre à jour
	 * */
	private void updateCellForNewItem(Cell cell) {
		cell.setItem(shop.getSelectedItem());
		shop.getSelectedItem().setHasMoved(true);
		shop.buy(cell.getDistrict());
	}
	
	/**
	 * Permet de déplacer un item d'une cellule à l'autre
	 * @param toCell la cellule de destination
	 * */
	public void move(Cell toCell) {
		//TO REDO
		if(selectedCell.getItem().isMovable() && selectedCell.getItem().canMove()) {//Il faut vérifier que l'item de la cellule est déplaçable et qu'il peut encore être déplacé
			if(isInPossibleMove(possibleMove(selectedCell), toCell)) {
				if(toCell.getItem() == null) {
					conquer(toCell);
				}
				else if(isOnOwnTerritory(toCell)) {
					if(sameInstance(toCell.getItem(), selectedCell.getItem())) {
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
					if(toCell.getItem().getLevel() == null) {
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
	
	/**
	 * Permet de fusinonner des items lors de l'ajout d'un nouvel item
	 * @param cell la cellule sur laquelle se trouve un item et qu'un nouvel item vient d'être placé
	 * */
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
		return cell.getDistrict().getPlayer() == null;
	}
	
	/**
	 * Permet de savoir si la cellule sur laquelle on souhaite se placer possède un item identique
	 * @param cell la cellule à tester
	 * @param isNewItem détermine si l'item qu'on souhaite ajouter sur la cellule provient du shop
	 * @return true si les items sont identiques
	 * 			false sinon
	 * */
	private boolean sameInstance(Item item1, Item item2) {
		return item1.getClass().isInstance(item2);
	}
	
	/**
	 * Permet de savoir si deux items ont le même niveau
	 * @param cell la cellule sur laquelle on souhaite se placer
	 * @param item l'item de la cellule de départ
	 * @return true si le niveau est identique
	 * 			false sinon
	 * */
	private boolean hasSameLevel(Cell cell, Item item) {
		return cell.getItem().getLevel() == item.getLevel();
	}
	
	/**
	 * Permet de connaître les mouvements possibles d'un item
	 * @param cell la cellule où se trouve l'item
	 * @return les cellules sur lesquelles peut se déplacer l'item
	 * */
	public ArrayList<Cell> possibleMove(Cell cell) {
		ArrayList<Cell> possible = new ArrayList<>();
		ArrayList<Cell> around = getNeighbors(cell);
		ArrayList<Cell> subAround = new ArrayList<>();
		if(cell.getItem() != null && cell.getItem().isMovable() && cell.getItem().canMove()) {
			for (int i = 0; i < cell.getItem().getMaxMove() - 1; i++) {
				subAround.clear();
				for (Cell c : around) {
					if (c.getDistrict() == cell.getDistrict() && (c.getItem() == null
							|| (c.getItem().getClass().isInstance(cell.getClass())
							&& c.getItem().getLevel() == cell.getItem().getLevel()))) {
						subAround.addAll(getNeighbors(c));
					}
				}
				if(cell.getItem() == null) {
				    break;
				}
				around.addAll(subAround);
			}
			for (Cell c : around) {
				if (c != cell && possible.indexOf(c) == -1) {
					if (canGoOn(c, cell.getItem())) {
						possible.add(c);
					}
				}
			}
		} 
		else {
		    possible.add(cell);
        }
		return possible;
	}
	
	/**
	 * Permet de connaître les position possibles pour placer un nouveau soldat
	 * @param district le district d'où le soldat a été acheté
	 * @return les cellules sur lesquelles peut être placé le nouvel item
	 * */
	public ArrayList<Cell> possibleMove(District district){
		ArrayList<Cell> possible = new ArrayList<>();
		synchronized (district.getCells()) {
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
	}
	
	/**
	 * Permet de récupérer les cellules autour d'une autre cellule sur lesquelles il est possible de placer un nouvel item
	 * @param cell la cellule de base
	 * @param item l'item que l'on souhaite placer
	 * @return la liste des cellules autour de cell pour lesquels c'est possible
	 * */
	public ArrayList<Cell> getNeighbors(Cell cell){
		ArrayList<Cell> around = new ArrayList<>();
		int x = cell.getX();
		int y = cell.getY();
		int parity = x & 1;
		for (int direction = 0; direction < 6; direction++) { //6 car un  hexagone possède 6 voisins
			int[] dir = directions[parity][direction];
			int neighborX = x + dir[0];
			int neighborY = y + dir[1];
			//On vérifie que le voisin est bien dans les limites de la map
			if(neighborX>=0 && neighborX<columns && neighborY>=0 && neighborY<rows) {
				if(board[neighborX][neighborY].isAccessible()) {
					around.add(board[neighborX][neighborY]);
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
		Item cellItem = cell.getItem();
		// Si il n'y a aucun item il est toujours possible de se placer sur la case
		if(cellItem == null) {
			return true;
		}
		// Cas où la cellule nous appartient déjà
		else if(isOnOwnTerritory(cell)) {
			if(sameInstance(cellItem, item) && item.isImprovable()) { // Cas où le joueur peut améliorer l'item
				return true;
			} else if(cellItem instanceof DestroyableItem) { // L'item est destructible par le joueur sur son propre territoire (ex : Tree)
				return true;
			}
		}
		// La cellule appartient à un autre joueur
		else {
			if(!cellItem.isImprovable()) { // Item quelconque
				return true;
			}
			else if(item.isStronger(cellItem)) { // Item de la cellule de plus faible niveau
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
	    hasChanged = true;
	    checkDistricts(); // Peut être à supprimer d'ici
		checkWinner();
		selectedCell = null;
		shop.removeSelection();
		if(winner == null) {
			activePlayer = (activePlayer + 1)%(players.size());
			generateTree();
			for(District district : districts) {
				if(district.getPlayer() == getActivePlayer()) {
					district.calculateGold();
					if(district.getGold() < 0) {
						district.removeSoldiers();
					}
					else {
						district.refreshSoldiers();
					}
				}
			}
			if(players.get(activePlayer) instanceof AI) {
				((AI)players.get(activePlayer)).play(); //Vérifier coût d'un cast !!!!
			}
		}
		else {
			//TO DO
			System.out.println(winner+" wins");
		}
	}
	
	public Cell getCell(int i, int j) {
		return board[i][j];
	}

	public Cell getSelectedCell() {
		return selectedCell;
	}

	public void setSelectedCell(Cell selectedCell) {
	    if(selectedCell == null || (selectedCell.getDistrict() != null && selectedCell.getDistrict().getPlayer() == getActivePlayer())) {
			this.selectedCell = selectedCell;
		}
	    hasChanged = true;
	}
	
	public Player getActivePlayer() {
		return players.get(activePlayer);
	}
	
	/**
	 * Vérifie si deux district doivent fusionner
	 * @param cell la cellule venant d'être ajoutée à un district pouvant provoquer une fusion
	 * */
	public void checkMerge(Cell cell) {
		for(Cell c : getNeighbors(cell)) {
			if(c.getDistrict() != cell.getDistrict() && c.getDistrict() != null) {
				if(c.getDistrict().getPlayer() == cell.getDistrict().getPlayer()) {
					if(cell.getDistrict().size() >= c.getDistrict().size()) {						
						merge(cell.getDistrict(), c.getDistrict());
					}
					else {
						merge(c.getDistrict(), cell.getDistrict());
					}
				}
			}
		}
	}
	
	/**
	 * Permet de fusionner deux districts
	 * @param bigger le district le plus grand
	 * @param smaller le district le plus petit
	 * */
	private void merge(District bigger, District smaller) {
		bigger.addGold(smaller.getGold());
		smaller.removeCapital();
		bigger.addAll(smaller);
		removeDistrict(smaller);
		checkDistricts();
	}
	
	/**
	 * Permet de diviser un district en deux sous districts
	 * @param district le district à diviser
	 * */
	private void split(District district) {
		District newDistrict = new District(district.getPlayer());
		for(Cell c : district.getCells()) {
			visited.clear();
			firstCell = c;
			if(numberOfWayToCapital(c)==0) {
				newDistrict.addCell(c);
			}
		}
		district.removeAll(newDistrict);
		addDistrict(newDistrict);
		checkDistricts();
	}
	
	/**
	 * Permet de vérifier si il faut diviser un district
	 * @param cell la cellule depuis laquelle on effectue la vérification
	 * */
	private void checkSplit(Cell cell) {
		for(Cell c : getNeighbors(cell)) {
			if(c.getDistrict() != null && c.getDistrict().getPlayer() != getActivePlayer()) {
				visited.clear();
				firstCell = c;
				if(numberOfWayToCapital(c) == 0) {
					split(c.getDistrict());
				}
			}
		}
	}
	
	/**
	 * Permet de connaître le nombre de chemin menant une cellule jusqu'à la capital de son district
	 * @param cell la cellule qui est testée
	 * @return le nombre de chemin menant jusqu'à la capital
	 * */
	private int numberOfWayToCapital(Cell cell) {
		if(visited.indexOf(cell) == -1) {
			visited.add(cell);
			if(cell == firstCell.getDistrict().getCapital()) {
				return 1;
			}
			if(cell.getDistrict() != firstCell.getDistrict()) {
				return 0;
			}
			else {
				int value = 0;
				for(Cell c : getNeighbors(cell)) {
					value += numberOfWayToCapital(c);
				}
				return value;
			}
		}
		return 0;
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
				if(board[i][j].getItem() == null && board[i][j].isAccessible()) {
					for(Cell c : getNeighbors(board[i][j])) { 
						if(c.getItem() instanceof Tree) {
							nTrees += 1;
						}
					}
					if(rand.nextInt(100)*PROBA < calculateProb(nTrees)*100) {
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
		ArrayList<Cell> visited = new ArrayList<>();
		Random rand = new Random();
		//On récupère une cellule du district aléatoirement
        synchronized (district.getCells()) { // what?
            int i = rand.nextInt(district.getCells().size());
            while(district.getCells().get(i).getItem() != null && visited.size() < district.getCells().size()) {
                visited.add(district.getCells().get(i));
                i = rand.nextInt(district.getCells().size());
            }
            if(visited.size() == district.getCells().size()) {
                districts.remove(district);
                removeDistrict(district);
            }
            else {
	            Cell cell = district.getCells().get(i);
	            district.addCapital(cell);
            }
        }
	}
	
	private void removeDistrict(District district) {
		districts.remove(district);
		if(district.getPlayer() instanceof AI) {
			((AI)district.getPlayer()).removeDistrict(district);
		}
	}
	
	/**
	 * Gère le placement d'un item sur les cellules
	 * @param cell cell à mettre à jour
	 * */
	// revoir la documentation
	private void updateCell(Cell cell) {
		selectedCell.getItem().setHasMoved(true);
		cell.setItem(selectedCell.getItem());
		selectedCell.removeItem();
		setSelectedCell(null);
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
		checkSplit(cell);
	}
	
	public void addDistrict(District district) {
		districts.add(district);
		if(district.getPlayer() instanceof AI) {
			((AI)district.getPlayer()).addDistrict(district);
		}
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

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public Shop getShop() {
		return shop;
	}
	
	/**
	 * Permet de jouer un tour
	 * @param cell la cellule sur laquelle une action est susceptible d'être effectuée
	 * */
	public void play(Cell cell) {
		if(selectedCell != null) {
			if(shop.getSelectedItem() != null) {
				placeNewItem(cell);
			}
			else if(selectedCell.getItem() != null && selectedCell.getItem().isMovable() && selectedCell.getItem().canMove()) {
				move(cell);
			}
			selectedCell = null;
			shop.removeSelection();
		}
		// cell != null vrmt utile ????
		else if(cell != null && cell.getDistrict() != null && cell.getDistrict().getPlayer() == players.get(activePlayer)){
			selectedCell = cell;
		}
	}
	
	private void checkWinner() {
		ArrayList<Player> deadPlayers = new ArrayList<>();
		for(Player player : players) {
			if(!canYetPlay(player)) {
				deadPlayers.add(player);
			}
		}
		players.removeAll(deadPlayers);
		if(players.size() == 1) {
			winner = players.get(0);
		}
	}
	
	private boolean canYetPlay(Player player) {
		for(District district : districts) {
			if(district.getPlayer() == player) {
				return true;
			}
		}
		return false;
	}
	
	private void checkDistricts() {
		neutralCells.clear();
		ArrayList<District> emptyDistricts = new ArrayList<>();
		for(District district : districts) {
			if(district.getCells().size() <= 1) {
				emptyDistricts.add(district);
				for(Cell c : district.getCells()) {
					c.removeDistrict();
					c.removeItem();
					neutralCells.add(c);
				}
			}
		}
		for(District district : emptyDistricts) {
			removeDistrict(district);
		}
		checkCapitals();
	}
	
	public void checkCapitals() {
		for(District district : districts) {
			if(district.getCapital() == null) {
				generateCapital(district);
			}
		}
	}

	public int getActivePlayerNumber() {
	    return activePlayer;
    }

    public boolean hasChanged() {
	    return hasChanged;
    }

    public void updateBoard(ArrayList<District> districts, ArrayList<Player> players, int activePlayer) {
        this.districts = districts;
        this.players = players;
        this.activePlayer = activePlayer;
        for (District district : districts) {
            for (Cell cell : district.getCells()) {
                board[cell.getX()][cell.getY()] = cell;
                cell.setDistrict(district);
            }
            for(int i = 0; i < players.size(); i++) {
                if(district.getPlayer().getId() == players.get(i).getId()) {
                   	players.set(i, district.getPlayer());
                }
            }
        }
    }
    
    public ArrayList<Cell> getNeutralCells(){
    	return neutralCells;
    }
}
