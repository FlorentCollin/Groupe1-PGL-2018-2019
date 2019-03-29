package ac.umons.slay.g01.logic.board;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ac.umons.slay.g01.logic.board.cell.BlizzardCell;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.board.cell.LandCell;
import ac.umons.slay.g01.logic.item.Capital;
import ac.umons.slay.g01.logic.item.Item;
import ac.umons.slay.g01.logic.item.Soldier;
import ac.umons.slay.g01.logic.item.Tomb;
import ac.umons.slay.g01.logic.item.Tree;
import ac.umons.slay.g01.logic.player.Player;

/**
 * Classe représentant un district.
 * Un district est un territoire qui appartient à un joueur.
 * Si deux districts différents qui appartiennent au même joueur se touchent alors ceux-ci
 * sont fusionnés.
 */
public class District {
	private Player player;
	private int gold;
	private transient Cell capital;
	private CopyOnWriteArrayList<Cell> cells;


	public District(Player player) {
		cells = new CopyOnWriteArrayList<>();
		this.player = player;

	}

	/**
	 * Permet d'ajouter une cellule au district
	 * @param cell la cellule à ajouter
	 */
	public void addCell(Cell cell) {
		if(cells.indexOf(cell) == -1) {
			cells.add(cell);
			cell.setDistrict(this); // Mise à jour du district pour la cellule ajoutée
		}
	}

	/**
	 * Permet d'ajouter au district toutes les cellules d'un autre district
	 * @param district le district dont on souhaite obtenir les cellules
	 * */
	public void addAll(District district) {
		for(Cell cell : district.getCells()) {
			addCell(cell);
		}
	}
	
	/**
	 * Permet de retirer une cellule du district
	 * @param cell la cellule à retirer
	 */
	public void removeCell(Cell cell) {
		cells.remove(cell);
		if(cell == capital) {
			removeCapital();
		}
	}

	/**
	 * Méthode qui permet de supprimer toutes les cellules d'un district
	 * @param district le district duquel il faut supprimer toutes les cellules
	 */
	public void removeAll(District district) {
        synchronized (cells) {
            cells.removeAll(district.getCells());
        }
	}

	/**
	 * Méthode qui permet de supprimer un district
	 */
	public void delete() {
		for(Cell cell : cells) {
			cell.removeDistrict();
			cell.removeItem();
		}
	}

	/**
	 * Méthode qui permet de supprimer tous les soldats d'un district et de les remplacer par une tombe
	 * Cette méthode est appelé lorsqu'un district est en faillit
	 */
	public void removeSoldiers() {
	    synchronized (cells) {
            for(Cell c : cells) {
                if(c.getItem() instanceof Soldier) {
                    c.setItem(new Tomb());
                }
            }
        }
	}

	/**
	 * Méthode qui permet de remettre les soldats dans un état où ils peuvent se déplacer sur la map
	 */
	public void refreshSoldiers() {
		for(Cell c : cells) {
			if(c.getItem() != null && c.getItem().isMovable()) {
				if(!(c instanceof BlizzardCell))
					c.getItem().setHasMoved(false);
				else
					c.getItem().setHasMoved(true);
			}
		}
	}
	
	/**
	 * Permet d'ajouter la capital au district
	 * @param cell la cellule contenant la capital
	 */
	public void addCapital(Cell cell) {
		if(cells.indexOf(cell) >= 0 && capital == null) { // On vérifie que la cellule appartient bien au district
			cell.setItem(new Capital());
			capital = cell;
		}
	}
	
	/**
	 * Permet de retirer la capital
	 */
	public void removeCapital() {
		if(capital != null) {
			capital.removeItem();
			capital = null;
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Permet de calculer le revenu du district
	 * */
	public void calculateGold() {
		Item item;
		synchronized (cells) {
            for(Cell cell : cells) {
            	item = cell.getItem();
            	if(cell instanceof LandCell) {
	                setGold(getGold() + 1);
            	}
                if(item instanceof Soldier) {
                    setGold(getGold() - ((Soldier) item).getLevel().getSalary());
                }
                else if(item instanceof Tree) {
                    setGold(getGold() - 1);
                }
            }
        }
	}

	public synchronized List<Cell> getCells() {
		return cells;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public void addGold(int gold) {
		this.gold += gold;
	}

	public Cell getCapital() {
		return capital;
	}

    public int size() {
    	return cells.size();
    }
    
    /**
     * Permet de faire une vérification sur les cellules contenues dans le district
     */
    public void check() {
    	ArrayList<Cell> toRemove = new ArrayList<>();
    	for(Cell cell : cells) {
    		if(cell.getDistrict() == null) {
    			toRemove.add(cell);
    			cell.removeItem();
    		}
    	}
    	cells.removeAll(toRemove);
    }
}
