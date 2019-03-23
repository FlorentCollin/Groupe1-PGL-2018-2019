package logic.board;

import logic.allianceController.AllianceController;
import logic.board.cell.BlizzardCell;
import logic.board.cell.Cell;
import logic.board.cell.LandCell;
import logic.board.cell.WaterCell;
import logic.item.DestroyableItem;
import logic.item.Item;
import logic.item.Soldier;
import logic.item.Tree;
import logic.naturalDisasters.naturalDisasterscontroller.NaturalDisastersController;
import logic.player.Player;
import logic.player.ai.AI;
import logic.player.ai.strategy.Strategy;
import logic.shop.Shop;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Classe représentant un plateau de jeu.
 */
public class Board{
	private transient Cell[][] board;
	private int columns, rows, activePlayer;
	private CopyOnWriteArrayList<Player> players;
	private CopyOnWriteArrayList<District> districts;
	private Shop shop;
	private boolean naturalDisasters;
	private transient NaturalDisastersController naturalDisastersController;
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
	private ArrayList<Cell> modificatedCells;
	private transient ArrayList<Cell> waterCells;
	private transient ArrayList<Cell> treeCells;
	private int turn = 0;
	private AllianceController alliances;

	//Vérifier où on appelle checkDistricts() !!!!!

	public Board(int columns, int rows, CopyOnWriteArrayList<Player> players, boolean naturalDisasters, Shop shop){
		this(columns, rows, players, shop);
		this.naturalDisasters = naturalDisasters;
		if (naturalDisasters) {
			naturalDisastersController = new NaturalDisastersController(this);
		}
	}

	public Board(int columns, int rows, CopyOnWriteArrayList<Player> players, Shop shop) {
		this.columns = columns;
		this.rows = rows;
		board = new Cell[columns][rows];
		this.players = players;
		this.districts = new CopyOnWriteArrayList<>();
		this.shop = shop;
		fullIn();
		waterCells = new ArrayList<>();
		treeCells = new ArrayList<>();
		modificatedCells = new ArrayList<>();
		for(District district : districts) {
			if(district.getPlayer() == players.get(0)) {
				district.calculateGold();
			}
		}
		alliances = new AllianceController(this);
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

	public void setCell(Cell cell) {
		System.out.println("setCell for cell in "+cell.getX()+", "+cell.getY()+" : "+cell.getClass().getSimpleName());
		board[cell.getX()][cell.getY()] = cell;
		Cell cell2 = board[cell.getX()][cell.getY()];
		System.out.println("setCell for cell2 in "+cell2.getX()+", "+cell2.getY()+" : "+cell2.getClass().getSimpleName());
	}

	/**
	 * Permet de changer un joueur en une IA
	 * @param nPlayer le numéro du joueur à changer en ia
	 * @param strategy la stratégie qu'utilise l'ia
	 */
	public void changeToAI(int nPlayer, Strategy strategy) {
		AI ai = new AI(strategy, this);
		ai.setId(players.get(nPlayer).getId());
		ai.setName("AI#" + nPlayer);
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
		waterCells.add(board[i][j]);
	}

	/**
	 * Permet de changer une cellule d'eau en une cellule neutre
	 * @param i la position en x
	 * @param j la position en y
	 */
	public void changeToLandCell(int i, int j) {
		waterCells.remove(board[i][j]);
		board[i][j] = new LandCell(i, j);
	}

	public void setShopItem(Item item) {
		if(selectedCell != null) {
			shop.setSelectedItem(item, selectedCell.getDistrict());
		}
		hasChanged = true;
	}

	/**
	 * Permet de placer un item sur une cellule du plateau
	 * @param cell la cellule sur laquelle placer le nouvel item
	 * */
	public void placeNewItem(Cell cell){
		Item cellItem = cell.getItem();
		Item shopItem = shop.getSelectedItem();
		if(isInPossibleMove(possibleMove(selectedCell.getDistrict()), cell)) {
			if(cellItem == null) { // Si cell ne contient aucun item on peut toujours se placer dessus
				conquerForNewItem(cell);
			}
			else if(isOnOwnTerritory(cell)) { // Si la cellule appartient déjà au joueur
				// Il faut que l'item soit de même instance que celui à ajouter
				// Mais aussi de même niveau et non maxé
				// Ainsi on peut améliorer
				if(cellItem.isImprovable() && sameInstance(cellItem, shopItem)) {
					fusionForNewItem(cell);
				}
				else if(cellItem instanceof DestroyableItem) {
					selectedCell.getDistrict().addGold(((DestroyableItem) cellItem).getBonus());
					updateCellForNewItem(cell);
				}
			}
			// Le joueur souhaite se placer sur une case ennemie non vide
			// L'item de la case doit être de niveau inférieur ou égal à celui que l'on souhaite placer
			else {
				// Si la cellule ennemi contient une capitale il faut régénérer une capitale pour le district de cette cellule
				if(cellItem.getLevel() == null) {
					conquerForNewItem(cell);
				}
				// La cellule contient un arbre ou l'item est de niveau inférieur à celui que va être placé
				else if(shopItem.isStronger(cellItem) || shopItem.isEqual(cellItem)) {
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
		if(cell.getDistrict() == null) {
			System.out.println(cell.getX()+", "+cell.getY());
		}
		shop.buy(cell.getDistrict());
		modificatedCells.add(cell);
		checkDistricts();
	}

	/**
	 * Permet de déplacer un item d'une cellule à l'autre
	 * @param toCell la cellule de destination
	 * */
	public void move(Cell toCell) {
		Item cellItem = toCell.getItem();
		Item selectedItem = selectedCell.getItem();
		if(isInPossibleMove(possibleMove(selectedCell), toCell)) {
			if(cellItem == null) {
				conquer(toCell);
			}
			else if(isOnOwnTerritory(toCell)) {
				if(cellItem.isImprovable() && sameInstance(cellItem, selectedItem)) {
					fusion(toCell);
				}
				else if(cellItem instanceof DestroyableItem) {
					toCell.getDistrict().addGold(((DestroyableItem) cellItem).getBonus());
					updateCell(toCell);
				}
			}
			else {
				if(toCell.getItem().getLevel() == null) {
					conquer(toCell);
				}
				else if(selectedItem.isStronger(cellItem) || selectedItem.isEqual(cellItem)){
					conquer(toCell);
				}
			}
		}
	}

	/**
	 * Permet de fusionner des items
	 * @param cell la cellule sur laquelle la fusion doit être faîte
	 * */
	private void fusion(Cell cell) {
		cell.getItem().improve();
		selectedCell.removeItem();
	}

	/**
	 * Permet de fusionner des items lors de l'ajout d'un nouvel item
	 * @param cell la cellule sur laquelle se trouve un item et qu'un nouvel item vient d'être placé
	 * */
	private void fusionForNewItem(Cell cell) {
		cell.getItem().improve();
		shop.buy(cell.getDistrict());
	}

	/**
	 * Permet de savoir si une cellule appartient à un joueur
	 * @param cell la cellule à tester
	 * @return true si la cellule appartient au joueur qui est entrain de jouer le tour
	 * 		   false sinon
	 * */
	private boolean isOnOwnTerritory(Cell cell) {
		if(isNeutral(cell)) {
			return false;
		}
		else {
			return cell.getDistrict().getPlayer() == getActivePlayer();
		}
	}

	/**
	 * Permet de savoir si une cellule n'appartient à aucun joueur
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
	 * @param item1 le premier item
	 * @param item2 le deuxième item
	 * @return true si les items sont identiques
	 * 			false sinon
	 * */
	private boolean sameInstance(Item item1, Item item2) {
		if(item1.getLevel() != null) {
			return item1.getClass().isInstance(item2) && item1.getLevel() == item2.getLevel();
		}
		return item1.getClass().isInstance(item2);
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
			int i = 1;
			Item item = cell.getItem();
			while(item != null && i < item.getMaxMove()) {
				subAround.clear();
				for (Cell c : around) {
					if (c.getDistrict() == cell.getDistrict()
							&& (c.getItem() == null
							|| (sameInstance(c.getItem(), cell.getItem())))
					) {
						subAround.addAll(getNeighbors(c));
					}
				}
				around.addAll(subAround);
				i++;
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
		for(Cell c : district.getCells()) {
			if(possible.indexOf(c) == -1 && canGoOn(c, shop.getSelectedItem())) {
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
	 * Permet de récupérer les cellules autour d'une cellule
	 * @param cell la cellule de base
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
	public boolean canGoOn(Cell cell, Item item) {
		Item cellItem = cell.getItem();
	
		// Si il n'y a aucun item il est toujours possible de se placer sur la case
//		if(alliances != null && cell.getDistrict() != null && cell.getDistrict().getPlayer() != selectedCell.getDistrict().getPlayer()) {
//			if(alliances.areAllied(cell.getDistrict().getPlayer(), selectedCell.getDistrict().getPlayer())) {
//				return false;
//			}
//		}
		for(Cell nb : getNeighbors(cell)) {
			if(nb.getDistrict() != null &&
					nb.getDistrict() != selectedCell.getDistrict() && nb.getItem() instanceof Soldier && nb.getItem().isStronger(item)) {
				return false;
			}
		}
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
			else if(item.isStronger(cellItem) || item.isEqual(cellItem)) {
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
		turn ++;
		hasChanged = true;
		if(naturalDisastersController != null) {
			naturalDisastersController.isHappening();
		}
		checkDistricts();
		checkWinner();
		setSelectedCell(null);
		shop.removeSelection();
		activePlayer = (activePlayer + 1)%(players.size());
		if(winner == null) {
			generateTree();
			for(District district : districts) {
				if(district.getPlayer() == getActivePlayer()) {
					district.calculateGold();
					if(district.getGold() < 0) {
						district.removeSoldiers();
					}
					else {
						district.refreshSoldiers();
						for (Cell c : district.getCells()) {
							if (c.getItem() != null && c.getItem().isMovable()) {
								c.getItem().setHasMoved(false);
							}
						}
					}
				}
			}
			if(players.get(activePlayer) instanceof AI) {
				((AI)players.get(activePlayer)).play();
			}
		}
		else {
			if(!(winner instanceof AI)) {
				System.out.println(winner+" wins");
			}
			else {
				System.out.println("You lose");
			}
		}
	}

	public Cell getCell(int i, int j) {
		return board[i][j];
	}

	public Cell getSelectedCell() {
		return selectedCell;
	}

	public void setSelectedCell(Cell selectedCell) {
		if(selectedCell == null || (selectedCell.getDistrict() != null && selectedCell.getDistrict().getPlayer().getId() == getActivePlayer().getId())) {
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
	}

	/**
	 * Permet de diviser un district en deux sous districts
	 * @param district le district à diviser
	 * */
	private void split(District district) {
		District newDistrict = new District(district.getPlayer());
		for (Cell c : district.getCells()) {
			visited.clear();
			firstCell = c;
			if (numberOfWayToCapital(c) == 0) {
				newDistrict.addCell(c);
				c.setDistrict(newDistrict);
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
	public void checkSplit(Cell cell) {
		for(Cell c : getNeighbors(cell)) {
			if(cell.getDistrict() == null && c.getDistrict() != null) { //cas d'un désastre naturel
				visited.clear();
				firstCell = c;
				if(numberOfWayToCapital(c) == 0) {
					split(c.getDistrict());
				}
			}
			else if(c.getDistrict() != null && c.getDistrict().getPlayer() != getActivePlayer()) {
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
						treeCells.add(board[i][j]);
					}
				}
			}
		}
	}

	/**
	 * Calcule la probabilité qu'un arbre apparaisse sur une cellule
	 * @param n le nombre d'arbres autour de la cellule
	 * @return la probabilité qu'un arbre apparaisse sur la cellule
	 * */
	private double calculateProb(int n) {
		if(n >= 0 && n <= 6) { // le nombre d'arbres au tour de la case doit être compris entre 0 et 6
			return 1/100f+(n*Math.log10(n+1))/10;
		}
		return 0;
	}

	/**
	 * Permet de créer une nouvelle capital sur un district
	 * @param district le district ayant besoin d'une nouvelle capitale
	 * */
	private int generateCapital(District district) {
		ArrayList<Cell> visited = new ArrayList<>();
		Random rand = new Random();
		//On récupère une cellule du district aléatoirement
		int i = rand.nextInt(district.getCells().size());
		while(district.getCells().get(i).getItem() != null && visited.size() < district.getCells().size()) {
			visited.add(district.getCells().get(i));
			i = rand.nextInt(district.getCells().size());
		}
		if(visited.size() == district.getCells().size()) {
			return 1;
		}
		Cell cell = district.getCells().get(i);
		district.addCapital(cell);
		return 0;
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
		modificatedCells.add(cell);
		checkDistricts();
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

	public CopyOnWriteArrayList<District> getDistricts() {
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

	public CopyOnWriteArrayList<Player> getPlayers() {
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
			setSelectedCell(null);
			shop.removeSelection();
		}
		else if(cell != null && cell.getDistrict() != null && cell.getDistrict().getPlayer() == players.get(activePlayer)){
			setSelectedCell(cell);
		}
	}

	/**
	 * Méthode qui vérifier si un joueur à gagné
	 */
	private void checkWinner() {
		ArrayList<Player> deadPlayers = new ArrayList<>();
		for(Player player : players) {
			if(!canYetPlay(player)) {
				deadPlayers.add(player);
			}
		}
		players.removeAll(deadPlayers);
		if(players.size() == 1 || !realPlayer()) {
			winner = players.get(0);
		}
	}

	/**
	 * Vérifie qu'il y a toujours au moins un joueur humain dans la partie
	 * @return true si il reste au moins un joueur humain
	 * 			false sinon
	 */
	private boolean realPlayer() {
		for(Player player : players) {
			if(!(player instanceof AI)) {
				return true;
			}
		}
		return false;
	}

	private boolean canYetPlay(Player player) {
		for(District district : districts) {
			if(district.getPlayer() == player) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Vérifie si un district est vide
	 */
	private void checkDistricts() {
		ArrayList<District> emptyDistricts = new ArrayList<>();
		for(District district : districts) {
			if (district.getCells().size() <= 1) {
				emptyDistricts.add(district);
				for (Cell c : district.getCells()) {
					modificatedCells.add(c);
				}
			}
		}
		for(District district : emptyDistricts) {
			removeDistrict(district);
			district.delete();
		}
		checkCapitals();
	}

	/**
	 * Vérifie que chaque district possède bien une capitale
	 * Si un district ne possède pas de capital une nouvelle est générer
	 */
	public void checkCapitals() {
		ArrayList<District> toRemove = new ArrayList<>();
		int returnValue;
		for(District district : districts) {
			if(district.getCapital() == null) {
				returnValue = generateCapital(district);
				if(returnValue == 1) {
					toRemove.add(district);
				}
			}
		}
		for(District district : toRemove) {
			for(Cell c : district.getCells()) {
				modificatedCells.add(c);
			}
			removeDistrict(district);
			district.delete();
		}
	}

	public int getActivePlayerNumber() {
		return activePlayer;
	}

	public boolean hasChanged() {
		boolean ret = hasChanged;
		hasChanged = false;
		return ret;
	}

	public void init() {
		board = new Cell[columns][rows];
		fullIn();
	}

	/**
	 * Méthode qui permet d'update le board. Cette méthode est utilisé par le client pour que l'interface graphique soit à jour
	 * @param districts les districts
	 * @param shopItem l'item contenu dans le shop
	 * @param players les joueurs
	 * @param activePlayer le numéro du joueur actif
	 */
	public void updateBoard(CopyOnWriteArrayList<District> districts, Item shopItem, CopyOnWriteArrayList<Player> players, int activePlayer) {
		this.districts = districts;
		this.shop.setSelectedItem(shopItem);
		this.players = players;
		this.activePlayer = activePlayer;
		for (District district : districts) {
			for (Cell cell : district.getCells()) {
				board[cell.getX()][cell.getY()] = cell;
				cell.setDistrict(district);
				modificatedCells.add(cell);
				if(cell.getItem() != null)
					cell.getItem().update();
			}
			for (int i = 0; i < players.size(); i++) {
				if (district.getPlayer().getId() == players.get(i).getId()) {
					players.set(i, district.getPlayer());
				}
			}
		}
		checkWinner();
	}

	public ArrayList<Cell> getWaterCells(){
		return waterCells;
	}

	public ArrayList<Cell> getModificatedCells(){
		return modificatedCells;
	}

	public Player getWinner() {
		return winner;
	}

	public void addTree(int i, int j) {
		treeCells.add(board[i][j]);
	}

	public ArrayList<Cell> getTreeCells(){
		return treeCells;
	}

	public void addModification(Cell cell) {
		modificatedCells.add(cell);
	}

	public int getTurn() {
		return turn;
	}

	public NaturalDisastersController getNaturalDisastersController() {
		return naturalDisastersController;
	}

	public boolean isNaturalDisasters() {
		return naturalDisasters;
	}
}
