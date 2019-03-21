package logic.naturalDisasters;

import java.util.ArrayList;

import logic.board.Board;
import logic.board.cell.Cell;
import logic.item.Tree;
import logic.item.TreeOnFire;

public class ForestFire extends NaturalDisasters{
	ArrayList<Cell> treesOnFire;

	public ForestFire(Board board) {
		super(board);
		treesOnFire = new ArrayList<>();
	}
	
	private void fire() {
		affectedCells.clear();
		ArrayList<Cell> treeCells = board.getTreeCells();
		if(treeCells.size() > 0 && rand.nextInt(101)>getProba()) {
			Cell tree = treeCells.get(rand.nextInt(treeCells.size()));
			destroyTreeFrom(tree);
		}
		modificatedCells.put(board.getTurn(), affectedCells);
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
	
	private void wasOnFire() {
		ArrayList<Cell> treesOnFire = modificatedCells.get(board.getActivePlayer());
		if(treesOnFire != null) {
			for(Cell treeOnFire : treesOnFire) {
				treeOnFire.removeItem();
			}
		}
	}
	
	@Override
	public void play() {
		wasOnFire();
		fire();
	}
}
