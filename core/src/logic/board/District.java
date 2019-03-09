package logic.board;

import logic.board.cell.Cell;
import logic.item.*;
import logic.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class District {
	private Player player;
	private int gold;
	private transient Cell capital;
	private List<Cell> cells;

	public static int globalId = 0;
	private int id;

	public District(Player player) {
		cells = new ArrayList<>();
		this.player = player;

		globalId++;
		id = globalId;
	}

	public void addCell(Cell cell) {
		if(cells.indexOf(cell) == -1) {
			cells.add(cell);
			cell.setDistrict(this); // Mise à jour du district pour la cellule ajoutée
		}
	}

	//TODO supprimer cette méthode
	/**
	 * Permet d'ajouter au district toutes les cellules d'un autre district
	 * @param district le district dont on souhaite obtenir les cellules
	 * */
	public void addAll(District district) {
		for(Cell cell : district.getCells()) {
			addCell(cell);
		}
	}
	
	public void removeCell(Cell cell) {
		cells.remove(cells.indexOf(cell));
		if(cell == capital) {
			removeCapital();
		}
	}

	public void removeAll(District district) {
        synchronized (cells) {
            cells.removeAll(district.getCells());
        }
	}

	public void removeSoldiers() {
	    synchronized (cells) {
            for(Cell c : cells) {
                if(c.getItem() instanceof Soldier) {
                    c.setItem(new Tomb());
                }
            }
        }
	}
	
	public void refreshSoldiers() {
		for(Cell c : cells) {
			if(c.getItem() != null && c.getItem().isMovable()) {
				c.getItem().setHasMoved(false);
			}
		}
	}
	

	public void addCapital(Cell cell) {
		if(cells.indexOf(cell) >= 0 && capital == null) { // On vérifie que la cellule appartient bien au district
			cell.setItem(new Capital());
			capital = cell;
		}
	}

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
	 * @return le revenu du district
	 * */
	public void calculateGold() {
		Item item;
		synchronized (cells) {
            for(Cell cell : cells) {
                item = cell.getItem();
                setGold(getGold() + 1);
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

    public int getId() {
        return id;
    }
    
    public int size() {
    	return cells.size();
    }
}
