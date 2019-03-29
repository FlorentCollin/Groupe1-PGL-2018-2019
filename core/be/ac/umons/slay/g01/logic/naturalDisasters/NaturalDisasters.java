package ac.umons.slay.g01.logic.naturalDisasters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.District;
import ac.umons.slay.g01.logic.board.cell.BlizzardCell;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.board.cell.DroughtCell;
import ac.umons.slay.g01.logic.board.cell.LandCell;
import ac.umons.slay.g01.logic.board.cell.LavaCell;
import ac.umons.slay.g01.logic.board.cell.WaterCell;
import ac.umons.slay.g01.logic.item.Capital;
import ac.umons.slay.g01.logic.item.Item;
import ac.umons.slay.g01.logic.item.TreeOnFire;

public abstract class NaturalDisasters {
	private int proba = 50;
	private int duration = 1;
	protected int nAffectedCells;
	private int maxAffectedCells = 1;
	protected Random rand = new Random();
	protected transient Board board;
	
	protected ArrayList<Cell> affectedCells;
	protected HashMap<Integer, ArrayList<Cell>> modificatedCells;


	public NaturalDisasters(Board board) {
		affectedCells = new ArrayList<>();
		modificatedCells = new HashMap<>();
		this.board = board;
	}

	public int getProba() {
		return proba;
	}

	public void setProba(int proba) {
		this.proba = proba;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration * board.getPlayers().size();
	}

	/**
	 * Permet de savoir si quelque chose doit se produire
	 * @param x la probabilité
	 * @return true si quelque chose doit se passer
	 * 			false sinon
	 */
	protected boolean mustHappen(int x) {
		return rand.nextInt(101) < x;
	}
	
	/**
	 * Permet de récupérer une cellule
	 * @return une cellule du board
	 */
	protected Cell getAnyCell() {
		int i = 0;
		int j = 0;
		if(this instanceof VolcanicEruption) {
			do {
				i = rand.nextInt(board.getColumns());
				j = rand.nextInt(board.getRows());
			}while((board.getCell(i, j) instanceof WaterCell));
		}
		else if(this instanceof Blizzard) {
			do {
				i = rand.nextInt(board.getColumns());
				j = rand.nextInt(board.getRows());
			}while(!(board.getCell(i, j) instanceof LandCell || board.getCell(i, j) instanceof WaterCell));
		}
		else if(this instanceof Drought) {
			do {
				i = rand.nextInt(board.getColumns());
				j = rand.nextInt(board.getRows());
			}while(!(board.getCell(i, j) instanceof LandCell || board.getCell(i, j) instanceof LavaCell));
		}
		return board.getCell(i, j);
	}
	
	/**
	 * Permet de récupérer une cellule depuis une liste de cellules
	 * @param cells la liste contenant les cellules
	 * @return une cellule de cette liste ou null
	 */
	protected Cell getOneFrom(ArrayList<Cell> cells) {
		ArrayList<Cell> visited = new ArrayList<Cell>();
		Cell cell = null;
		if(cells.size() > 0) {
			if(this instanceof Blizzard) {
				do {
					cell = cells.get(rand.nextInt(cells.size()));
					if(visited.indexOf(cell) == -1) {
						visited.add(cell);
					}
					if(visited.size() == cells.size()) {
						cell = null;
						break;
					}
				}while(!(cell instanceof LandCell || cell instanceof WaterCell));
			}
			else if(this instanceof Drought) {
				do {
					cell = cells.get(rand.nextInt(cells.size()));
					if(visited.indexOf(cell) == -1) {
						visited.add(cell);
					}
					if(visited.size() == cells.size()) {
						cell = null;
						break;
					}
				}while(!(cell instanceof LandCell));
			}
			else if(this instanceof VolcanicEruption) {
				do {
					cell = cells.get(rand.nextInt(cells.size()));
					if(visited.indexOf(cell) == -1) {
						visited.add(cell);
					}
					if(visited.size() == cells.size()) {
						cell = null;
						break;
					}
				}while(!(cell instanceof LandCell || cell instanceof DroughtCell));
			}
		}
		return cell;
	}
	
	/**
	 * Produit un désastre sur la plateau
	 * @param cell la cellule de départ du désastre
	 */
	protected void destroy(Cell cell) {
		nAffectedCells ++;
		if(cell.getItem() instanceof Capital) {
			cell.getDistrict().removeCapital();
		}
		if(cell.getDistrict() != null) {
			cell.getDistrict().removeCell(cell);
		}
		if(this instanceof Tsunami) {
			cell = new WaterCell(cell.getX(), cell.getY());
		}
		else if(this instanceof VolcanicEruption) {
			cell = new LavaCell(cell.getX(), cell.getY());
		}
		else {
			Item item = cell.getItem();
			District district = cell.getDistrict();
			if(this instanceof Drought) {
				cell = new DroughtCell(cell.getX(), cell.getY());
			}
			else if(this instanceof Blizzard) {
				cell = new BlizzardCell(cell.getX(), cell.getY());
			}
			cell.setItem(item);
			cell.setDistrict(district);
			if(district != null) {
				district.addCell(cell);
			}
			if(item instanceof Capital) {
				district.addCapital(cell);
			}
		}
		board.setCell(cell);
		affectedCells.add(cell);
		board.addModification(cell);
		checkDistricts();
		board.checkDistricts();
		board.checkSplit(cell);
		if(nAffectedCells < getMaxAffectedCells() && mustHappen(50)) {
			Cell c = getOneFrom(board.getNeighbors(cell));
			if(c != null) {
				destroy(c);
			}
		}
	}
	
	public void play() {
		
	}
	
	/**
	 * Permet de vérifier les districts
	 */
	protected void checkDistricts() {
		ArrayList<Cell> toRemove = new ArrayList<>();
		for(District district : board.getDistricts()) {
			toRemove.clear();
			for(Cell cell :  district.getCells()) {
				if(cell.getDistrict() == null) {
					toRemove.add(cell);
				}
			}
			district.getCells().removeAll(toRemove);
		}
	}
	
	/**
	 * Remet les cellules à neuf qaund le désastre ne se produit plus
	 */
	protected void cancel() {
		ArrayList<Integer> keysToDelete = new ArrayList<>();
		Item item;
		District district;
		for(int key : modificatedCells.keySet()) {
			if(board.getTurn() - key > getDuration()) {	
				for(Cell cell : modificatedCells.get(key)) {
					item = cell.getItem();
					district = cell.getDistrict();
					if(district != null) {
						district.removeCell(cell);
					}
					cell = new LandCell(cell.getX(), cell.getY());
					cell.setDistrict(district);
					if(district != null) {
						district.addCell(cell);
					}
					if(!(item instanceof TreeOnFire)) {
						cell.setItem(item);
					}
					if(item instanceof Capital) {
						district.addCapital(cell);
					}
					board.setCell(cell);
					if(cell.getDistrict() != null) {
						board.checkMerge(cell);
					}
					board.addModification(cell);
				}
				keysToDelete.add(key);
			}
		}
		//Réduire la taille de modificatedCells
		for(int key : keysToDelete) {
			modificatedCells.remove(key);			
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Permet de sauvegarder les modifications affectuées
	 */
	protected void saveChanges() {
		ArrayList<Cell> save = (ArrayList<Cell>) affectedCells.clone();
		modificatedCells.put(board.getTurn(), save);
	}

	public int getMaxAffectedCells() {
		return maxAffectedCells;
	}

	public void setMaxAffectedCells(int maxAffectedCells) {
		this.maxAffectedCells = maxAffectedCells;
	}
	
	/**
	 * @return les cellules voisines aux cellules d'eau
	 */
	protected ArrayList<Cell> getNeighboursWaterCells(){
		ArrayList<Cell> neighbours = new ArrayList<>();
		for(Cell waterCell : board.getWaterCells()) {
			for(Cell nc : board.getNeighbors(waterCell)) {
				if(nc instanceof LandCell && neighbours.indexOf(nc) == -1) {
					neighbours.add(nc);
				}
			}
		}
		return neighbours;
	}
	
}
