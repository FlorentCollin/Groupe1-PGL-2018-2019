package logic.naturalDisasters;

import java.util.ArrayList;
import java.util.Random;

import logic.board.Board;
import logic.board.cell.Cell;

public abstract class NaturalDisasters {
	private int proba;
	private int	numberOfEffectedCells;
	private int duration;
	protected Random rand = new Random();
	protected Board board;
	
	protected ArrayList<Cell> affectedCells;
	
	public NaturalDisasters(Board board) {
		affectedCells = new ArrayList<>();
		this.board = board;
		duration = 1;
	}

	public int getProba() {
		return proba;
	}

	public void setProba(int proba) {
		this.proba = proba;
	}

	public int getNumberOfEffectedCells() {
		return numberOfEffectedCells;
	}

	public void setNumberOfEffectedCells(int numberOfEffectedCells) {
		this.numberOfEffectedCells = numberOfEffectedCells;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public ArrayList<Cell> getAffectedCells() {
		return affectedCells;
	}

	public void setAffectedCells(ArrayList<Cell> affectedCells) {
		this.affectedCells = affectedCells;
	}
	
	public void play() {
		
	}
	
}
