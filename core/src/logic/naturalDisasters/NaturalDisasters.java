package logic.naturalDisasters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.board.cell.LandCell;
import logic.board.cell.LavaCell;
import logic.board.cell.WaterCell;

public class NaturalDisasters {
	private int proba;
	private int duration;
	protected int nAffectedCells;
	private int maxAffectedCells;
	protected Random rand = new Random();
	protected transient Board board;
	
	protected ArrayList<Cell> affectedCells;
	protected HashMap<Integer, ArrayList<Cell>> modificatedCells;


	public NaturalDisasters(Board board) {
		affectedCells = new ArrayList<>();
		modificatedCells = new HashMap<>();
		this.board = board;
		duration = 1;
		proba = 50;
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

	public ArrayList<Cell> getAffectedCells() {
		return affectedCells;
	}

	public void setAffectedCells(ArrayList<Cell> affectedCells) {
		this.affectedCells = affectedCells;
	}
	
	protected boolean mustHappen(int x) {
		return rand.nextInt(101) < x;
	}
	
	protected Cell getAnyCell() {
		int i;
		int j;
		do {
			i = rand.nextInt(board.getColumns());
			j = rand.nextInt(board.getRows());
		}while(board.getCell(i, j) instanceof WaterCell);
		return board.getCell(i, j);
	}
	
	protected Cell getOneFrom(ArrayList<Cell> cells) {
		Cell cell = null;
		if(cells.size() > 0) {
			do {
				cell = cells.get(rand.nextInt(cells.size()));
			}while(! (cell instanceof LandCell));
		}
		return cell;
	}
	
	protected void destroy(Cell cell, boolean mustBeWater) {
		nAffectedCells ++;
		affectedCells.add(cell);
		if(cell.getDistrict() != null) {
			cell.getDistrict().removeCell(cell);
		}
		if(mustBeWater) {
			cell = new WaterCell(cell.getX(), cell.getY());
		}
		else {
			cell = new LavaCell(cell.getX(), cell.getY());
		}
		board.addModification(cell);
		board.setCell(cell);
		board.checkSplit(cell);
		if(nAffectedCells < getMaxAffectedCells() && mustHappen(50)) {
			Cell c = getOneFrom(board.getNeighbors(cell));
			if(c != null) {
				destroy(c, mustBeWater);
			}
		}
	}
	
	public void play() {
		
	}
	
	protected void cancel() {
		ArrayList<Integer> keysToDelete = new ArrayList<>();
		for(int key : modificatedCells.keySet()) {
			if(board.getTurn() - key > getDuration()) {	
				for(Cell c : modificatedCells.get(key)) {
					c = new LandCell(c.getX(), c.getY());
					board.setCell(c);
					board.addModification(c);
				}
				keysToDelete.add(key);
			}
		}
		//Réduire la taille de modificatedCells
		for(int key : keysToDelete) {
			modificatedCells.remove(key);			
		}
	}
	
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
