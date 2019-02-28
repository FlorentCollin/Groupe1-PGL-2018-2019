package logic.player.ai;

import java.util.ArrayList;
import java.util.Random;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;

public abstract class AbstractStrategy implements Strategy {
	protected enum Actions {};
	protected Random rand = new Random();
	
	public AbstractStrategy() {
		// TODO Auto-generated constructor stub
	}
	
	protected int sommeSalary(Cell cell) {
		District district = cell.getDistrict();
		int sold = 0;
		for(Cell c : district.getCells()) {
			if(c.getItem() != null) {
				sold += c.getItem().getSalary();
			}
		}
		return sold;
	}
	
	protected int nCells(Cell cell) {
		return cell.getDistrict().getCells().size();
	}

	@Override
	public void play(Board board, ArrayList<District> districts) {
		// TODO Auto-generated method stub

	}

}
