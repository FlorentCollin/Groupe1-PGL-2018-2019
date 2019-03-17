package logic.naturalDisasters;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.item.Tree;

public class ForestFire extends NaturalDisasters{

	public ForestFire(Board board) {
		super(board);
		// TODO Auto-generated constructor stub
	}
	
	private void fire() {
		affectedCells.clear();
		ArrayList<Cell> treeCells = board.getTreeCells();
		if(treeCells.size() > 0) {
			Cell tree = treeCells.get(rand.nextInt(treeCells.size()));
			destroyTreeFrom(tree);
		}
	}
	
	private void destroyTreeFrom(Cell tree) {
		tree.removeItem();
		for(Cell neighbour : board.getNeighbors(tree)) {
			if(neighbour.getItem() instanceof Tree) {
				destroyTreeFrom(neighbour);
			}
		}
	}
	
	@Override
	public void play() {
		fire();
	}
}
