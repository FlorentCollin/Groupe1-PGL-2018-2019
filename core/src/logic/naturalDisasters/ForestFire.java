package logic.naturalDisasters;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.cell.Cell;

public class ForestFire extends NaturalDisasters{

	public ForestFire(Board board) {
		super(board);
		// TODO Auto-generated constructor stub
	}
	
	private void fire() {
		affectedCells.clear();
		ArrayList<Cell> treeCells = board.getTreeCells();
		affectedCells.add(treeCells.get(rand.nextInt(treeCells.size())));
	}
	
	@Override
	public void play() {
		fire();
	}
}
