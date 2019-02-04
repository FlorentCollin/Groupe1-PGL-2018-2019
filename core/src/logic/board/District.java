package logic.board;

import java.util.ArrayList;

import logic.board.cell.Cell;
import logic.item.Capital;
import logic.item.Item;
import logic.item.Soldier;
import logic.item.Tree;
import logic.player.Player;

public class District {
	private Player player;
	private int gold;
	private Capital capital;
	private ArrayList<Cell> cells;
	
	public District(Player player) {
		cells = new ArrayList<Cell>();
		this.player = player;
	}
	
	public void addCell(Cell cell) {
		cells.add(cell);
	}
	
	public void removeCell(Cell cell) {
		cells.remove(cells.indexOf(cell));
	}
	
	public void setCapital(Capital capital) {
		this.capital = capital;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/**
	 * Permet de calculer le revenu du district
	 * @return le revenu du district
	 * */
	public void calculateGold() {
		Item item;
		for(Cell cell : cells) {
			item = cell.getItem();
			setGold(getGold() + 1);
			if(item instanceof Soldier) {
				setGold(getGold() - ((Soldier) item).getLevel().getSalary());
				// Remise à zéro des soldats déplacé au tour précédent
				((Soldier) item).setHasMoved(false);
			}
			else if(item instanceof Tree) {
				setGold(getGold() - 1);
			}
		}
	}
	
	public ArrayList<Cell> getCells() {
		return this.cells;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}
	
}
