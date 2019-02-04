package logic.board;

import java.util.ArrayList;

import logic.board.cell.Cell;
import logic.item.Capital;
import logic.player.Player;

public class District {
	private Player player;
	private int gold,
				numbCell,
				numbTree;
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
	
	public void addTree() {
		this.numbTree += 1;
	}
	
	public void setCapital(Capital capital) {
		this.capital = capital;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/*
	 * Permet de calculer le revenu du district
	 * @return le revenu du district
	 * */
	public int calculateGold() {
		return 0;
	}
	
	public ArrayList<Cell> getCells() {
		return this.cells;
	}
	
}
