package ac.umons.slay.g01.logic.naturalDisasters;

import java.util.ArrayList;
import java.util.HashMap;

import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.board.cell.Cell;
import ac.umons.slay.g01.logic.item.Soldier;
import ac.umons.slay.g01.logic.item.level.Level;
import ac.umons.slay.g01.logic.item.level.SoldierLevel;

public class Blizzard extends NaturalDisasters{
	private HashMap<Integer, Integer> durationMap;
	private HashMap<Level, Integer> deadProb;

	public Blizzard(Board board) {
		super(board);
		durationMap = new HashMap<>();
		deadProb = new HashMap<>();
		generateDeadProb();
		setMaxAffectedCells(10);
	}
	
	private void generateDeadProb() {
		int prob = 70;
		for(Level level : SoldierLevel.values()) {
			deadProb.put(level, prob);
			prob -= 20;
		}
	}
	
	private void blizzard() {
		int proba = rand.nextInt(4)+1; //proba compris entre 1 et 4
		durationMap.put(board.getTurn(), proba);
		affectedCells.clear();
		nAffectedCells = 0;
		destroy(getAnyCell());
		saveChanges();
	}
	
	private void kill() {
		Level level;
		for(ArrayList<Cell> cells : modificatedCells.values()) {
			for(Cell cell : cells) {
				if(cell.getItem() instanceof Soldier) {
					level = cell.getItem().getLevel();
					if(mustHappen(deadProb.get(level))) {
						cell.removeItem();
					}
				}
			}
		}
	}
	
	@Override
	public void play() {
		kill();
		for(int key : durationMap.keySet()) { // car la proba est variable
			setDuration(durationMap.get(key));
			cancel();
		}
		if(mustHappen(getProba())) {
			blizzard();
		}
	}
}
