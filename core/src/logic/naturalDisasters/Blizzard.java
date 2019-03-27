package logic.naturalDisasters;

import java.util.ArrayList;
import java.util.HashMap;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.BlizzardCell;
import logic.board.cell.Cell;
import logic.item.Capital;
import logic.item.Item;
import logic.item.Soldier;
import logic.item.level.Level;
import logic.item.level.SoldierLevel;

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
//		blizzardFrom(getAnyCell());
		destroy(getAnyCell());
		saveChanges();
	}
	
	private void blizzardFrom(Cell cell) {
		nAffectedCells ++;
		affectedCells.add(cell);
		Item item = cell.getItem();
		District district = cell.getDistrict();
		if(district != null) {
			district.removeCell(cell);
		}
		cell = new BlizzardCell(cell.getX(), cell.getY());
		cell.setItem(item);
		cell.setDistrict(district);
		if(district != null) {
			district.addCell(cell);
		}
		if(item instanceof Capital) {
			district.addCapital(cell);
		}
		board.addModification(cell);
		board.setCell(cell);
		if(nAffectedCells < getMaxAffectedCells() && mustHappen(50)) {
			Cell c = getOneFrom(board.getNeighbors(cell));
			if(c != null) {
				blizzardFrom(c);
			}
		}
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
		System.out.println("blizzard proba : "+getProba());
		if(mustHappen(getProba())) {
			blizzard();
		}
	}
}
