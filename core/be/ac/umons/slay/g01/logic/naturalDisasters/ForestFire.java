package ac.umons.slay.g01.logic.naturalDisasters;

import java.util.ArrayList;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.item.Tree;
import ac.umons.slay.g01.logic.item.TreeOnFire;

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
	protected void cancel(){
		ArrayList<Integer> keysToDelete = new ArrayList<>();
		for(int key : modificatedCells.keySet()) {
			if(board.getTurn() - key > getDuration()) {	
				keysToDelete.add(key);
				for(Cell cell : modificatedCells.get(key)) {
					cell.removeItem();
				}
			}
		}
		for(int key : keysToDelete) {
			modificatedCells.remove(key);
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
