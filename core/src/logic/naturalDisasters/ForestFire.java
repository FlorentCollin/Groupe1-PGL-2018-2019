package logic.naturalDisasters;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.item.Tree;
import logic.item.TreeOnFire;

public class ForestFire extends NaturalDisasters{

	public ForestFire(Board board) {
		super(board);
	}
	
	private void fire() {
		affectedCells.clear();
		ArrayList<Cell> treeCells = board.getTreeCells();
		if(treeCells.size() > 0) {
			Cell tree = treeCells.get(rand.nextInt(treeCells.size()));
			destroyTreeFrom(tree);
			saveChanges();
		}
	}
	
	private void destroyTreeFrom(Cell tree) {
		tree.removeItem();
		tree.setItem(new TreeOnFire());
		affectedCells.add(tree);
		for(Cell neighbour : board.getNeighbors(tree)) {
			if(neighbour.getItem() instanceof Tree && !(neighbour.getItem() instanceof TreeOnFire)) {
				destroyTreeFrom(neighbour);
			}
		}
	}
	
	@Override
	public void play() {
		cancel();
		if(mustHappen(getProba())) {
			fire();
		}
	}
}
