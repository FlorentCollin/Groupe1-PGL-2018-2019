package logic.board;

import java.util.ArrayList;
import java.util.Random;

import logic.board.cell.Cell;
import logic.board.cell.LandCell;
import logic.board.cell.WaterCell;
import logic.item.Capital;
import logic.item.Item;
import logic.item.Tomb;
import logic.item.Tree;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;
import logic.player.ai.AI;
import logic.player.ai.Strategy;
import logic.shop.Shop;
import memory.Memory;



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
	private ArrayList<ArrayList<Memory>> memories;

	public Board(int columns, int rows, ArrayList<Player> players,NaturalDisastersController naturalDisastersController, Shop shop){
		this.columns = columns;
		this.rows = rows;
		board = new Cell[columns][rows];
		this.players = players;
		this.naturalDisastersController = naturalDisastersController;
		this.districts = new ArrayList<>();
		this.shop = shop;
		memories = new ArrayList<>();
		memories.add(new ArrayList<>());
		fullIn();
		for(District district : districts) {
			generateCapital(district);			
		}
	}
	
	public Board(int columns, int rows, ArrayList<Player> players, Shop shop) {
		this.columns = columns;
		this.rows = rows;
		board = new Cell[columns][rows];
		this.players = players;
		this.districts = new ArrayList<>();
		this.shop = shop;
		memories = new ArrayList<>();
		memories.add(new ArrayList<>());
		fullIn();
		for(District district : districts) {
			generateCapital(district);			
		}
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
		// Pour placer une nouvel item il faut d'abord séléctionner une cellule du district depuis lequel on souhaite acheter
		// Ensuite il faut séléctionner la cellule sur laquelle on souhaite placer l'item
//		if(selectedCell != null && shop.getSelectedItem() != null) {
			if(isInPossibleMove(possibleMove(selectedCell.getDistrict()), cell)) {
				if(cell.getItem() == null) { // Si cell ne contient aucun item on peut toujours se placer dessus
					conquerForNewItem(cell);
				}
				if(cell.getItem() instanceof Tree) {
					conquerForNewItem(cell);
				}
				else if(isOnOwnTerritory(cell)) { // Si la cellule appartient déjà au joueur
					// Il faut que l'item soit de même instance que celui à ajouter
					// Mais aussi de même niveau et non maxé
					// Ainsi on peut améliorer
					if(cell.getItem().isImprovable() && isSameItem(cell, shop.getSelectedItem()) && cell.getItem().getLevel().isNotMax()) {
						fusionForNewItem(cell);
					}
				}
				// Le joueur souhaite se placer sur une case ennemie non vide
				// L'item de la case doit être de niveau inférieur ou égal à celui que l'on souhaite placer
				else {
					// Si la cellule enemi contient une capitale il faut regénérer une capitale pour le district de cette cellule
					if(cell.getItem() instanceof Capital) {
						if(cell.getDistrict().getCells().size() > 1) {
							generateCapital(cell.getDistrict());
						}
						conquerForNewItem(cell);
					}
					// La cellule contient un arbre ou l'item est de niveau inférieur à celui que va être placé
					else if(cell.getItem() instanceof Tree || cell.getItem() instanceof Tomb || cell.getItem().getLevel().compareTo(shop.getSelectedItem()) <= 0) {
						conquerForNewItem(cell);
					}
				}
			}
//		}
	}
	
	/**
	 * Permet de conquérir une cellule lors de l'ajout d'un nouvel item
	 * @param cell la cellule à conquérir
	 * */	
	private void conquerForNewItem(Cell cell) {
		memories.get(memories.size()-1).add(new Memory(cell, cell.getItem(), cell.getDistrict(), null));
		if(cell.getDistrict() != null) {
			cell.getDistrict().removeCell(cell);
		}
		cell.setDistrict(selectedCell.getDistrict());
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
		setSelectedCell(null);
	}
	
	/**
	 * Permet de déplacer un item d'une cellule à l'autre
	 * @param toCell la cellule de destination
	 * */
	public void move(Cell toCell) {
//		if(selectedCell != null && selectedCell.getItem() != null && selectedCell.getItem().getMode().isMovable() && selectedCell.getItem().canMove()) {
		if(selectedCell.getItem().isMovable() && selectedCell.getItem().canMove()) {//Il faut vérifier que l'item de la cellule est déplaçable et qu'il peut encore être déplacé
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
						if(toCell.getDistrict().getCells().size() > 1) {							
							generateCapital(toCell.getDistrict());
						}
						conquer(toCell);
					} else if(toCell.getItem() instanceof Tree || toCell.getItem() instanceof Tomb || (toCell.getItem().getLevel() != null && toCell.getItem().getLevel().compareTo(selectedCell.getItem()) <= 0)){
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
	private boolean isSameItem(Cell cell, Item item) {
		return cell.getItem().getClass().isInstance(item);
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
				if(cell.getItem() == null)
				    break;
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
		if(cell.getItem() == null) {
			return true;
		}
		else if(isOnOwnTerritory(cell)) {
			if(isSameItem(cell, item) && item.isImprovable() && item.getLevel().isNotMax()) {
				return true;
			} else if(cell.getItem() instanceof Tree) {
				return true;
			}
		}
		else {
			if(!cell.getItem().isImprovable()) {
				return true;
			}
			else if(item != null && cell.getItem().getLevel().compareTo(item) <= 0) {
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
		checkDistricts();
		checkWinner();
		selectedCell = null;
		shop.removeSelection();
		if(winner == null) {
			memories.add(new ArrayList<>());
			activePlayer = (activePlayer + 1)%(players.size());
			generateTree();
			for(District district : districts) {
				if(district.getPlayer() == getActivePlayer()) {
					district.calculateGold();
					if(district.getGold() < 0) {
						district.remove();
					}
					else {
						for(Cell c : district.getCells()) {
							if(c.getItem() != null && c.getItem().isMovable()) {
								c.getItem().setHasMoved(false);
							}
						}
					}
				}
			}
			if(players.get(activePlayer) instanceof AI) {
				((AI)players.get(activePlayer)).play(); //Vérifier coût d'un cast !!!!
			}
		}
		else {
			System.out.println(winner+" wins");
		}
	}
	
	public Cell getCell(int i, int j) {
		return board[i][j];
	}

	//TODO Supprimer cette méthode si elle n'est plus utilisé
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
	    if(selectedCell == null) {
	        this.selectedCell = null;
        } else if(selectedCell.getDistrict().getPlayer() == getActivePlayer()) {
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
	
	/**
	 * Permet de fusionner deux districts
	 * @param bigger le district le plus grand
	 * @param smaller le district le plus petit
	 * */
	private void merge(District bigger, District smaller) {
		bigger.addGold(smaller.getGold());
		smaller.removeCapital();
		for(Cell c : smaller.getCells()) {
			bigger.addCell(c);
			c.setDistrict(bigger);
		}
		if(smaller.getPlayer() instanceof AI) {
			((AI) smaller.getPlayer()).removeDistrict(smaller);
		}
		districts.remove(smaller);
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
				c.setDistrict(newDistrict);
			}
		}
		for(Cell c : newDistrict.getCells()) {
			district.removeCell(c);
		}
		if(newDistrict.getCells().size() <= 1) {
			for(Cell c : newDistrict.getCells()) {
				c.setDistrict(null);
				c.removeItem();
			}
		}
		if(district.getCells().size() <= 1) {
			for(Cell c : newDistrict.getCells()) {
				c.setDistrict(null);
				c.removeItem();
			}
			districts.remove(district);
			if(district.getPlayer() instanceof AI) {
				((AI) district.getPlayer()).removeDistrict(district);
			}
		}
		else {
			generateCapital(newDistrict);
			districts.add(newDistrict);
			if(district.getPlayer() instanceof AI) {
				((AI) district.getPlayer()).addDistrict(newDistrict);
			}
		}
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
					for(Cell c : getNeighbors(board[i][j])) { // on considère que la cellule est un district pour obtenir toutes les cellules l'entourant
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
		int i = rand.nextInt(district.getCells().size());
		while(district.getCells().get(i).getItem() != null && visited.size() < district.getCells().size()) {
			visited.add(district.getCells().get(i));
			i = rand.nextInt(district.getCells().size());
		}
		if(visited.size() == district.getCells().size()) {
			for(Cell c : district.getCells()) {
				if(c.getItem() instanceof Tree) {
					i = district.getCells().indexOf(c);
				}
			}
		}
		Cell cell = district.getCells().get(i);
		district.addCapital(cell);
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
		memories.get(memories.size()-1).add(new Memory(cell, cell.getItem(), cell.getDistrict(), selectedCell));
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
		else if(cell != null && cell.getDistrict() != null && cell.getDistrict().getPlayer() == players.get(activePlayer)){
			selectedCell = cell;
		}
	}
	
	public void undo() {
		if(memories.size() > 0) {
			ArrayList<Memory> memory = memories.get(memories.size()-1);
			Cell cell, lastCell;
			Item lastItem, currentItem;
			District lastDistrict;
			for(Memory mem : memory) {
				cell = mem.getCell();
				currentItem = cell.getItem();
				lastItem = mem.getLastItem();
				lastDistrict = mem.getLastDistrict();
				lastCell = mem.getLastCell();
				cell.getDistrict().removeCell(cell);
				cell.setDistrict(lastDistrict);
				cell.setItem(lastItem);
				if(lastCell != null) {
					lastCell.setItem(currentItem);
				}
				if(lastDistrict != null) {
					lastDistrict.addCell(cell);
					checkMerge(cell);
					checkSplit(cell);
				}
			}
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
		
		//TO REDO
		int[] nCells = new int[players.size()];
		int playerNumber;
		Player currentPlayer;
		for(District district : districts) {
			playerNumber = getIndex(district.getPlayer());
			currentPlayer = players.get(playerNumber);
			nCells[playerNumber] += district.getCells().size();
			if(nCells[playerNumber] > rows*columns/100*80) {
				winner = currentPlayer;
				break;
			}
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
	
	private int getIndex(Player player) {
		for(int i=0; i<players.size(); i++) {
			if(players.get(i) == player) {
				return i;
			}
		}
		return -1;
	}
	
	private void checkDistricts() {
		ArrayList<District> emptyDistricts = new ArrayList<>();
		for(District district : districts) {
			if(district.getCells().size() <= 1) {
				emptyDistricts.add(district);
				for(Cell c : district.getCells()) {
					c.setDistrict(null);
					c.removeItem();
				}
				if(district.getPlayer() instanceof AI) {
					((AI)district.getPlayer()).removeDistrict(district);
				}
			}
		}
		districts.removeAll(emptyDistricts);
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
}
